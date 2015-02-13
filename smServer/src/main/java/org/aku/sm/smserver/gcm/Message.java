package org.aku.sm.smserver.gcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Message to send a message by GCM to notify an Android client.
 *
 * Source https://developer.android.com/google/gcm/server.html
 */
public class Message {

	/**
	 * registration_ids - This parameter specifies a string array containing the list of devices (registration IDs) receiving the message.
	 * It must contain at least 1 and at most 1000 registration IDs.
	 */
	private List<String> registrationIds;
	
	/**
	 * notification_key - This parameter specifies a string that maps a single user to multiple registration IDs associated with that user. 
	 * This allows a 3rd-party server to send a single message to multiple app instances (typically on multiple devices) owned by a single user. 
	 */
	private String notificationKey;
	
	/** 
	 * collapse_key - This parameter specifies an arbitrary string (such as "Updates Available") that is used to collapse a group of 
	 * like messages when the device is offline, so that only the last message gets sent to the client.
	 */
	private String collapseKey;
	
	/**
	 * data - This parameter specifies a JSON object whose fields represents the key-value pairs of the message's payload data. 
	 * If present, the payload data will be included in the Intent as application data, with the key being the extra's name. 
	 * For instance, "data":{"score":"3x1"} would result in an intent extra named score whose value is the string 3x1.
	 */
	private Map<String, String> data;
	
	/** 
	 * delay_while_idle - This parameter indicates that the message should not be sent immediately if the device is idle. 
	 * The server will wait for the device to become active, and then only the last message for each collapse_key value will be sent. 
	 * The default value is false, and must be a JSON boolean. Optional.
	 */
	private Boolean delayWhileIdle;
	
	/** 
	 * time_to_live - This parameter specifies how long (in seconds) the message should be kept on GCM storage if the device is offline. 
	 * Optional (default time-to-live is 4 weeks, and must be set as a JSON number).
	 */
	private Long timeToLive;
	
	/**
	 * restricted_package_name - This parameter specifies a string containing the package name of your application. W
	 * hen set, messages are only sent to registration IDs that match the package name. Optional.	
	 */
	private String restrictedPackageName;
	
	/**
	 * dry_run - This parameter allows developers to test a request without actually sending a message. 
	 * Optional. The default value is false, and must be a JSON boolean.
	 */
	private Boolean dryRun;

	
	
	public Message(List<String> registrationIds, String notificationKey,
			String collapseKey, Map<String, String> data,
			Boolean delayWhileIdle, Long timeToLive,
			String restrictedPackageName, Boolean dryRun) {
		super();
		this.registrationIds = registrationIds;
		this.notificationKey = notificationKey;
		this.collapseKey = collapseKey;
		this.data = data;
		this.delayWhileIdle = delayWhileIdle;
		this.timeToLive = timeToLive;
		this.restrictedPackageName = restrictedPackageName;
		this.dryRun = dryRun;
	}



	public Message() {
		this.registrationIds = new ArrayList<String>();
		this.data = new HashMap<String, String>();
	}


	public List<String> getRegistrationIds() {
		return registrationIds;
	}


	public void setRegistrationIds(List<String> registrationIds) {
		this.registrationIds = registrationIds;
	}


	public String getNotificationKey() {
		return notificationKey;
	}


	public void setNotificationKey(String notificationKey) {
		this.notificationKey = notificationKey;
	}


	public String getCollapseKey() {
		return collapseKey;
	}


	public void setCollapseKey(String collapseKey) {
		this.collapseKey = collapseKey;
	}


	public Map<String, String> getData() {
		return data;
	}


	public void setData(Map<String, String> data) {
		this.data = data;
	}


	public Boolean getDelayWhileIdle() {
		return delayWhileIdle;
	}


	public void setDelayWhileIdle(Boolean delayWhileIdle) {
		this.delayWhileIdle = delayWhileIdle;
	}


	public Long getTimeToLive() {
		return timeToLive;
	}


	public void setTimeToLive(Long timeToLive) {
		this.timeToLive = timeToLive;
	}


	public String getRestrictedPackageName() {
		return restrictedPackageName;
	}


	public void setRestrictedPackageName(String restrictedPackageName) {
		this.restrictedPackageName = restrictedPackageName;
	}


	public Boolean getDryRun() {
		return dryRun;
	}


	public void setDryRun(Boolean dryRun) {
		this.dryRun = dryRun;
	}


	public void addRegistration(String string) {
		registrationIds.add(string);
	}

	public void addData(String key, String value) {
		data.put(key, value);
	}
	
}
