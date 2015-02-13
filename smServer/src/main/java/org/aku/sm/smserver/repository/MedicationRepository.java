package org.aku.sm.smserver.repository;

import org.springframework.data.repository.CrudRepository;

public interface MedicationRepository extends CrudRepository<Medication, Long> {

	Iterable<Medication> findByPatientId(long patientId);

}

