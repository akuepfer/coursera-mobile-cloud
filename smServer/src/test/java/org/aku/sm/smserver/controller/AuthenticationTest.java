package org.aku.sm.smserver.controller;

import org.aku.sm.smserver.ResponseErrorHanderFactory;
import org.aku.sm.smserver.SslTestRestTemplate;
import org.aku.sm.smserver.repository.Doctor;
import org.aku.sm.smserver.repository.Patient;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;

/**
 * Pre condition: smserver Server must be running on 192.168.1.42:8433
 */
public class AuthenticationTest {

	String url = "https://aba.home:8443";
	String username = "aflower";
	String password = "p0";
	
	/**
	 * Test access for an authenticated doctor
	 * - doctor may access patients assigned to him
	 * - doctor may not access patients assigned to an other doctor
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRequest() throws Exception {
		TestRestTemplate template = new SslTestRestTemplate(username, password);
		template.setErrorHandler(ResponseErrorHanderFactory.createResponseErrorHandler());

		Doctor doctor = template.getForEntity(url +  "/login/aflower", Doctor.class).getBody();
		Assert.assertEquals("ROLE_DOCTOR", doctor.getRole());
		
		// assigned to doctor aflower, access ok
		Patient patient1 = template.getForEntity(url + "/patient/4", Patient.class).getBody();
		Assert.assertEquals("Access", "Black", patient1.getLastName());			
		
		// assigend to doctor cleaf, no access for doctor aflower
		try {
			@SuppressWarnings("unused")
			Patient patient2 = template.getForEntity(url + "/patient/14", Patient.class).getBody();		
			Assert.fail();
		} catch (Exception exception) {
			System.out.println(exception.getMessage());		
			Assert.assertTrue(exception.getMessage().contains("FORBIDDEN"));
		}

	}
	
}
