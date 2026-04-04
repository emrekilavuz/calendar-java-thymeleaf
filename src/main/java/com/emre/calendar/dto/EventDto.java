package com.emre.calendar.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EventDto {

    private Long id;
    private String title;
    private String description;
    private String url;
    private Boolean important;
    private Boolean canceled;
    private LocalDateTime createdAt;
    private Integer calendarId;
    private Integer hour;
    private Integer minute;

}
