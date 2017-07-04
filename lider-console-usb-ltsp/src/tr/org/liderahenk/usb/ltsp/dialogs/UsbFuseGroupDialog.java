package tr.org.liderahenk.usb.ltsp.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.usb.ltsp.constants.UsbLtspConstants;
import tr.org.liderahenk.usb.ltsp.enums.StatusCode;
import tr.org.liderahenk.usb.ltsp.i18n.Messages;
import tr.org.liderahenk.usb.ltsp.model.CrontabExpression;

public class UsbFuseGroupDialog extends DefaultTaskDialog {

	private static final Logger logger = LoggerFactory.getLogger(UsbFuseGroupDialog.class);

	private TableViewer tableViewer;
//	private TableFilter tableFilter;
	private Button btnAddFuseGroup;
	private Button btnEnableRemoveFuseGroupDate;
	private Button btnRemoveFuseGroupDate; 
	private Text txtRemoveFuseGroupDate;

	public UsbFuseGroupDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
	}

	@Override
	public String createTitle() {
		return Messages.getString("USB_FUSE_GROUP");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(1, false));
		createInputArea(parent);
//		createTableArea(parent);
		return null;
	}
	
	private void createTableArea(Composite parent) {
		GridData dataSearchGrid = new GridData();
		dataSearchGrid.grabExcessHorizontalSpace = true;
		dataSearchGrid.horizontalAlignment = GridData.FILL;

		tableViewer = new TableViewer(parent,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// Create table columns
//		createTableColumns();

		// Configure table layout
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.getVerticalBar().setEnabled(true);
		table.getVerticalBar().setVisible(true);
		tableViewer.setContentProvider(new ArrayContentProvider());
		populateTable();

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.heightHint = 140;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);

//		tableFilter = new TableFilter();
//		tableViewer.addFilter(tableFilter);
//		tableViewer.refresh();
	}

	private void populateTable() {
		// TODO Auto-generated method stub
		
	}

	private void createInputArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		composite.setLayout(new GridLayout(1, false));

		btnAddFuseGroup = new Button(composite, SWT.CHECK);
		btnAddFuseGroup.setText(Messages.getString("ADD_FUSE_GROUP"));
		btnAddFuseGroup.setSelection(true);
		btnAddFuseGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnAddFuseGroup.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleEndDateInputs();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		Composite innerComposite = new Composite(composite, SWT.BORDER);
		innerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		innerComposite.setLayout(new GridLayout(3, false));
		
		btnEnableRemoveFuseGroupDate = new Button(innerComposite, SWT.CHECK);
		btnEnableRemoveFuseGroupDate.setText(Messages.getString("ENABLE_REMOVE_GROUP_DATE"));
		btnEnableRemoveFuseGroupDate.setSelection(false);
		btnEnableRemoveFuseGroupDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnAddFuseGroup.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleEndDateInputs();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		txtRemoveFuseGroupDate = new Text(innerComposite, SWT.BORDER);
		txtRemoveFuseGroupDate.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		btnRemoveFuseGroupDate = new Button(innerComposite, SWT.PUSH);
		btnRemoveFuseGroupDate.setText("Seç");
		btnRemoveFuseGroupDate.addSelectionListener(new SelectionListener(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				CrontabDialog crontabDialog = new CrontabDialog(Display.getDefault().getActiveShell(), true);
				crontabDialog.create();
				if (crontabDialog.open() != Window.OK) {
					return;
				} else {
					CrontabExpression expr = crontabDialog.getExpression();
					if (expr != null) {
						String crontabStr = expr.getCrontabStr();
						crontabStr = crontabStr.replaceFirst("0 ", "");
						txtRemoveFuseGroupDate.setText(crontabStr);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	protected void toggleEndDateInputs() {
		if (btnAddFuseGroup.getSelection()) {
			btnEnableRemoveFuseGroupDate.setEnabled(true);
			if (btnEnableRemoveFuseGroupDate.getSelection()) {
				txtRemoveFuseGroupDate.setEnabled(true);
				btnRemoveFuseGroupDate.setEnabled(true);
			} else {
				txtRemoveFuseGroupDate.setEnabled(false);
				btnRemoveFuseGroupDate.setEnabled(false);
			}
		} else {
			btnEnableRemoveFuseGroupDate.setEnabled(false);
			btnEnableRemoveFuseGroupDate.setSelection(false);
			txtRemoveFuseGroupDate.setEnabled(false);
			txtRemoveFuseGroupDate.setText("");
			btnRemoveFuseGroupDate.setEnabled(false);
		}
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("usernames", getSelectedUsers());
		parameterMap.put("statusCode", btnAddFuseGroup.getSelection() ? StatusCode.PRIVILEGED.getId() : StatusCode.UNPRIVILEGED.getId());
		parameterMap.put("endDate", getEndDate());
		return parameterMap;
	}

	private String[] getSelectedUsers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getEndDate() {
		return this.btnEnableRemoveFuseGroupDate.getSelection() && txtRemoveFuseGroupDate.getText() != null && !txtRemoveFuseGroupDate.getText().isEmpty() ? txtRemoveFuseGroupDate.getText() : null;
	}

	@Override
	public String getCommandId() {
		return UsbLtspConstants.TASKS.USB_FUSE_GROUP;
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
