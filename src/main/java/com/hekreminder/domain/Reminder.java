package com.hekreminder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
@Getter
@NoArgsConstructor
public class Reminder extends BaseEntity {

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

    public Reminder(String title, String notes, LocalDateTime dueDate, Priority priority, boolean flagged) {
        this.title = title;
        this.notes = notes;
        this.dueDate = dueDate;
        this.priority = priority != null ? priority : Priority.NONE;
        this.flagged = flagged;
        initTimestamps();
    }

    public void update(String title, String notes, LocalDateTime dueDate, Priority priority, boolean flagged) {
        this.title = title;
        this.notes = notes;
        this.dueDate = dueDate;
        this.priority = priority != null ? priority : Priority.NONE;
        this.flagged = flagged;
        touchUpdatedAt();
    }

    public void toggleComplete() {
        this.completed = !this.completed;
        touchUpdatedAt();
    }

    public void toggleFlag() {
        this.flagged = !this.flagged;
        touchUpdatedAt();
    }
}
