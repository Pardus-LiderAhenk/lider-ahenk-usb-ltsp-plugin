package tr.org.liderahenk.usb.ltsp.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.persistence.entities.ICommandExecutionResult;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.plugin.ITaskAwareCommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.usb.ltsp.entities.UsbFuseGroupResult;
import tr.org.liderahenk.usb.ltsp.enums.StatusCode;
import tr.org.liderahenk.usb.ltsp.model.AgentUsbFuseGroupResult;
import tr.org.liderahenk.usb.ltsp.plugininfo.PluginInfoImpl;

public class UsbFuseGroupCommand implements ICommand, ITaskAwareCommand {

	private static final Logger logger = LoggerFactory.getLogger(UsbFuseGroupCommand.class);

	private ICommandResultFactory resultFactory;
	private PluginInfoImpl pluginInfo;
	private IPluginDbService dbService;

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {
		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onTaskUpdate(ICommandExecutionResult result) {
		try {
			Map<String, Object> responseData = new ObjectMapper().readValue(result.getResponseData(), 0,
					result.getResponseData().length, new TypeReference<HashMap<String, Object>>() {
					});
			if (responseData.get("fuse-group-results") != null) {
				List<AgentUsbFuseGroupResult> res = (List<AgentUsbFuseGroupResult>) responseData
						.get("fuse-group-results");
				for (AgentUsbFuseGroupResult r : res) {
					UsbFuseGroupResult obj = new UsbFuseGroupResult(null, r.getUsername(),
							result.getCommandExecution().getUid(),
							StatusCode.getType(Integer.parseInt(r.getStatusCode())), new Date());
					dbService.save(obj);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this);
	}

	@Override
	public String getPluginName() {
		return pluginInfo.getPluginName();
	}

	@Override
	public String getPluginVersion() {
		return pluginInfo.getPluginVersion();
	}

	@Override
	public String getCommandId() {
		return "USB_FUSE_GROUP";
	}

	@Override
	public Boolean executeOnAgent() {
		return true;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void setPluginInfo(PluginInfoImpl pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public void setDbService(IPluginDbService dbService) {
		this.dbService = dbService;
	}

}
