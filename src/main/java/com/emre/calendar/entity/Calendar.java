package com.emre.calendar.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDate currDate = LocalDate.now();
    private Integer currMonth = LocalDate.now().getMonthValue();
    private Integer scrollMonth = LocalDate.now().getMonthValue();
    private LocalDate scrollDate = LocalDate.now();
    private Boolean showLeftAndRightBar = true;
    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;
}
