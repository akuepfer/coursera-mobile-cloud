package org.aku.sm.smserver.gcm;

import java.util.List;

/**
 * The respons for a GCM message.
 *
 * @see https://developer.android.com/google/gcm/http.html#response
 */
public class Response {

	
	// "{"multicast_id":6551877618119157542,"success":1,"failure":0,"canonical_ids":0,"results":[{"message_id":"0:1415749808943705%eae8ffe7f9fd7ecd"}]}[\r][\n]"
	
	/**
	 * multicast_id	Unique ID (number) identifying the multicast message.
	 */
	private String multicastId;
	
	/** 
	 * success	Number of messages that were processed without an error.
	 */
	private long success;
	
	
	/**
	 * failure	Number of messages that could not be processed.
	 */
	private long failure;
	
	/**
	 * canonical_ids	Number of results that contain a canonical registration ID. See Advanced Topics for more discussion of this topic.
	 */
	private long canonicalIds;
	
	/**
	 * results	Array of objects representing the status of the messages processed. The objects are listed in the same order as the request (i.e., for each registration ID in the request, its result is listed in the same index in the response) and they can have these fields:
	 */
	private List<Result> results;

	
	public Response() {
		super();
	}

	public String getMulticastId() {
		return multicastId;
	}

	public void setMulticastId(String multicastId) {
		this.multicastId = multicastId;
	}

	public long getSuccess() {
		return success;
	}

	public void setSuccess(long success) {
		this.success = success;
	}

	public long getFailure() {
		return failure;
	}

	public void setFailure(long failure) {
		this.failure = failure;
	}

	public long getCanonicalIds() {
		return canonicalIds;
	}

	public void setCanonicalIds(long canonicalIds) {
		this.canonicalIds = canonicalIds;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}
	
	
	
}
