package org.aku.sm.smserver.repository;

import org.springframework.data.repository.CrudRepository;

public interface SymptomCheckinImageRepository extends CrudRepository<SymptomCheckinImage, Long> {

	SymptomCheckinImage findBySymptomCheckinId(long symptomCheckinId);
	
}

