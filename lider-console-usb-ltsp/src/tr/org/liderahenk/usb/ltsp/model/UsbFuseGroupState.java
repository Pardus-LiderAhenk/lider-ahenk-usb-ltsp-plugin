package tr.org.liderahenk.usb.ltsp.model;

import java.io.Serializable;

public class UsbFuseGroupState implements Serializable {

	private static final long serialVersionUID = 4882144394899651394L;

	private String uid;
	private String username;
	private String state;

	public UsbFuseGroupState(String uid, String username, String state) {
		super();
		this.uid = uid;
		this.username = username;
		this.state = state;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
