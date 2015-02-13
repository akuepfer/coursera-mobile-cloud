package org.aku.sm.smserver.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.access.prepost.PostAuthorize;

public interface PatientRepository extends CrudRepository<Patient, Long> {
	Iterable<Patient> findByDoctorIdOrderByLastNameAsc(long doctorId);


	  @Query("select p "
	  		+ "from Patient p, "
	  		+ "     SymptomCheckin s "
	  		+ "where p.doctorId =?1 "
	  		+ "  and s.patientId = p.id "
	  		+ "group by p "
	  		+ "order by max(s.checkinDate) desc")
	Iterable<Patient> findByDoctorIdOrderByCheckinDateDesc(long doctorId);
	  
	@PostAuthorize("returnObject.doctorId == authentication.id")
	Iterable<Patient> findByLastNameLike(String lastName);
	
	@PostAuthorize("returnObject.Id == principal.id || returnObject.doctorId == principal.id")
	Patient findByIdAndDoctorId(long id, long doctorId);

	Iterable<Patient> findByDoctorIdAndLastNameLikeOrderByLastNameAsc(long doctorId, String lastName);	
}

