package tr.org.liderahenk.usb.ltsp.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.ldap.model.LdapEntry;
import tr.org.liderahenk.liderconsole.core.ldap.utils.LdapUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.usb.ltsp.constants.UsbLtspConstants;
import tr.org.liderahenk.usb.ltsp.enums.StatusCode;
import tr.org.liderahenk.usb.ltsp.i18n.Messages;
import tr.org.liderahenk.usb.ltsp.model.CrontabExpression;

public class UsbFuseGroupDialog extends DefaultTaskDialog {

	private TableViewer tableViewer;
	private TableFilter tableFilter;
	private Text txtSearch;
	private Button btnAddFuseGroup;
	private Button btnRemoveFuseGroup;
	private Button btnEnableRemoveFuseGroupDate;
	private Button btnRemoveFuseGroupDate;
	private Text txtRemoveFuseGroupDate;

	public UsbFuseGroupDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet, false, true);
	}

	@Override
	public String createTitle() {
		return Messages.getString("USB_FUSE_GROUP");
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parent.setLayout(new GridLayout(1, false));
		createTableArea(parent);
		createInputArea(parent);
		return null;
	}

	private void createTableArea(Composite parent) {

		Label lblTable = new Label(parent, SWT.NONE);
		lblTable.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblTable.setText(Messages.getString("SELECT_USER"));

		createTableFilterArea(parent);

		GridData dataSearchGrid = new GridData();
		dataSearchGrid.grabExcessHorizontalSpace = true;
		dataSearchGrid.horizontalAlignment = GridData.FILL;

		tableViewer = new TableViewer(parent,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);

		// Create table columns
		createTableColumns();

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

		tableFilter = new TableFilter();
		tableViewer.addFilter(tableFilter);
		tableViewer.refresh();
	}

	private void createTableFilterArea(Composite parent) {
		Composite filterContainer = new Composite(parent, SWT.NONE);
		filterContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		filterContainer.setLayout(new GridLayout(2, false));

		// Search label
		Label lblSearch = new Label(filterContainer, SWT.NONE);
		lblSearch.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblSearch.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblSearch.setText(Messages.getString("SEARCH_FILTER"));

		// Filter table rows
		txtSearch = new Text(filterContainer, SWT.BORDER | SWT.SEARCH);
		txtSearch.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		txtSearch.setToolTipText(Messages.getString("SEARCH_USERNAME_TOOLTIP"));
		txtSearch.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				tableFilter.setSearchText(txtSearch.getText());
				tableViewer.refresh();
			}
		});
	}

	private void createTableColumns() {

		TableViewerColumn dnColumn = createTableViewerColumn(Messages.getString("USER_DN"), 300);
		dnColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LdapEntry) {
					return ((LdapEntry) element).getDistinguishedName();
				}
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn uidColumn = createTableViewerColumn(Messages.getString("USER_UID"), 100);
		uidColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LdapEntry) {
					return ((LdapEntry) element).getAttributes().get("uid") != null
							? ((LdapEntry) element).getAttributes().get("uid") : "-";
				}
				return Messages.getString("UNTITLED");
			}
		});
	}

	private void populateTable() {
		List<LdapEntry> users = LdapUtils.getInstance().findUsers(null, new String[] { "uid", "cn" });
		tableViewer.setInput(users);
		tableViewer.refresh();
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(false);
		column.setAlignment(SWT.LEFT);
		return viewerColumn;
	}

	private void createInputArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		composite.setLayout(new GridLayout(1, false));

		Composite rbComposite = new Composite(composite, SWT.NONE);
		rbComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		rbComposite.setLayout(new GridLayout(2, false));

		btnAddFuseGroup = new Button(rbComposite, SWT.RADIO);
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

		btnRemoveFuseGroup = new Button(rbComposite, SWT.RADIO);
		btnRemoveFuseGroup.setText(Messages.getString("REMOVE_FUSE_GROUP"));
		btnRemoveFuseGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		new Label(composite, SWT.NONE);

		Composite innerComposite = new Composite(composite, SWT.NONE);
		innerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		innerComposite.setLayout(new GridLayout(3, false));

		btnEnableRemoveFuseGroupDate = new Button(innerComposite, SWT.CHECK);
		btnEnableRemoveFuseGroupDate.setText(Messages.getString("ENABLE_REMOVE_GROUP_DATE"));
		btnEnableRemoveFuseGroupDate.setSelection(false);
		btnEnableRemoveFuseGroupDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnEnableRemoveFuseGroupDate.addSelectionListener(new SelectionListener() {
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
		btnRemoveFuseGroupDate.addSelectionListener(new SelectionListener() {
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

		toggleEndDateInputs();
	}

	public class TableFilter extends ViewerFilter {

		private String searchString;

		public void setSearchText(String s) {
			this.searchString = ".*" + s + ".*";
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (searchString == null || searchString.length() == 0) {
				return true;
			}
			LdapEntry item = (LdapEntry) element;
			return item.getDistinguishedName().matches(searchString)
					|| (item.getAttributes().get("uid") != null
							&& item.getAttributes().get("uid").matches(searchString))
					|| (item.getAttributes().get("cn") != null && item.getAttributes().get("cn").matches(searchString));
		}
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
		String[] selectedUsers = getSelectedUsers();
		if (selectedUsers == null || selectedUsers.length == 0) {
			throw new ValidationException(Messages.getString("SELECTED_USER_NOT_FOUND"));
		}
	}

	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("usernames", getSelectedUsers());
		parameterMap.put("statusCode",
				btnAddFuseGroup.getSelection() ? StatusCode.PRIVILEGED.getId() : StatusCode.UNPRIVILEGED.getId());
		parameterMap.put("endDate", getEndDate());
		return parameterMap;
	}

	private String[] getSelectedUsers() {
		TableItem[] items = tableViewer.getTable().getItems();
		List<String> selectedUsers = new ArrayList<String>();
		for (TableItem item : items) {
			if (item.getChecked()) {
				selectedUsers.add(item.getText(1).toString());
			}
		}
		return selectedUsers.toArray(new String[] {});
	}

	private String getEndDate() {
		return this.btnEnableRemoveFuseGroupDate.getSelection() && txtRemoveFuseGroupDate.getText() != null
				&& !txtRemoveFuseGroupDate.getText().isEmpty() ? txtRemoveFuseGroupDate.getText() : null;
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

	@Override
	public String getMailSubject() {
		return "USB Yetki Düzenleme";
	}

	@Override
	public String getMailContent() {
		return "cn={ahenk} istemcisinde aşağıdaki kullanıcılar için USB yetkileri düzenlenmiştir: \n {usernames} ";
	}

}
