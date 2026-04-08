package com.hekreminder.dto;

public record ReminderCountsResponse(long today, long scheduled, long all, long flagged) {
}
