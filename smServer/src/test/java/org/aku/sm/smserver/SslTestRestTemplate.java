package org.aku.sm.smserver;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.security.crypto.codec.Base64;

public class SslTestRestTemplate extends TestRestTemplate {

	
	public SslTestRestTemplate(String username, String password) 
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		super(username, password, 
				TestRestTemplate.HtppClientOption.ENABLE_COOKIES,
				TestRestTemplate.HtppClientOption.ENABLE_REDIRECTS);

		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
				new SSLContextBuilder().loadTrustMaterial(null,
						new TrustSelfSignedStrategy()).build());

		HttpClient sslHttpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();	
			
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(sslHttpClient);		
		
		List<ClientHttpRequestInterceptor> interceptors = 
				Collections.<ClientHttpRequestInterceptor> singletonList(
						new BasicAuthorizationInterceptor(username, password));
		
		setRequestFactory(new InterceptingClientHttpRequestFactory(requestFactory, interceptors));
		setErrorHandler(ResponseErrorHanderFactory.createResponseErrorHandler());
	}	
	
	
	/**
	 * From Sprint TestRestTemplate
	 */
	private static class BasicAuthorizationInterceptor implements
			ClientHttpRequestInterceptor {

		private final String username;
		private final String password;

		public BasicAuthorizationInterceptor(String username, String password) {
			this.username = username;
			this.password = (password == null ? "" : password);
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body,
				ClientHttpRequestExecution execution) throws IOException {
			byte[] token = Base64.encode((this.username + ":" + this.password)
					.getBytes());
			request.getHeaders().add("Authorization",
					"Basic " + new String(token));
			return execution.execute(request, body);
		}
	}	
}
