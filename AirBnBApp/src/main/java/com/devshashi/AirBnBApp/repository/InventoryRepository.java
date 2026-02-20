package com.devshashi.AirBnBApp.repository;

import com.devshashi.AirBnBApp.entity.Inventory;
import com.devshashi.AirBnBApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByDateAfterAndRoom(LocalDate date, Room room);
}