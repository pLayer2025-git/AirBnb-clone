package com.project.Airbnb.repository;

import com.project.Airbnb.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long> {
    Optional<Booking> findByPaymentSessionId(String sessionId);
}
