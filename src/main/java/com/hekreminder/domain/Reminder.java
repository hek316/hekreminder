package com.hekreminder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
@Getter
@NoArgsConstructor
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String notes;

    private LocalDateTime dueDate;

    @Column(nullable = false)
    private boolean completed = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.NONE;

    @Column(nullable = false)
    private boolean flagged = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Reminder(String title, String notes, LocalDateTime dueDate, Priority priority, boolean flagged) {
        this.title = title;
        this.notes = notes;
        this.dueDate = dueDate;
        this.priority = priority != null ? priority : Priority.NONE;
        this.flagged = flagged;
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void update(String title, String notes, LocalDateTime dueDate, Priority priority, boolean flagged) {
        this.title = title;
        this.notes = notes;
        this.dueDate = dueDate;
        this.priority = priority != null ? priority : Priority.NONE;
        this.flagged = flagged;
        this.updatedAt = LocalDateTime.now();
    }

    public void toggleComplete() {
        this.completed = !this.completed;
        this.updatedAt = LocalDateTime.now();
    }

    public void toggleFlag() {
        this.flagged = !this.flagged;
        this.updatedAt = LocalDateTime.now();
    }
}
