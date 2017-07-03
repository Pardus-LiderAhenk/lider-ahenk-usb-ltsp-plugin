package tr.org.liderahenk.usb.ltsp.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	
	private static final String BUNDLE_NAME = "tr.org.liderahenk.backup.i18n.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	public Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	/**
	 * Returns a formatted string using the specified message string and
	 * arguments.<br/>
	 * <br/>
	 * 
	 * <b>Example:</b><br/>
	 * messages_tr.properties:<br/>
	 * ROSTER_ONLINE=%s çevrimiçi oldu<br/>
	 * 
	 * usage:<br/>
	 * Messages.getString("ROSTER_ONLINE", dn)
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	public static String getString(String key, Object... args) {
		return String.format(getString(key), args);
	}
	
}
