package com.emre.calendar.controller;

import java.util.Optional;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.emre.calendar.entity.Calendar;
import com.emre.calendar.entity.Event;
import com.emre.calendar.repository.CalendarRepository;
import com.emre.calendar.repository.EventRepository;

@Controller
public class CalendarController {
    @Autowired
    private EventRepository repository;
    @Autowired
    private CalendarRepository calRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<String> monthNames = new ArrayList<String>(Arrays.asList("Ocak", "Şubat", "Mart", "Nisan", "Mayıs",
                "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"));

        List<Calendar> appCalendars = calRepository.findAll();
        Calendar appCalendar = null;

        if (appCalendars.size() > 0) {
            appCalendar = appCalendars.get(0);
        }

        if (appCalendar != null) {
            LocalDate current = appCalendar.getCurrDate();
            String currentMonthName = current.getMonth().getDisplayName(TextStyle.FULL, new Locale("tr", "TR"));
            YearMonth month = YearMonth.of(current.getYear(), current.getMonthValue());
            Integer monthDayLength = month.lengthOfMonth();

            LocalDate scroll = appCalendar.getScrollDate();
            String scrollMonthName = scroll.getMonth().getDisplayName(TextStyle.FULL, new Locale("tr", "TR"));
            String scrollWeekDayName = scroll.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("tr", "TR"));
            YearMonth scrollMonth = YearMonth.of(scroll.getYear(), scroll.getMonthValue());
            Integer scrollMonthDayLength = scrollMonth.lengthOfMonth();
            Integer scrollYear = scroll.getYear();
            LocalDate prevScrollM = scroll.minusMonths(1);
            YearMonth prevScrollMonth = YearMonth.of(prevScrollM.getYear(), prevScrollM.getMonth());
            Integer scrollPrevLength = prevScrollMonth.lengthOfMonth();
            LocalDate firstDayOfSM = scroll.withDayOfMonth(1);
            Integer scrollSkip = firstDayOfSM.getDayOfWeek().getValue() - 1;
            Integer monthToday = current.getDayOfMonth();
            Integer scrollToday = scroll.getDayOfMonth();
            Boolean samePage = month.equals(scrollMonth);
            Boolean showBar = appCalendar.getShowLeftAndRightBar();

            model.addAttribute("monthLength", monthDayLength);
            model.addAttribute("monthName", currentMonthName);
            model.addAttribute("monthToday", monthToday);
            model.addAttribute("scrollToday", scrollToday);
            model.addAttribute("samePage", samePage);
            model.addAttribute("scrollMonth", scrollMonthName);
            model.addAttribute("scrollMonthValue", scroll.getMonthValue());
            model.addAttribute("scrollWeekDayName", scrollWeekDayName);
            model.addAttribute("scrollLength", scrollMonthDayLength);
            model.addAttribute("scrollYear", scrollYear);
            model.addAttribute("scrollSkip", scrollSkip);
            model.addAttribute("scrollPrevLength", scrollPrevLength);
            model.addAttribute("showBar", showBar);
            model.addAttribute("monthNames", monthNames);
        }

        return "index"; // src/main/resources/templates/index.html dosyasını arar
    }

    @GetMapping("/day/{eyear}/{emonth}/{eday}")
    public String dayEvents(Model model, @PathVariable Integer eyear, @PathVariable Integer emonth,
            @PathVariable Integer eday) {

        LocalDate date = LocalDate.of(eyear, emonth, eday);

        LocalDateTime start = date.atStartOfDay(); // 2026-03-30 00:00:00
        LocalDateTime end = date.atTime(23, 59, 59); // 2026-03-30 23:59:59

        List<Event> events = repository.findByCreatedAtBetween(start, end);

        model.addAttribute("events", events);

        return "day-events";
    }

    // Yeni bir görev eklemek için POST isteği
    @PostMapping("/add")
    public String addEvent(@RequestParam String title) {
        List<Calendar> calendars = calRepository.findAll();
        if (calendars.size() > 0) {
            Event newEvent = new Event();
            newEvent.setTitle(title);
            newEvent.setImportant(false);
            newEvent.setCanceled(false);
            newEvent.setCalendar(calendars.get(0));
            repository.save(newEvent);
        }

        return "redirect:/"; // İşlem bitince ana sayfaya dön
    }

    // Görevi silmek için GET isteği
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/";
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
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return "redirect:/";
    }

    @GetMapping("/change-year/{year}")
    public String changeYear(@PathVariable Integer year) {
        Optional<Calendar> calendar = calRepository.findById(Long.valueOf(1));
        if (calendar.isPresent()) {
            Calendar appCalendar = calendar.get();
            LocalDate scrollDate = appCalendar.getScrollDate();
            LocalDate updated = scrollDate.withYear(year);
            appCalendar.setScrollDate(updated);
            appCalendar.setScrollMonth(updated.getMonthValue());
            calRepository.save(appCalendar);
        }
        return "redirect:/";
    }

    @GetMapping("/change-day/{day}")
    public String changeDay(@PathVariable Integer day) {
        Optional<Calendar> calendar = calRepository.findById(Long.valueOf(1));
        if (calendar.isPresent()) {
            Calendar appCalendar = calendar.get();
            LocalDate scrollDate = appCalendar.getScrollDate();
            LocalDate updated = scrollDate.withDayOfMonth(day);
            appCalendar.setScrollDate(updated);
            appCalendar.setScrollMonth(updated.getMonthValue());
            calRepository.save(appCalendar);
        }
        return "redirect:/";
    }

    @GetMapping("/change-month/{isNext}")
    public String changeMonth(@PathVariable Boolean isNext) {
        Optional<Calendar> calendar = calRepository.findById(Long.valueOf(1));
        if (calendar.isPresent()) {
            Calendar appCalendar = calendar.get();
            LocalDate scrollDate = appCalendar.getScrollDate();
            if (isNext) {
                LocalDate updated = scrollDate.plusMonths(1);
                appCalendar.setScrollDate(updated);
                appCalendar.setScrollMonth(updated.getMonthValue());
            } else {
                LocalDate updated = scrollDate.minusMonths(1);
                appCalendar.setScrollDate(updated);
                appCalendar.setScrollMonth(updated.getMonthValue());
            }

            calRepository.save(appCalendar);
        }
        return "redirect:/";
    }

    @GetMapping("/change-month-spec/{month}")
    public String changeMonthSpec(@PathVariable Integer month) {
        Optional<Calendar> calendar = calRepository.findById(Long.valueOf(1));
        if (calendar.isPresent()) {
            Calendar appCalendar = calendar.get();
            LocalDate scrollDate = appCalendar.getScrollDate();
            LocalDate updated = scrollDate.withMonth(month);
            appCalendar.setScrollDate(updated);
            appCalendar.setScrollMonth(updated.getMonthValue());

            calRepository.save(appCalendar);
        }
        return "redirect:/";
    }

    @GetMapping("/toggle-bar")
    public String changeBar() {
        Optional<Calendar> calendar = calRepository.findById(Long.valueOf(1));
        if (calendar.isPresent()) {
            Calendar appCalendar = calendar.get();
            appCalendar.setShowLeftAndRightBar(!appCalendar.getShowLeftAndRightBar());
            calRepository.save(appCalendar);
        }
        return "redirect:/";
    }

}
