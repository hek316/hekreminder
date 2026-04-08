package com.hekreminder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReminderListRequest {

    @NotBlank
    private String name;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "color must be a valid HEX code")
    private String color;

    private String icon;
}
