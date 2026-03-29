package com.emre.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emre.calendar.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // JpaRepository sayesinde save(), findAll(), deleteById() gibi metodlar hazır gelir.
}
