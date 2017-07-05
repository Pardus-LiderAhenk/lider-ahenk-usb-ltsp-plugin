package tr.org.liderahenk.usb.ltsp.model;

import java.io.Serializable;
import java.util.Date;

import tr.org.liderahenk.usb.ltsp.enums.StatusCode;

public class UsbFuseGroupResult implements Serializable {

	private static final long serialVersionUID = 749394306065638349L;

	private Long id;

	private String username;

	private String uid;

	private Integer statusCode;

	private Date createDate;

	public UsbFuseGroupResult() {
	}

	public UsbFuseGroupResult(Long id, String username, String uid, Integer statusCode, Date createDate) {
		super();
		this.id = id;
		this.username = username;
		this.uid = uid;
		this.statusCode = statusCode;
		this.createDate = createDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public StatusCode getStatusCode() {
		return StatusCode.getType(statusCode);
	}
	
	public void setStatusCode(StatusCode statusCode) {
		if (statusCode == null) {
			this.statusCode = null;
		} else {
			this.statusCode = statusCode.getId();
		}
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
