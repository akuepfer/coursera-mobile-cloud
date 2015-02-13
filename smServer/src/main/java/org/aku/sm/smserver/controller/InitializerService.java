package org.aku.sm.smserver.controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.aku.sm.smserver.common.DateFormatter;
import org.aku.sm.smserver.repository.Doctor;
import org.aku.sm.smserver.repository.DoctorRepository;
import org.aku.sm.smserver.repository.Medication;
import org.aku.sm.smserver.repository.MedicationIntake;
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
import org.springframework.stereotype.Controller;

@Controller
public class InitializerService {
	 private Log log = LogFactory.getLog(InitializerService.class);

	@Autowired
	DoctorRepository dr;

	@Autowired
	PatientRepository pr;

	@Autowired
	MedicationRepository mr;

	@Autowired
	SymptomCheckinImageRepository si;

	@Autowired
	SymptomCheckinRepository sr;

	@Autowired
	MedicationIntakeRepository mi;

	@PostConstruct
	public void initTestDatabase() {

		Doctor d1 = dr.save(new Doctor("aflower", "p0", "Aron", "Flower", "Dr. med", "aa-aa-aa"));
		Doctor d2 = dr.save(new Doctor("btree", "p0", "Ben", "Tree", "Dr. med", "bb-bb-bb"));
		Doctor d3 = dr.save(new Doctor("cleaf", "p0", "Can", "Leaf", "Dr. med",	"cc-cc-cc"));

		System.out.println("doctor ids: " + d1.getId() + " " + d2.getId() + " "
				+ d3.getId());

		Patient p1 = pr.save(new Patient("cblack", "p0", "Chris", "Black",	"11.11.11", d1.getId(), null));
		List<Medication> medications1 = new ArrayList<Medication>();
		medications1.add(new Medication("Lortab",
				"3 times at 8, 12 and 18 o'clock", p1));
		medications1.add(new Medication("OxyContin", "take after dinner", p1));
		medications1.add(new Medication("Tactinal Extra Strength",
				"take before sleeping", p1));
		p1.setMedications(medications1);
		pr.save(p1);

		Patient p2 = pr.save(new Patient("dblue", "p0", "Dan", "Blue",
				"22.22.22", d1.getId(), null));
		List<Medication> medications2 = new ArrayList<Medication>();
		medications2.add(new Medication("Lortab",
				"3 times at 8, 12 and 18 o'clock", p2));
		medications2
				.add(new Medication("OxyContin", "take after breakfast", p2));
		medications2.add(new Medication("Tactinal Extra Strength",
				"take before dinner", p2));
		p2.setMedications(medications2);
		pr.save(p2);

		Patient p3 = pr.save(new Patient("jcarmine", "p0", "Jane", "Carmine",
				"33.33.33", d1.getId(), null));
		Patient p4 = pr.save(new Patient("mcrimson", "p0", "Mia", "Crimson",
				"44.44.44", d1.getId(), null));
		Patient p5 = pr.save(new Patient("hgreen", "p0", "Heb", "Green",
				"55.55.55", d1.getId(), null));
		Patient p6 = pr.save(new Patient("amaron", "p0", "Al", "Maroon",
				"66.66.66", d1.getId(), null));
		Patient p7 = pr.save(new Patient("dmauve", "p0", "Dix", "Mauve",
				"77.77.77", d1.getId(), null));
		Patient p8 = pr.save(new Patient("hruby", "p0", "Hub", "Ruby",
				"88.88.88", d1.getId(), null));
		Patient p9 = pr.save(new Patient("ssangria", "p0", "Sing", "Sangria",
				"99.99.99", d1.getId(), null));
		Patient p10 = pr.save(new Patient("jturquise", "p0", "Jack",
				"Turquoise", "12.12.12", d1.getId(), null));
		Patient p11 = pr.save(new Patient("dyellow", "p0", "Dug", "Yellow",
				"13.13.13", d2.getId(), null));
		Patient p12 = pr.save(new Patient("mplum", "p0", "Mike", "Plum",
				"14.14.14", d2.getId(), null));
		Patient p13 = pr.save(new Patient("soliver", "p0", "Sue", "Olive",
				"15.15.15", d2.getId(), null));
		Patient p14 = pr.save(new Patient("petepear", "p0", "Pete", "Pear",
				"16.16.16", d2.getId(), null));
		Patient p15 = pr.save(new Patient("djade", "p0", "Dick", "Jade",
				"17.17.17", d2.getId(), null));
		Patient p16 = pr.save(new Patient("jerin", "p0", "Jane", "Erin",
				"18.18.18", d2.getId(), null));
		Patient p17 = pr.save(new Patient("jcooper", "p0", "James", "Copper",
				"19.19.19", d2.getId(), null));

		
		long currentTime = new Date().getTime();
		long hour = 1000 * 60 * 60;
		long day = 24 * hour;
		
		/**
		 * Check-in Patient 1
		 */		
		// -23 hour
		long testTime = currentTime -23 * hour;
		SymptomCheckin s1 = sr.save(new SymptomCheckin(p1.getId(), new Date(testTime), "WELL_CONTROLLED", "SOME", "YES"));
		List<MedicationIntake> medicationIntakes1 = new ArrayList<MedicationIntake>();
		medicationIntakes1.add(new MedicationIntake(s1, "Lortab", "YES",     new Date(testTime - 1 * hour)));
		medicationIntakes1.add(new MedicationIntake(s1, "OxyContin", "YES",  new Date(testTime - 2 * hour)));
		medicationIntakes1.add(new MedicationIntake(s1, "Tactinal Extra Strength", "NO", null));
		s1.setMedicationIntakes(medicationIntakes1);
		sr.save(s1);
		si.save(new SymptomCheckinImage(s1.getId(), loadImage("symptom1.jpg")));

		// -18 hour
		testTime = currentTime - 18 * hour;
		SymptomCheckin s2 = sr.save(new SymptomCheckin(p1.getId(),	 new Date(testTime), "MODERATE", "CAN_NOT_EAT", "YES"));
		List<MedicationIntake> medicationIntakes2 = new ArrayList<MedicationIntake>();
		medicationIntakes2.add(new MedicationIntake(s2, "Lortab", "YES",  new Date(testTime - 1 * hour)));
		medicationIntakes2.add(new MedicationIntake(s2, "OxyContin", "NO", null));
		medicationIntakes2.add(new MedicationIntake(s2, "Tactinal Extra Strength", "NO", null));
		s2.setMedicationIntakes(medicationIntakes2);
		sr.save(s2);
		si.save(new SymptomCheckinImage(s2.getId(), loadImage("symptom2.jpg")));

		// -10 hour
		testTime = currentTime - 10 * hour;
		SymptomCheckin s3 = sr.save(new SymptomCheckin(p1.getId(),  new Date(testTime), "SEVERE", "SOME", "YES"));
		List<MedicationIntake> medicationIntakes3 = new ArrayList<MedicationIntake>();
		medicationIntakes3.add(new MedicationIntake(s3, "Lortab", "NO",  new Date(testTime - 2 * hour)));
		medicationIntakes3.add(new MedicationIntake(s3, "OxyContin", "YES",  new Date(testTime - 3 * hour)));
		medicationIntakes3.add(new MedicationIntake(s3,	"Tactinal Extra Strength", "NO", null)); 
		s3.setMedicationIntakes(medicationIntakes3);
		sr.save(s3);
		si.save(new SymptomCheckinImage(s3.getId(), loadImage("symptom3.jpg")));

		// -5 hour
		testTime = currentTime - 5 * hour;
		SymptomCheckin s4 = sr.save(new SymptomCheckin(p1.getId(),  new Date(testTime), "SEVERE", "NO", "NO"));
		List<MedicationIntake> medicationIntakes4 = new ArrayList<MedicationIntake>();
		medicationIntakes4.add(new MedicationIntake(s4, "Lortab", "YES",  new Date(testTime - 1 * hour)));
		medicationIntakes4.add(new MedicationIntake(s4, "OxyContin", "YES",  new Date(testTime - 2 * hour)));
		medicationIntakes4.add(new MedicationIntake(s4, "Tactinal Extra Strength", "NO", null));
		s4.setMedicationIntakes(medicationIntakes4);
		sr.save(s4);
		si.save(new SymptomCheckinImage(s4.getId(), loadImage("symptom2.jpg")));

		// -1 hour
		testTime = currentTime - 1 * hour;
		SymptomCheckin s5 = sr.save(new SymptomCheckin(p1.getId(),  new Date(testTime), "SEVERE", "NO", "NO"));
		List<MedicationIntake> medicationIntakes5 = new ArrayList<MedicationIntake>();
		medicationIntakes5.add(new MedicationIntake(s5, "Lortab", "YES",  new Date(testTime - 1 * hour)));
		medicationIntakes5.add(new MedicationIntake(s5, "OxyContin", "YES",  new Date(testTime - 1 * hour)));
		medicationIntakes5.add(new MedicationIntake(s5, "Tactinal Extra Strength", "NO", null));
		s5.setMedicationIntakes(medicationIntakes5);
		sr.save(s5);
		si.save(new SymptomCheckinImage(s5.getId(), loadImage("symptom3.jpg")));

		/**
		 * Check-in Patient 2
		 */
		testTime = currentTime - 10 * hour;
		SymptomCheckin s6 = sr.save(new SymptomCheckin(p2.getId(),  new Date(testTime), "MODERATE", "SOME", "YES"));
		testTime = currentTime - 5 * hour;
		SymptomCheckin s7 = sr.save(new SymptomCheckin(p2.getId(),  new Date(testTime), "MODERATE", "NO", "NO"));
		testTime = currentTime - 1 * hour;
		SymptomCheckin s8 = sr.save(new SymptomCheckin(p2.getId(),  new Date(testTime), "MODERATE", "NO", "NO"));

		addSymptomCheckin(p3, "SEVERE");
		addSymptomCheckin(p4, "MODERATE");
		addSymptomCheckin(p5, "WELL_CONTROLLED");
		addSymptomCheckin(p6, "WELL_CONTROLLED");
		addSymptomCheckin(p7, "MODERATE");
		addSymptomCheckin(p8, "SEVERE");
		addSymptomCheckin(p9, "SEVERE");
		addSymptomCheckin(p10, "MODERATE");
		addSymptomCheckin(p11, "WELL_CONTROLLED");
		addSymptomCheckin(p12, "SEVERE");
		addSymptomCheckin(p13, "WELL_CONTROLLED");
		addSymptomCheckin(p14, "WELL_CONTROLLED");
		addSymptomCheckin(p15, "MODERATE");
		addSymptomCheckin(p16, "WELL_CONTROLLED");
		addSymptomCheckin(p17, "SEVERE");
	}

	private byte[] loadImage(String string) {
		byte[] fileData = new byte[0];
		
		URL url = null;
		try {
			ClassLoader cl = this.getClass().getClassLoader();
			url = cl.getResource(string);
			File file = new File(url.toURI());
			DataInputStream dataIs = new DataInputStream(new FileInputStream(file));
			fileData = new byte[(int)file.length()];
			dataIs.readFully(fileData);
			dataIs.close();
		} catch (Exception e) {
			log.error("loadImage " + string + " failed, url " + (url == null ? "<null>" : url.toString()), e);
		}
		return fileData;
	}

	private void addSymptomCheckin(Patient pn, String mouthPain) {

		SymptomCheckin sc = sr.save(new SymptomCheckin(pn.getId(),
				DateFormatter.parseDate("2014-11-22 08:11:21"), mouthPain,
				"NO", "NO"));
		List<MedicationIntake> medicationIntakes5 = new ArrayList<MedicationIntake>();
		medicationIntakes5.add(new MedicationIntake(sc,
				"Tactinal Extra Strength", "NO", null));
		sc.setMedicationIntakes(medicationIntakes5);
		sr.save(sc);
	}
}
