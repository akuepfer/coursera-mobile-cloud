package org.aku.sm.smserver.repository;

import javax.persistence.Transient;

public class ServiceStatus {

	@Transient
	protected Long timestamp;
	@Transient
	protected String status;
	@Transient
	protected String error;
	@Transient
	protected String exception;
	@Transient
	protected String message;
	@Transient
	protected String path;

	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}
