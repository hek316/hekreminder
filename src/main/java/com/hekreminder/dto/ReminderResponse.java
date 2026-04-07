package com.hekreminder.dto;

import com.hekreminder.domain.Priority;
import com.hekreminder.domain.Reminder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReminderResponse {

    private final Long id;
    private final String title;
    private final String notes;
    private final LocalDateTime dueDate;
    private final boolean completed;
    private final Priority priority;
    private final boolean flagged;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private ReminderResponse(Reminder reminder) {
        this.id = reminder.getId();
        this.title = reminder.getTitle();
        this.notes = reminder.getNotes();
        this.dueDate = reminder.getDueDate();
        this.completed = reminder.isCompleted();
        this.priority = reminder.getPriority();
        this.flagged = reminder.isFlagged();
        this.createdAt = reminder.getCreatedAt();
        this.updatedAt = reminder.getUpdatedAt();
    }

    public static ReminderResponse from(Reminder reminder) {
        return new ReminderResponse(reminder);
    }
}
