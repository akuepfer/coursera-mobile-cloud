package org.aku.sm.smserver.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.aku.sm.smserver.repository.SymptomCheckin;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the pain level rules
 *
 * A doctor is alerted if a patient experiences
 * - 12 of “severe pain,”
 * - 16 or more hours of “moderate” or “severe pain” or
 * - 12 hours of “I can’t eat.”
 */
public class PainLevelTest {

	
	List<SymptomCheckin> checkins;
	

	MedicationService medicationService = new MedicationService();
	

	@Test
	public void testMouthPain() throws ParseException {
		checkins = new ArrayList<SymptomCheckin>();
		addCheckin(checkins, "2014-01-01 10:00", 
				SymptomCheckin.SymptomCheckinAnswer.SEVERE.name(), 
				SymptomCheckin.SymptomCheckinAnswer.CAN_NOT_EAT.name());
		addCheckin(checkins, "2014-01-01 00:00", 
				SymptomCheckin.SymptomCheckinAnswer.SEVERE.name(), 
				SymptomCheckin.SymptomCheckinAnswer.CAN_NOT_EAT.name());
		
		Collections.sort(checkins, Collections.reverseOrder());
		
		Assert.assertEquals(10, medicationService.hoursOfPainRule(checkins, new MedicationService.MouthPainSevere()));
		Assert.assertEquals(10, medicationService.hoursOfPainRule(checkins, new MedicationService.MouthPainModerateOrMore()));
		Assert.assertEquals(10, medicationService.hoursOfPainRule(checkins, new MedicationService.CannotEat()));
		
		
		checkins = new ArrayList<SymptomCheckin>();
		addCheckin(checkins, "2014-01-01 00:00", 
				SymptomCheckin.SymptomCheckinAnswer.SEVERE.name(), 
				SymptomCheckin.SymptomCheckinAnswer.SOME.name());
		addCheckin(checkins, "2014-01-01 10:00", 
				SymptomCheckin.SymptomCheckinAnswer.SEVERE.name(), 
				SymptomCheckin.SymptomCheckinAnswer.CAN_NOT_EAT.name());
		addCheckin(checkins, "2014-01-01 12:00", 
				SymptomCheckin.SymptomCheckinAnswer.MODERATE.name(), 
				SymptomCheckin.SymptomCheckinAnswer.CAN_NOT_EAT.name());
		
		Collections.sort(checkins, Collections.reverseOrder());
		
		Assert.assertEquals( 0, medicationService.hoursOfPainRule(checkins, new MedicationService.MouthPainSevere()));
		Assert.assertEquals(12, medicationService.hoursOfPainRule(checkins, new MedicationService.MouthPainModerateOrMore()));
		Assert.assertEquals( 2, medicationService.hoursOfPainRule(checkins, new MedicationService.CannotEat()));
		
		
		
	}

	
	public void addCheckin(List<SymptomCheckin> symptomCheckins,
			String checkinDate, String mouthPain, String painStopFromEating) throws ParseException {
		
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SymptomCheckin sc = new SymptomCheckin();
		sc.setMouthPain(mouthPain);
		sc.setPainStopFromEating(painStopFromEating);
		sc.setCheckinDate(sd.parse(checkinDate));
		symptomCheckins.add(sc);
	}
	
	
}
