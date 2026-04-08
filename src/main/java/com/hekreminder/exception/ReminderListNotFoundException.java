package com.hekreminder.exception;

public class ReminderListNotFoundException extends RuntimeException {
    public ReminderListNotFoundException(Long id) {
        super("ReminderList not found: " + id);
    }
}
