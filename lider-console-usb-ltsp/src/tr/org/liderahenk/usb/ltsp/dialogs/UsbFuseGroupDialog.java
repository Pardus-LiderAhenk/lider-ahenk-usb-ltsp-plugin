package tr.org.liderahenk.usb.ltsp.dialogs;

import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.usb.ltsp.constants.UsbLtspConstants;
import tr.org.liderahenk.usb.ltsp.i18n.Messages;

public class UsbFuseGroupDialog extends DefaultTaskDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(UsbFuseGroupDialog.class);
	
	public UsbFuseGroupDialog(Shell parentShell, Set<String> dnSet)
	{
		super(parentShell, dnSet);
		logger.debug("USB fuse group");
	}

	@Override
	public String createTitle() {
		return Messages.getString("USB_FUSE_GROUP");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
	}

	@Override
	public Map<String, Object> getParameterMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommandId() {
		return "USB_FUSE_GROUP";
	}

	@Override
	public String getPluginName() {
		return UsbLtspConstants.PLUGIN_NAME;
	}

	@Override
	public String getPluginVersion() {
		return UsbLtspConstants.PLUGIN_VERSION;
	}

}
