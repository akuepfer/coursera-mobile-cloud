package org.aku.sm.smserver.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SymptomCheckinRepository extends CrudRepository<SymptomCheckin, Long> {

	Iterable<SymptomCheckin> findByPatientIdOrderByCheckinDateDesc(long patientId);

	Iterable<SymptomCheckin> findByPatientIdAndCheckinDateGreaterThanOrderByCheckinDateDesc(long patientId, Date checkinDate);

	  @Query("select s0.mouthPain from SymptomCheckin s0 "
	  		+ "where s0.patientId = ?1"
	  		+ "  and s0.checkinDate = (select max(checkinDate) from SymptomCheckin s1 "
	  		+ "                        where s1.patientId = ?1)")
	String findLastMouthPain(long id);
}

