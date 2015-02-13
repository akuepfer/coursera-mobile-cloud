package org.aku.sm.smserver.controller;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.aku.sm.smserver.common.Util;
import org.aku.sm.smserver.gcm.GoogleCloudMessagingService;
import org.aku.sm.smserver.repository.Medication;
import org.aku.sm.smserver.repository.MedicationIntakeRepository;
import org.aku.sm.smserver.repository.MedicationRepository;
import org.aku.sm.smserver.repository.Patient;
import org.aku.sm.smserver.repository.PatientRepository;
import org.aku.sm.smserver.repository.SymptomCheckin;
import org.aku.sm.smserver.repository.SymptomCheckinImage;
import org.aku.sm.smserver.repository.SymptomCheckinImageRepository;
import org.aku.sm.smserver.repository.SymptomCheckinRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/")
public class MedicationService {
	 private Log log = LogFactory.getLog(MedicationService.class);
	
	@Autowired
	PatientRepository patientRepository;
	
	@Autowired
	MedicationRepository medicationRepository;
	
	@Autowired
	SymptomCheckinRepository symptomCheckinRepository;
	
	@Autowired
	SymptomCheckinImageRepository symptomCheckinImageRepository;

	@Autowired
	MedicationIntakeRepository medicationRepositorIntakeRepositoryy;
	
	@Autowired
	GoogleCloudMessagingService googleCloudMessagingService;
	
	//
	// Medication
	//
	@RequestMapping(value="/patient/{patientId}/medication", method=RequestMethod.GET)
	public @ResponseBody Collection<Medication> findMedicationByPatientId(@PathVariable long patientId) {
		return Util.makeCollection(medicationRepository.findByPatientId(patientId));
	}

	
	@RequestMapping(value="/patient/{patientId}/medication", method=RequestMethod.POST)
	public @ResponseBody Medication createMedication(@PathVariable long patientId, 
			@RequestBody Medication medication) {
		Patient patient = patientRepository.findOne(patientId);
		medication.setPatient(patient);
		Medication savedMedication = medicationRepository.save(medication);
		patient.getMedications().add(medication);
		return savedMedication;
	}
	
	
	@RequestMapping(value="/patient/{patientId}/medication", method=RequestMethod.PUT)
	public @ResponseBody Medication updateMedicationd(@PathVariable long patientId,
			@RequestBody Medication medication) {
		Patient patient = patientRepository.findOne(patientId);
		medication.setPatient(patient);
		Medication savedMedication = medicationRepository.save(medication);
		return savedMedication;
	}

	
	@RequestMapping(value="/patient/{patientId}/medication/{id}", method=RequestMethod.DELETE)
	public @ResponseBody Medication deleteMedication(@PathVariable long patientId, @PathVariable long id) {
		Patient patient = patientRepository.findOne(patientId);
		Medication medication = medicationRepository.findOne(id);
		patient.getMedications().remove(medication);
		medicationRepository.delete(medication);
		medication.setPatient(null);
		return medication;
	}
			

	@RequestMapping(value="/doctor/{doctorId}/patient/{id}/checkin", method=RequestMethod.GET)
	public @ResponseBody Collection<SymptomCheckin> findCheckinByDoctorIdAndPatientId(
			@PathVariable long id,
			@PathVariable long doctorId){
		
		// security check patient is assigned to doctor
		Patient patient = patientRepository.findByIdAndDoctorId(id, doctorId);
		if (patient == null) {
			throw new ResourceNotFoundException("No patinent with id " + id + " and assigned doctorId " + doctorId + " found");
		}
		Iterable<SymptomCheckin> symptomCheckins = symptomCheckinRepository.findByPatientIdOrderByCheckinDateDesc(patient.getId());
		return Util.makeCollection(symptomCheckins);
	}		
	

	/**
	 * Perform a symtom checkin. Sends a Google Cloud Notification to the doctor assigned to the patient of the 
	 * checkin if the hours of pains exceed the specified limit.
	 * 
	 * @param symptomChecking
	 * @return store symptom checkin
	 */
	@Secured("ROLE_PATIENT")
	@RequestMapping(value="/checkin", method=RequestMethod.POST)
	public @ResponseBody SymptomCheckin createCheckin(@RequestBody SymptomCheckin symptomChecking){
		SymptomCheckin symptomCheckin = symptomCheckinRepository.save(symptomChecking);
		
		if (painLevelExceeded(symptomCheckin)) {
			Patient patient = patientRepository.findOne(symptomCheckin.getPatientId());
			if (patient == null) {
				throw new ResourceNotFoundException("No patient with id " + symptomCheckin.getPatientId() + " found");
			}
			googleCloudMessagingService.notifyDoctor(patient);
			log.debug("Checkin " + symptomCheckin.getId() + " notification required doctorId " + patient.getDoctorId());
		} else {
			log.debug("Checkin " + symptomCheckin.getId() + " no notification required");
		}
		
		return symptomCheckin;
	}


	/**
	 * Saved image of a symptom checkin
	 * 
	 * @param symptomCheckinId
	 * @param image
	 * @return
	 */
	@Secured("ROLE_PATIENT")
	@RequestMapping(value="/checkin/{symptomCheckinId}/image", method=RequestMethod.POST)
	public @ResponseBody SymptomCheckin createMultipartCheckin(
			@PathVariable long symptomCheckinId, @RequestParam("image") MultipartFile image) {		
		try {
			SymptomCheckin symptomCheckin = symptomCheckinRepository.findOne(symptomCheckinId);
			if (symptomCheckin == null) {
				throw new ResourceNotFoundException("symptom checkin id " + symptomCheckinId + " not found");
			}
			SymptomCheckinImage symptomCheckinImage = new SymptomCheckinImage(symptomCheckin.getId(), image.getBytes());
			symptomCheckinImageRepository.save(symptomCheckinImage);
			return symptomCheckin;
		} catch (IOException ioException) {
			throw new ResourceNotFoundException("Cannot save symptom checkin with original file name " + image.getOriginalFilename() 
					+ " and name " + image.getName());
		}
	}


	/**
	 * Returns image of a symptom checkin
	 * @param checkinId
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/checkin/{checkinId}/image", method=RequestMethod.GET)
	public void getImageData(@PathVariable("checkinId")  Long checkinId,
							HttpServletResponse response) throws IOException  {
		SymptomCheckinImage symptomCheckinImage = symptomCheckinImageRepository.findBySymptomCheckinId(checkinId);
		if (symptomCheckinImage == null) {
			throw new ResourceNotFoundException("No symptom checkin image with id " + checkinId + " found!"); 
		}
		ServletOutputStream servletOutputStream = response.getOutputStream();
		servletOutputStream.write(symptomCheckinImage.getData());
	}	
	
	
	/**
	 * Returns true if the the patient of the symptomCheckin had pains of leven severe 
	 * for more than 24 hours. For this verification the pain levels of the checkins of
	 * the last two days will be verified.
	 * 
	 * @param symptomCheckin
	 * @return
	 */
	private boolean painLevelExceeded(SymptomCheckin symptomCheckin) {
		
		//                          day hour, minute, second, millisecond
		long fromMs = new Date().getTime() - (2 * 24 * 60 * 60 * 1000);
		Date from = new Date(fromMs);
		
		Iterable<SymptomCheckin> symptomCheckins =
				symptomCheckinRepository.findByPatientIdAndCheckinDateGreaterThanOrderByCheckinDateDesc(
						symptomCheckin.getPatientId(), from);
		
		return painLevelExceeded(Util.makeList(symptomCheckins));
	}
	
	
	/**
	 * A doctor is alerted if a patient experiences 
	 * - 12 of “severe pain,” 
	 * - 16 or more hours of “moderate” or “severe pain” or 
	 * - 12 hours of “I can’t eat.”
	 * 
	 * @param symptomCheckinList
	 * @return
	 */
	public boolean painLevelExceeded(List<SymptomCheckin> symptomCheckinList) {
		
		Collections.sort(symptomCheckinList, Collections.reverseOrder());
		
		long hoursSeverPain = hoursOfPainRule(symptomCheckinList, new MouthPainSevere());
		long hoursSeverOrModeratPain = hoursOfPainRule(symptomCheckinList, new MouthPainModerateOrMore());
		long hoursCannotEat = hoursOfPainRule(symptomCheckinList, new CannotEat());
		
		return hoursSeverPain > 12 || hoursSeverOrModeratPain > 16 || hoursCannotEat > 12;		
	}
	
	
	/**
	 * Returns the hours the pain rule is true counting from the most recent checkin
	 * 
	 * @param symptomCheckins SymptomCheckin must be reverse sorted by checkin date.
	 * @param painRule The rule to verify
	 * 
	 * @return hours from most recent date the rule is true.
	 */
	public long hoursOfPainRule(Collection<SymptomCheckin> symptomCheckins, PainRule painRule) {
		// at least two two checkins
		if (symptomCheckins.size()  < 2) {
			return 0;
		}
		// most recent leven must be severe
		SymptomCheckin[] checkins = new SymptomCheckin[0];
		checkins = symptomCheckins.toArray(checkins);
		SymptomCheckin mostRecent = checkins[0];
		if (!painRule.isTrue(mostRecent)) {
			return 0;
		}
		
		Date to = mostRecent.getCheckinDate();
		Date from = null;
		for (SymptomCheckin next : symptomCheckins) {
			if (painRule.isTrue(next)) {
				from = next.getCheckinDate();
			} else {
				break;
			}
		}
		if (from == null) {
			 return 0;
		}
		
		long duration = to.getTime() - from.getTime();
		return duration / (1000 * 60 * 60); 
	}


	
	public interface PainRule {
		boolean isTrue(SymptomCheckin symptomCheckin);
	}
	
	
	/**
	 * Mouth pain rule.
	 * Rule isTrue(..) returns true if mouth pain == severe
	 */
	public static class MouthPainSevere implements PainRule {
		public boolean isTrue(SymptomCheckin symptomCheckin) {
			String mouthPain = symptomCheckin.getMouthPain();
			return mouthPain != null 
					&& mouthPain.equals(SymptomCheckin.SymptomCheckinAnswer.SEVERE.name());			
		}
	}
	
	/**
	 * Mouth pain rule.
	 * Rule isTrue(..) returns true if mouth pain == severe or mouth pain == moderate
	 */
	public static class MouthPainModerateOrMore implements PainRule {
		public boolean isTrue(SymptomCheckin symptomCheckin) {
			String mouthPain = symptomCheckin.getMouthPain();
			return mouthPain != null 
					&& (mouthPain.equals(SymptomCheckin.SymptomCheckinAnswer.SEVERE.name())
							|| mouthPain.equals(SymptomCheckin.SymptomCheckinAnswer.MODERATE.name()));			
		}		
	}
	
	
	/**
	 * Eating pain rule.
	 * Rule isTrue(..) returns true if pain stops from eating has level cannot eat.
	 */
	public static class CannotEat implements PainRule {
		public boolean isTrue(SymptomCheckin symptomCheckin) {
			String paintStopFromEating = symptomCheckin.getPainStopFromEating();
			return paintStopFromEating != null 
					&& paintStopFromEating.equals(SymptomCheckin.SymptomCheckinAnswer.CAN_NOT_EAT.name());
		}		
	}

	
	//
	// Common for all services
	//
    @ExceptionHandler(value={ResourceNotFoundException.class, IOException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody ResponseEntity<String> handleIOException(ResourceNotFoundException ex) {
    	return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }		
	
	
}
