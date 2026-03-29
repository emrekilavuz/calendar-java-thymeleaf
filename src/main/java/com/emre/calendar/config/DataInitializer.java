package com.emre.calendar.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.emre.calendar.entity.Calendar;
import com.emre.calendar.repository.CalendarRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initCalendar(CalendarRepository calendarRepository) {
        return args -> {
            if (calendarRepository.count() == 0) {
                Calendar calendar = new Calendar();
                calendar.setCurrDate(LocalDate.now());
                calendar.setCurrMonth(LocalDate.now().getMonthValue());
                calendar.setScrollDate(LocalDate.now());
                calendar.setScrollMonth(LocalDate.now().getMonthValue());
                calendar.setShowLeftAndRightBar(true);

                calendarRepository.save(calendar);
            }
        };
    }
}
