package org.aku.sm.smserver.controller;

import java.security.Principal;
import java.util.Collection;

import org.aku.sm.smserver.common.Util;
import org.aku.sm.smserver.repository.DoctorRepository;
import org.aku.sm.smserver.repository.Patient;
import org.aku.sm.smserver.repository.PatientRepository;
import org.aku.sm.smserver.repository.SymptomCheckinRepository;
import org.aku.sm.smserver.repository.User;
import org.aku.sm.smserver.repository.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class UserService {
	Logger LOG = Logger.getLogger(UserService.class);

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	DoctorRepository doctorRepository;
	
	@Autowired
	PatientRepository patientRepository;
	
	@Autowired
	SymptomCheckinRepository symptomCheckinRepository;
	
		
	@RequestMapping(value="/login/{username}", method=RequestMethod.GET)
	public @ResponseBody User getUserByUsername(@PathVariable String username) {
		return userRepository.findByUsername(username);
	}	

	@RequestMapping(value="/login/{username}/{gcmRegistrationId}", method=RequestMethod.GET)
	public @ResponseBody User getUserByUsername(@PathVariable String username, @PathVariable String gcmRegistrationId) {
		User user = userRepository.findByUsername(username);
		// System.out.println("getUserByUsername " + username + " " + user.getId() + " " + user.getGcmRegistrationId());
		// LOG.info("getUserByUsername {} {} {}", username ,user.getId(), user.getGcmRegistrationId());
		user.setGcmRegistrationId(gcmRegistrationId);
		userRepository.save(user);
		return user;
	}	

	/**
	 * Returns all patients where lastName like 'lastName%' pattern
	 * 
	 * @param doctorId    id of a doctor
	 * @param lastName    Name string, to query … where x.firstname like ?1
	 * 
	 * @return list of patients
	 */
	@RequestMapping(value="/doctor/{doctorId}/patient/name/{lastName}", method=RequestMethod.GET)
	public @ResponseBody Collection<Patient> getAllPatients(@PathVariable long doctorId,
			@PathVariable String lastName){
		Collection<Patient> patients = Util.makeCollection(patientRepository.findByDoctorIdAndLastNameLikeOrderByLastNameAsc(doctorId, lastName + "%"));
		return withLatestMouthPain(patients);
	}	
	
	
	/**
	 * Returns all patients assigned to a doctor
	 * @param doctorId id of a doctor
	 * @return list of patients
	 */
	//  @PostFilter("hasPermission(filterObject, 'read') or hasPermission(filterObject, 'admin')")
	@RequestMapping(value="/doctor/{doctorId}/patient", method=RequestMethod.GET)
	public @ResponseBody Collection<Patient> findPatientByDoctorId(@PathVariable long doctorId){
		Collection<Patient> patients = Util.makeCollection(patientRepository.findByDoctorIdOrderByCheckinDateDesc(doctorId));
		return withLatestMouthPain(patients);
		
	}

	
	/**
	 * Returns the the patient matching the id of a patient (is unique)
	 * 
	 * @param id		id of patient
	 * 
	 * @return
	 */
	@PostAuthorize("returnObject.doctorId == principal.id")
	@RequestMapping(value="/patient/{id}", method=RequestMethod.GET)
	public @ResponseBody Patient getPatient(@PathVariable long id, Principal principal){
		Patient patient = patientRepository.findOne(id);
		return withLatestMouthPain(patient);
	}	
	
	
	/**
	 * Updates the attribute mouthPain of each patient
	 * @return the patient list argument
	 */
	private Collection<Patient> withLatestMouthPain(Collection<Patient> patients) {
		for (Patient patient : patients) {
			String mouthPain = symptomCheckinRepository.findLastMouthPain(patient.getId());
			withLatestMouthPain(patient);
			patient.setMouthPain(mouthPain);
		}
		return patients;
	}
	
	/**
	 * Updates the attribute mouthPain of the patient
	 * @return the patient argument
	 */
	private Patient withLatestMouthPain(Patient patient) {
			String mouthPain = symptomCheckinRepository.findLastMouthPain(patient.getId());
			patient.setMouthPain(mouthPain);
			return patient;
	}
	
	
}