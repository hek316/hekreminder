package com.hekreminder.service;

import com.hekreminder.domain.Reminder;
import com.hekreminder.dto.ReminderRequest;
import com.hekreminder.repository.ReminderRepository;
import com.hekreminder.service.ports.inp.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderService implements ReminderService {

    private final ReminderRepository reminderRepository;

    @Override
    public List<Reminder> findAll(String filter) {
        if (filter == null) return reminderRepository.findByCompletedFalse();
        return switch (filter) {
            case "today" -> {
                LocalDateTime start = LocalDate.now().atStartOfDay();
                LocalDateTime end = start.plusDays(1);
                yield reminderRepository.findTodayReminders(start, end);
            }
            case "scheduled" -> reminderRepository.findScheduledReminders(LocalDateTime.now());
            case "flagged"   -> reminderRepository.findByFlaggedTrueAndCompletedFalse();
            case "completed" -> reminderRepository.findByCompletedTrue();
            default          -> reminderRepository.findByCompletedFalse();
        };
    }

    @Override
    public Reminder findById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found: " + id));
    }

    @Override
    @Transactional
    public Reminder create(ReminderRequest request) {
        return reminderRepository.save(new Reminder(
                request.getTitle(),
                request.getNotes(),
                request.getDueDate(),
                request.getPriority(),
                Boolean.TRUE.equals(request.getFlagged())
        ));
    }

    @Override
    @Transactional
    public Reminder update(Long id, ReminderRequest request) {
        Reminder reminder = findById(id);
        reminder.update(
                request.getTitle(),
                request.getNotes(),
                request.getDueDate(),
                request.getPriority(),
                Boolean.TRUE.equals(request.getFlagged())
        );
        return reminder;
    }

    @Override
    @Transactional
    public Reminder toggleComplete(Long id) {
        Reminder reminder = findById(id);
        reminder.toggleComplete();
        return reminder;
    }

    @Override
    @Transactional
    public Reminder toggleFlag(Long id) {
        Reminder reminder = findById(id);
        reminder.toggleFlag();
        return reminder;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!reminderRepository.existsById(id)) {
            throw new IllegalArgumentException("Reminder not found: " + id);
        }
        reminderRepository.deleteById(id);
    }
}
