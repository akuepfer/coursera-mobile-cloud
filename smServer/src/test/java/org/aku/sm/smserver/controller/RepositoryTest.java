package org.aku.sm.smserver.controller;

import org.aku.sm.smserver.ApplicationContextTest;
import org.aku.sm.smserver.auth.SecurityConfiguration;
import org.aku.sm.smserver.repository.Doctor;
import org.aku.sm.smserver.repository.DoctorRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Simple test to verify primary key is assigned
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationContextTest.class)
@WebAppConfiguration
public class RepositoryTest {

	@Autowired
	DoctorRepository doctorRepository;
	
	
	static {
		SecurityConfiguration.disableForTest = true;
	}
	
	@Test
	public void testTherapy() {
		Doctor doctor = doctorRepository.save(new Doctor("firstName", "lastName", "degree"));
		Assert.assertNotEquals(doctor.getId(), 0);
	}
	
}
