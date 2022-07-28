package com.magg.reservation.repository;

import com.magg.reservation.domain.Availability;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailabilityRepository extends CrudRepository<Availability, String>, JpaSpecificationExecutor<Availability>
{
}
