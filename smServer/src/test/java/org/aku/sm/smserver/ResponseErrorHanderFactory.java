package org.aku.sm.smserver;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * Error handler used by SslTestRestFactory
 */
public class ResponseErrorHanderFactory {

	public static class StatusException extends IOException {
		private static final long serialVersionUID = 4751559766127009148L;
		public StatusException() { super(); }
	    public StatusException(String message) { super(message); }
	    public StatusException(String message, Throwable cause) { super(message, cause); }
	    public StatusException(Throwable cause) { super(cause); }
	}
	
	
	public static ResponseErrorHandler createResponseErrorHandler() {
		return new ResponseErrorHandler() {				
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				if (response.getStatusCode() == HttpStatus.OK) {
					return false;
				} else {
					System.out.println(">> " + response.getRawStatusCode());
					System.out.println(">> " + response.getStatusText());
					System.out.println(">> " + response.getStatusCode());
					return true;
				}
			}
			
			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
				throw new StatusException(response.getStatusCode().name() + " " + response.getStatusText());
			}
		};
	}	
	
}
