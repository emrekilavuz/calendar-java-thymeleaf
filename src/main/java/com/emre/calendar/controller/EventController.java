package com.emre.calendar.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.emre.calendar.dto.EventDto;
import com.emre.calendar.entity.Calendar;
import com.emre.calendar.entity.Event;
import com.emre.calendar.repository.CalendarRepository;
import com.emre.calendar.repository.EventRepository;

@Controller
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventRepository repository;
    @Autowired
    private CalendarRepository calRepository;

    @GetMapping("/day/{eyear}/{emonth}/{eday}")
    public String dayEvents(Model model, @PathVariable Integer eyear, @PathVariable Integer emonth,
            @PathVariable Integer eday) {

        LocalDate date = LocalDate.of(eyear, emonth, eday);

        LocalDateTime start = date.atStartOfDay(); // 2026-03-30 00:00:00
        LocalDateTime end = date.atTime(23, 59, 59); // 2026-03-30 23:59:59

        List<Event> events = repository.findByCreatedAtBetween(start, end);

        model.addAttribute("events", events);
        List<Calendar> calendars = calRepository.findAll();
        if (calendars.size() > 0) {
            EventDto newEvent = new EventDto();
            newEvent.setCalendarId(calendars.get(0).getId().intValue());
            newEvent.setCreatedAt(start);
            model.addAttribute("event", newEvent);
        }

        return "day-events";
    }

    // Yeni bir görev eklemek için POST isteği
    @PostMapping("/add")
    public String addEvent(@ModelAttribute EventDto event) {
        Event newEvent = new Event();
        Optional<Calendar> calendar = calRepository.findById(event.getCalendarId().longValue());
        if (calendar.isPresent()) {
            newEvent.setCalendar(calendar.get());
            newEvent.setCanceled(event.getCanceled());
            newEvent.setDescription(event.getDescription());
            newEvent.setImportant(event.getImportant());
            newEvent.setTitle(event.getTitle());
            if(!event.getUrl().isBlank()) {
                newEvent.setUrl(event.getUrl());
            }
            else {
                newEvent.setUrl(null);
            }
            LocalDateTime mDateTime = event.getCreatedAt().withHour(event.getHour()).withMinute(event.getMinute());
            newEvent.setCreatedAt(mDateTime);
            repository.save(newEvent);
            String url = "redirect:/event/day/";
            Integer year = event.getCreatedAt().getYear();
            Integer month = event.getCreatedAt().getMonthValue();
            Integer day = event.getCreatedAt().getDayOfMonth();
            url = url.concat(year.toString());
            url = url.concat("/");
            url = url.concat(month.toString());
            url = url.concat("/");
            url = url.concat(day.toString());
            return url; // İşlem bitince ana sayfaya dön
        } else {
            return null;
        }
    }

    // Görevi silmek için GET isteği
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        Optional<Event> event = repository.findById(id);
        if (event.isPresent()) {
            Event eventVal = event.get();
            String url = "redirect:/event/day/";
            Integer year = eventVal.getCreatedAt().getYear();
            Integer month = eventVal.getCreatedAt().getMonthValue();
            Integer day = eventVal.getCreatedAt().getDayOfMonth();
            url = url.concat(year.toString());
            url = url.concat("/");
            url = url.concat(month.toString());
            url = url.concat("/");
            url = url.concat(day.toString());
            repository.delete(eventVal);
            return url;
        } else {
            return "";
        }

    }

    // Olayı iptal için GET isteği
    @GetMapping("/cancel/{id}")
    public String cancelEvent(@PathVariable Long id) {
        try {
            Optional<Event> event = repository.findById(id);
            if (event.isPresent()) {
                Event eventVal = event.get();
                eventVal.setCanceled(!eventVal.getCanceled());
                repository.save(eventVal);
                String url = "redirect:/event/day/";
                Integer year = eventVal.getCreatedAt().getYear();
                Integer month = eventVal.getCreatedAt().getMonthValue();
                Integer day = eventVal.getCreatedAt().getDayOfMonth();
                url = url.concat(year.toString());
                url = url.concat("/");
                url = url.concat(month.toString());
                url = url.concat("/");
                url = url.concat(day.toString());
                return url;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return "redirect:/";
    }

    // Olayı önemli yapmak için GET isteği
    @GetMapping("/important/{id}")
    public String toggleEventSignificance(@PathVariable Long id) {
        try {
            Optional<Event> event = repository.findById(id);
            if (event.isPresent()) {
                Event eventVal = event.get();
                eventVal.setImportant(!eventVal.getImportant());
                repository.save(eventVal);
                String url = "redirect:/event/day/";
                Integer year = eventVal.getCreatedAt().getYear();
                Integer month = eventVal.getCreatedAt().getMonthValue();
                Integer day = eventVal.getCreatedAt().getDayOfMonth();
                url = url.concat(year.toString());
                url = url.concat("/");
                url = url.concat(month.toString());
                url = url.concat("/");
                url = url.concat(day.toString());
                return url;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return "redirect:/";
    }
}