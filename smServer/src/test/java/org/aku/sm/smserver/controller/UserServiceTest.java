package org.aku.sm.smserver.controller;

import java.util.Collection;

import org.aku.sm.smserver.ApplicationContextTest;
import org.aku.sm.smserver.auth.SecurityConfiguration;
import org.aku.sm.smserver.repository.Patient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Test the search service, search for patient last name.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationContextTest.class)
//class-level annotation that is used to declare that the ApplicationContext loaded for an integration test should be a WebApplicationContext
@WebAppConfiguration
public class UserServiceTest {

	static {
		SecurityConfiguration.disableForTest = true;
	}
	
	@Autowired
	UserService userService;
		
	@Test
	public void testRetrievePatient() {
		
		Collection<Patient> patients = userService.getAllPatients(1, "Bl%");
		Assert.assertEquals(2, patients.size());
	}
	
}
