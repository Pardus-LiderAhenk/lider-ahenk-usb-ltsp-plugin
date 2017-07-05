package tr.org.liderahenk.usb.ltsp.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import tr.org.liderahenk.usb.ltsp.enums.StatusCode;

@Entity
@Table(name = "P_USB_FUSE_GROUP_RESULT"/*, uniqueConstraints = @UniqueConstraint(columnNames = { "USERNAME", "UID" })*/)
public class UsbFuseGroupResult implements Serializable {

	private static final long serialVersionUID = -4130227601711334080L;

	@Id
	@GeneratedValue
	@Column(name = "USB_FUSE_GROUP_RESULT_ID", unique = true, nullable = false)
	private Long id;

	@Column(name = "USERNAME", nullable = false)
	private String username;

	@Column(name = "UID", nullable = false)
	private String uid;

	@Column(name = "STATE_CODE", nullable = false)
	private Integer statusCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", nullable = false)
	private Date createDate;

	public UsbFuseGroupResult() {
	}

	public UsbFuseGroupResult(Long id, String username, String uid, StatusCode statusCode, Date createDate) {
		super();
		this.id = id;
		this.username = username;
		this.uid = uid;
		setStatusCode(statusCode);
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

//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
//		result = prime * result + ((username == null) ? 0 : username.hashCode());
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		UsbFuseGroupResult other = (UsbFuseGroupResult) obj;
//		if (uid == null) {
//			if (other.uid != null)
//				return false;
//		} else if (!uid.equals(other.uid))
//			return false;
//		if (username == null) {
//			if (other.username != null)
//				return false;
//		} else if (!username.equals(other.username))
//			return false;
//		return true;
//	}

}
