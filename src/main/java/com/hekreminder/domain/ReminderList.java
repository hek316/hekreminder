package com.hekreminder.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reminder_lists")
@Getter
@NoArgsConstructor
public class ReminderList extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    private String icon;

    public ReminderList(String name, String color, String icon) {
        this.name = name;
        this.color = color != null ? color : "#007AFF";
        this.icon = icon;
        initTimestamps();
    }

    public void update(String name, String color, String icon) {
        this.name = name;
        this.color = color != null ? color : "#007AFF";
        this.icon = icon;
        touchUpdatedAt();
    }
}
