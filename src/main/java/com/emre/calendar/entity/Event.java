package com.emre.calendar.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String url;
    private Boolean important;
    private Boolean canceled;
    private LocalDateTime createdAt = LocalDateTime.now();
}
