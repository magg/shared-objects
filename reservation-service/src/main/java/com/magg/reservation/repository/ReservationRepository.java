package com.magg.reservation.repository;

import com.magg.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, String>, JpaSpecificationExecutor<Reservation>
{
}
