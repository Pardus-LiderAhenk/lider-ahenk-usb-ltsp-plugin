#!/usr/bin/python3
# -*- coding: utf-8 -*-

import json

from base.plugin.abstract_plugin import AbstractPlugin


class Fusegroup(AbstractPlugin):
    def __init__(self, task, context):
        super(AbstractPlugin, self).__init__()
        self.task = task
        self.context = context
        self.logger = self.get_logger()
        self.message_code = self.get_message_code()

    def does_user_exist(self, username):
        result_code, p_out, p_err = self.execute('grep -c \'{0}\' /etc/passwd'.format(username))
        if p_err == 0 and p_out == 1:
            return True
        else:
            return False

    def does_group_exist(self, groupname):
        result_code, p_out, p_err = self.execute('getent group \'{0}\' /etc/passwd'.format(groupname))
        if p_err == 0 and p_out == 1:
            return True
        else:
            return False

    def handle_task(self):

        try:
            data_list = []
            users = self.task['usernames'].replace(']', '').replace('[', '').split(',')
            state = self.task['statusCode']

            self.logger.debug('Configuration of Usb privileges started.')
            for user in users:
                self.logger.debug('User {0} is handling'.format(user))
                if not self.does_user_exist(user):
                    self.logger.debug('User {0} does not exist'.format(user))
                    data = dict()
                    data['username'] = user
                    data['statusCode'] = 3
                    data_list.append(data)
                    continue
                if not self.does_group_exist('fuse'):
                    self.logger.debug('Fuse group does not exist')
                    data = dict()
                    data['username'] = user
                    data['statusCode'] = 2
                    data_list.append(data)
                    continue

                data = dict()
                data['username'] = user
                if state == '1':
                    result_code, p_out, p_err = self.execute('adduser {0} fuse'.format(user))
                    if result_code == 0:
                        self.logger.debug('User {0} added to fuse group'.format(user))
                        data['statusCode'] = 1
                    else:
                        self.logger.error('A problem occurred while adding user {0} to fuse group'.format(user))
                        data['statusCode'] = 4
                else:
                    self.logger.debug('')
                    result_code, p_out, p_err = self.execute('deluser {0} fuse'.format(user))
                    if result_code == 0:
                        self.logger.debug('User {0} removed from fuse group'.format(user))
                        data['statusCode'] = 0
                    else:
                        self.logger.error('A problem occurred while removing user {0} from fuse group'.format(user))
                        data['statusCode'] = 4
                data_list.append(data)
                continue

            result = dict()
            result['fuse-group-results'] = data_list
            self.context.create_response(code=self.message_code.TASK_PROCESSED.value,
                                         message='USB hakları düzenlendi.',
                                         data=json.dumps(result),
                                         content_type=self.get_content_type().APPLICATION_JSON.value)
        except Exception as e:
            self.logger.error('A problem occurred while editing usb privilege. Erro Message: {0}'.format(str(e)))
            self.context.create_response(code=self.message_code.TASK_ERROR.value,
                                         message='USB hakları düzenlenirken hata oluştu',
                                         content_type=self.get_content_type().APPLICATION_JSON.value)


def handle_task(task, context):
    fg = Fusegroup(task, context)
    fg.handle_task()
