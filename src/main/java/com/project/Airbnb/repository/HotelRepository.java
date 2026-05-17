package com.project.Airbnb.repository;

import com.project.Airbnb.entity.Hotel;
import com.project.Airbnb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {
    List<Hotel> findByOwner(User owner);//simplejparepository is a class implementing this interface
}
