package com.hekreminder.controller;

import com.hekreminder.dto.ReminderCountsResponse;
import com.hekreminder.dto.ReminderRequest;
import com.hekreminder.dto.ReminderResponse;
import com.hekreminder.repository.ReminderRepository;
import com.hekreminder.service.ports.inp.ReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;
    private final ReminderRepository reminderRepository;

    @GetMapping("/counts")
    public ReminderCountsResponse getCounts() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        return new ReminderCountsResponse(
                reminderRepository.countTodayReminders(start, start.plusDays(1)),
                reminderRepository.countScheduledReminders(LocalDateTime.now()),
                reminderRepository.countByCompletedFalse(),
                reminderRepository.countByFlaggedTrueAndCompletedFalse()
        );
    }

    @GetMapping
    public List<ReminderResponse> getAll(@RequestParam(required = false) String filter) {
        return reminderService.findAll(filter)
                .stream()
                .map(ReminderResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderResponse create(@Valid @RequestBody ReminderRequest request) {
        return ReminderResponse.from(reminderService.create(request));
    }

    @PutMapping("/{id}")
    public ReminderResponse update(@PathVariable Long id,
                                   @Valid @RequestBody ReminderRequest request) {
        return ReminderResponse.from(reminderService.update(id, request));
    }

    @PatchMapping("/{id}/complete")
    public ReminderResponse toggleComplete(@PathVariable Long id) {
        return ReminderResponse.from(reminderService.toggleComplete(id));
    }

    @PatchMapping("/{id}/flag")
    public ReminderResponse toggleFlag(@PathVariable Long id) {
        return ReminderResponse.from(reminderService.toggleFlag(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reminderService.delete(id);
    }
}
