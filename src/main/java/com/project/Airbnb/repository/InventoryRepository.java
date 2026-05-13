package com.project.Airbnb.repository;

import com.project.Airbnb.entity.Hotel;
import com.project.Airbnb.entity.Inventory;

import com.project.Airbnb.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    void deleteByDateAfterAndRoom(LocalDate date, Room room);
}
