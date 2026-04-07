package com.hekreminder.dto;

import com.hekreminder.domain.Priority;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderRequest {

    @NotBlank
    private String title;

    private String notes;

    private LocalDateTime dueDate;

    private Priority priority;

    private boolean flagged;
}
