package com.project.Airbnb.repository;

import com.project.Airbnb.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {//simplejparepository is a class implementing this interface
}
