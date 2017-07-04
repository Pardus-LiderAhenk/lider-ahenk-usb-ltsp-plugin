package tr.org.liderahenk.usb.ltsp.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;
import tr.org.liderahenk.usb.ltsp.entities.UsbFuseGroupResult;
import tr.org.liderahenk.usb.ltsp.plugininfo.PluginInfoImpl;

/**
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class ListUsbFuseGroupResultCommand implements ICommand {

	private ICommandResultFactory resultFactory;
	private PluginInfoImpl pluginInfo;
	private IPluginDbService dbService;

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {
		Map<String, Object> parameterMap = context.getRequest().getParameterMap();
		String uid = (String) parameterMap.get("uid");

		Map<String, Object> propertiesMap = null;
		List<PropertyOrder> orders = new ArrayList<PropertyOrder>();
		orders.add(new PropertyOrder("createDate", OrderType.DESC));

		if (uid != null && !uid.isEmpty()) {
			orders.add(new PropertyOrder("uid", OrderType.DESC));
			propertiesMap = new HashMap<String, Object>();
			propertiesMap.put("uid", uid);
		}

		List<UsbFuseGroupResult> result = dbService.findByProperties(UsbFuseGroupResult.class, propertiesMap, orders,
				null);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("fuse-group-results", new ObjectMapper().writeValueAsString(result));

		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this, resultMap);
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
		return "LIST_USB_FUSE_GROUP_STATUS";
	}

	@Override
	public Boolean executeOnAgent() {
		return false;
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
