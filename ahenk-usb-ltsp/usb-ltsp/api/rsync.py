#!/usr/bin/python3
# -*- coding: utf-8 -*-

import json
import os
import re
import subprocess
import sys
import threading

from base.model.enum.content_type import ContentType
from base.model.enum.message_code import MessageCode
from base.model.enum.message_type import MessageType
from base.model.response import Response
from base.plugin.abstract_plugin import AbstractPlugin
from base.scope import Scope

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__))))


class BackupParser(AbstractPlugin):
    def __init__(self, logger, context):
        super(AbstractPlugin, self).__init__()
        scope = Scope.get_instance()
        self.context = context
        self.messenger = scope.get_messenger()
        self.messaging = scope.get_message_manager()

        self.dry_run_status = True
        self.percentage = None
        self.number_of_files = None
        self.number_of_transferred_files = None
        self.resuming = True
        self.estimated_time = 0
        self.total_file_size = None
        self.total_transferred_file_size = None
        self.transferred_file_size = 0
        self.logger = logger

    def parse_dry(self, line):
        if 'Number of files' in line:
            total_file = re.findall('Number of files: (\d+)', line)
            if total_file:
                self.number_of_files = str(total_file[0])
                self.update_last_status()
        elif 'Number of regular files transferred' in line:
            transfferd_file_size = re.findall('Number of regular files transferred: (\d+)', line)
            if transfferd_file_size:
                self.number_of_transferred_files = str(transfferd_file_size[0])
                self.update_last_status()
        elif 'Total file size' in line:
            file_size = re.findall('Total file size: (\d+)', line)
            if file_size:
                self.total_file_size = str(file_size[0])
                self.update_last_status()
        elif 'Total transferred file size' in line:
            transferred_file_size = re.findall('Total transferred file size: (\d+)', line)
            if transferred_file_size:
                self.total_transferred_file_size = str(transferred_file_size[0])
                self.update_last_status()

    def parse_sync(self, line):
        self.logger.debug('Rsync command results are parsing')

        line_as_arr = None
        try:
            line_as_arr = line.split()
        except:
            pass
        if len(line_as_arr) > 1 and b'%' in line:
            self.transferred_file_size = str(line_as_arr[0].decode('utf-8'))
            self.percentage = str(line_as_arr[1].decode('utf-8')).replace('%', '')
            self.estimated_time = str(line_as_arr[3].decode('utf-8'))

            if float(self.total_transferred_file_size) == 0:
                return -1
            else:
                transfer_range = float(
                    '{0:.2f}'.format(float(self.total_transferred_file_size) / float(self.total_file_size)))
                real_percentage = int(int(self.percentage) / transfer_range)

                if real_percentage == 100:
                    self.estimated_time = '0:00:00'

                self.send_processing_message(str(real_percentage), str(self.estimated_time))
                if real_percentage == 100:
                    return -1
        return 0

    def send_processing_message(self, percentage, time):
        try:
            data = {
                'percentage': str(percentage), 'estimation': str(time)
            }

            response = Response(type=MessageType.TASK_STATUS.value, id=self.context.get('taskId'),
                                code=MessageCode.TASK_PROCESSING.value,
                                data=json.dumps(data),
                                content_type=ContentType.APPLICATION_JSON.value)

            message = self.messaging.task_status_msg(response)
            Scope.get_instance().get_messenger().send_direct_message(message)
        except Exception as e:
            self.logger.error('A problem occurred while sending message. Error Message: {0}'.format(str(e)))

    def update_last_status(self):
        if not self.dry_run_status:
            self.percentage = str(100)
            self.estimated_time = '0:00:00'
            self.resuming = False


class BackupRsync(AbstractPlugin):
    def __init__(self, backup_data, context):
        super(BackupRsync, self).__init__()
        self.backup_data = backup_data
        self.context = context
        self.backup_result = {}
        self.parser = BackupParser(self.logger, context)

    def prepare_command(self):
        destination_path = self.backup_data['username'] + "@" + self.backup_data['destHost'] + ':' + self.backup_data[
            'destPath']
        path = self.backup_data['sourcePath'] + ' ' + destination_path
        options = ' -a --no-i-r --info=progress2 --stats --no-h '
        backup_command = 'rsync ' + options + ' ' + path
        self.logger.info(str(backup_command))
        return backup_command

    def dry_run(self):
        destination_path = self.backup_data['username'] + "@" + self.backup_data['destHost'] + ':' + self.backup_data[
            'destPath']

        path = self.backup_data['sourcePath'] + ' ' + destination_path
        options = ' -azn --stats --no-h '
        dry_run_backup_command = 'rsync ' + options + ' ' + path
        return dry_run_backup_command

    def execute_command(self, cmd):
        self.logger.debug('Backup command is executing. Command : {0}'.format(cmd))
        try:
            self.parser.resuming = True
            command_process = subprocess.Popen(cmd, shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                                               stderr=subprocess.PIPE)
            self.parser.set_process = command_process
            while self.parser.resuming:
                output = command_process.stdout.readline()
                if output == '' or command_process.poll() is not None:
                    self.parser.resuming = False
                    self.logger.info('#processed')
                if output:
                    if self.parser.parse_sync(output) == -1:
                        break
        except Exception as e:
            self.logger.error('A problem occurred while executing rsync command. Error Message: {0}'.format(str(e)))

    def execute_dry_run(self, cmd):

        process = subprocess.Popen(cmd, shell=True, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                                   stderr=subprocess.PIPE)

        p_out = process.stdout.read().decode("unicode_escape")

        for line in p_out.strip().split('\n'):
            self.parser.parse_dry(line)

    def get_resource_total_size(self, source_path):
        return os.stat(source_path).st_size

    def is_file(self, source_path):
        return os.path.isfile(source_path)

    def append_command_execution_type(self, cmd):
        return 'sshpass -p ' + self.backup_data['password'] + ' ' + cmd + ' | stdbuf -oL tr "\\r" "\\n"'

    def destination_confirm(self):
        self.execute_command(
            'sshpass -p {0} ssh {1}@{2} mkdir -p {3}'.format(self.backup_data['password'], self.backup_data['username'],
                                                             self.backup_data['destHost'],
                                                             os.path.dirname(self.backup_data['destPath'])))

    def backup(self):
        # Change status of parser and run dry run command for backup informations
        # self.parser.dry_run_status = True

        self.destination_confirm()
        dry_run_cmd = self.append_command_execution_type(self.dry_run())
        self.logger.debug('Dry run command:{0}'.format(dry_run_cmd))
        self.execute_dry_run(dry_run_cmd)
        self.parser.dry_run_status = False
        #
        self.logger.info('Dry run executed.')
        self.prepare_backup()

        self.logger.info('Backup completed')

        self.context.create_response(code=MessageCode.TASK_PROCESSED.value,
                                     message='Dosya transferi bitti.',
                                     content_type=self.get_content_type().APPLICATION_JSON.value)

    def prepare_backup(self):
        cmd = self.append_command_execution_type(self.prepare_command())
        self.logger.info(cmd)
        self.execute_command(cmd)

    def start_backup(self):
        try:
            t = threading.Thread(target=self.prepare_backup, args=())
            t.start()
        except Exception as e:
            Scope.get_instance().get_logger().info(e)
