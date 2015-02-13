package org.aku.sm.smserver.repository;

import org.springframework.data.repository.CrudRepository;

public interface MedicationIntakeRepository extends CrudRepository<MedicationIntake, Long> {

	Iterable<MedicationIntake> findBySymptomCheckinId(long patientId);

}

