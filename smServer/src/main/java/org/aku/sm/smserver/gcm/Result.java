package org.aku.sm.smserver.gcm;

/**
 * Reesult of a results list of a GCM response.
 *
 * https://developer.android.com/google/gcm/http.html#response
 */
public class Result {

	/**
	 * message_id: String representing the message when it was successfully processed.
	 */
	String messageId;
	
	/**
	 * registration_id: If set, means that GCM processed the message but it has another canonical registration ID for that device, 
	 * so sender should replace the IDs on future requests (otherwise they might be rejected). 
	 * This field is never set if there is an error in the request.
	 */
	String registrationId;
	
	
	/**
	 * error: String describing an error that occurred while processing the message for that recipient. 
	 * The possible values are the same as documented in the above table, plus "Unavailable" (meaning GCM servers were busy
	 * and could not process the message for that particular recipient, so it could be retried).	
	 */
	String error;


	
	public Result() {
		super();
	}


	public String getMessageId() {
		return messageId;
	}


	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}


	public String getRegistrationId() {
		return registrationId;
	}


	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}


	public String getError() {
		return error;
	}


	public void setError(String error) {
		this.error = error;
	}
	
	
	
	
	
}
