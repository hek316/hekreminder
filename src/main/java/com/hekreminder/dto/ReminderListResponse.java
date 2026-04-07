package com.hekreminder.dto;

import com.hekreminder.domain.ReminderList;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReminderListResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final String icon;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private ReminderListResponse(ReminderList list) {
        this.id = list.getId();
        this.name = list.getName();
        this.color = list.getColor();
        this.icon = list.getIcon();
        this.createdAt = list.getCreatedAt();
        this.updatedAt = list.getUpdatedAt();
    }

    public static ReminderListResponse from(ReminderList list) {
        return new ReminderListResponse(list);
    }
}
