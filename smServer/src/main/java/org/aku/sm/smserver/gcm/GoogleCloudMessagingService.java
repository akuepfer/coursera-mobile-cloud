package org.aku.sm.smserver.gcm;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.aku.sm.smserver.repository.Doctor;
import org.aku.sm.smserver.repository.DoctorRepository;
import org.aku.sm.smserver.repository.Patient;
import org.aku.sm.smserver.repository.UserRepository;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;


/**
 * Controller to call Google Cloud Messaging (GCM) using Spring RestTemplate
 * Test using curl
 * curl --header "Authorization: key=AIzaSyASI8cDoxBJe-eo59pxb-GX5u-bR3gcOfc" --header Content-Type:"application/json" https://android.googleapis.com/gcm/send  -d "{\"registration_ids\":[\"APA91bGMKDXOH8lonZiwOJCtoDc7VK9D8Cg_lnQeR_CSCreVKWdt8UC2fux7eY7HHkxRLnztTDCJZ_2-QDMAXr5INAMA4TW8LUBK6k7R3ySVK3cLFZ5zCVIs3V5rEca_op1oxrWvx8Xo6ENZqud0RvTVEc_fdMzazQ\"]}"
 * 
 * http://192.168.43.159
 */
@Controller
@RequestMapping("/")
public class GoogleCloudMessagingService {
	  protected static final Logger logger = Logger.getLogger(GoogleCloudMessagingService.class.getName());
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	DoctorRepository doctorRepository;
	
	// The HTTP header must contain the following headers:
	// Authorization: key=YOUR_API_KEY
	String AUTHORIZATION = "Authorization";
	String AUTHORIZATION_KEY = "key=" + "AIzaSyASI8cDoxBJe-eo59pxb-GX5u-bR3gcOfc";

	/**
	 * Testing only, send a GCS notification
	 *
	 * @param userId     user to be notified
	 * @param patientId  patient who' checkin list should be displaced.
	 */
	@PreAuthorize("hasAnyRole({ 'ROLE_PATIENT', 'ROLE_DOCTOR', 'PATIENT', 'DOCTOR' })")
	@RequestMapping(value="/notify/{userId}/patient/{patientId}", method=RequestMethod.POST)
	public @ResponseBody String notify(@PathVariable long userId, @PathVariable long patientId){
		Patient patient = new TestPatient(patientId, userId);
		patient.setFirstName("Heb");
		patient.setLastName("Green");
		patient.setMedicalRecordNumber("11-22-33");
		notifyDoctor(patient);
		return "OK";
	}	
	
	
	/**
	 * A doctor is alerted if a patient experiences 12 of “severe pain,” 16 or more hours of “moderate” or “severe pain” or 12 hours of “I can’t eat.”
	 * 
	 * Send a notification to the doctor of the patient
	 * with data patientId=<patientId>.
	 * Notification should lead the doctor to the patients checking list.
	 * 
	 * @param patient patient who's doctor has to be notfied
	 */
	@PreAuthorize("hasAnyRole({ 'ROLE_PATIENT', 'PATIENT' })")
	public void notifyDoctor(Patient patient){
		
		Doctor doctor = doctorRepository.findOne(patient.getDoctorId());
		if (doctor == null) {
			throw new ResourceNotFoundException("No user with id " + patient.getDoctorId() + "found");
		}
				
		
		Message message = new Message();
		message.addRegistration(doctor.getGcmRegistrationId());
		message.addData("patientId", Long.toString(patient.getId()));
		message.addData("medicalRecordNumber", patient.getMedicalRecordNumber());
		message.addData("firstName", patient.getFirstName());
		message.addData("lastName", patient.getLastName());
		
		try {
			Response response = sendNotification(message);
			long ret = response.getSuccess();
			logger.log(Level.FINE, "notifyDoctor status %d", ret);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "notifyDoctor", e);
			throw new ResourceNotFoundException(e.getMessage());
		}
	}	
	
    
    
    /**
     * Send the message to GCM using a trust any certificate trust manager.
	 *
     * @param message the message to send, specifed by https://developer.android.com/google/gcm/server.html
     * @return GCM response see https://developer.android.com/google/gcm/http.html#response
	 *
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     * @throws KeyManagementException
     */
	public Response sendNotification(Message message) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, 
		IOException, KeyManagementException {

	  // Content-Type: application/json for JSON; application/x-www-form-urlencoded;charset=UTF-8 for plain text.
      HttpHeaders requestHeaders = new HttpHeaders();
      requestHeaders.setContentType(new MediaType("application","json"));
      requestHeaders.add(AUTHORIZATION, AUTHORIZATION_KEY);
      HttpEntity<Message> requestEntity = new HttpEntity<Message>(message, requestHeaders);			
        
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { 
        		new X509TrustManager() {
        			@Override
        			public void checkClientTrusted( final X509Certificate[] chain, final String authType ) { }
        			@Override
        			public void checkServerTrusted( final X509Certificate[] chain, final String authType ) { }
        			@Override
        			public X509Certificate[] getAcceptedIssuers() { return null; }
        		}
        };
        
        // Install the all-trusting trust manager
        final SSLContext sslcontext = SSLContext.getInstance( "TLS" );
        sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());       
        
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext, new String[] { "TLSv1.2" }, null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build(); 
        
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpclient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);     

        // Replace the default mapper by the Jackson and String message converters
        // replace camel-case by lower case with underscore, skip non null values
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);
        List<HttpMessageConverter<?>> list = new ArrayList<HttpMessageConverter<?>>();       
        list.add(converter);
        list.add(new StringHttpMessageConverter());
        restTemplate.setMessageConverters(list);

        // Make the HTTP POST request, marshaling the request to JSON, and the response to a String
        String url = "https://android.googleapis.com/gcm/send";
        ResponseEntity<Response> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Response.class);      
		return responseEntity.getBody();
	}
	


	
	/**
	 * Common for all services -translate a ResourceNotFoundException to HTTP not found status.
	 */
    @ExceptionHandler(value={ResourceNotFoundException.class, IOException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody ResponseEntity<String> handleIOException(ResourceNotFoundException ex) {
    	return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }		
    
    
    private class TestPatient extends Patient {
		private static final long serialVersionUID = -6379794395209212177L;

		public TestPatient(long id, long doctorId) {
    		this.id = id;
    		this.doctorId = doctorId;
    	}
    }
		
}
