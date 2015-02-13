package org.aku.sm.smserver.controller;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.aku.sm.smserver.SslTestRestTemplate;
import org.aku.sm.smserver.repository.SymptomCheckin;
import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;

/**
 * Integration test
 */
public class MedicationServiceTest {


		String url = "https://aba.home:8443";
		String path = "/doctor/1/patient/4/checkin";
		String username = "aflower";
		String password = "p0";
		

		@Test
		public void testCheckin() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
			TestRestTemplate template = new SslTestRestTemplate(username, password);
			ResponseEntity<SymptomCheckin[]> response = template.getForEntity(url + path, SymptomCheckin[].class);
			@SuppressWarnings({ "unused" })
			SymptomCheckin[] checkins = response.getBody();
		}	
	
		
}
