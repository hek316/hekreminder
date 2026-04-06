package com.hekreminder.service;

import com.hekreminder.domain.Reminder;
import com.hekreminder.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public List<Reminder> findAll() {
        return reminderRepository.findAll();
    }

    public Reminder findById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reminder not found: " + id));
    }

    @Transactional
    public Reminder create(Reminder reminder) {
        return reminderRepository.save(reminder);
    }

    @Transactional
    public Reminder update(Long id, Reminder updated) {
        Reminder reminder = findById(id);
        reminder.setTitle(updated.getTitle());
        reminder.setDescription(updated.getDescription());
        reminder.setDueDate(updated.getDueDate());
        reminder.setCompleted(updated.isCompleted());
        return reminder;
    }

    @Transactional
    public void delete(Long id) {
        reminderRepository.deleteById(id);
    }
}
