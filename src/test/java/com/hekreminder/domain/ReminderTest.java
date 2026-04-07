package com.hekreminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderTest {

    @Test
    @DisplayName("생성자 호출 시 필드가 올바르게 설정된다")
    void constructor_sets_fields() {
        LocalDateTime due = LocalDateTime.of(2026, 5, 1, 9, 0);
        Reminder reminder = new Reminder("마트 다녀오기", "우유, 계란", due, Priority.HIGH, true);

        assertThat(reminder.getTitle()).isEqualTo("마트 다녀오기");
        assertThat(reminder.getNotes()).isEqualTo("우유, 계란");
        assertThat(reminder.getDueDate()).isEqualTo(due);
        assertThat(reminder.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(reminder.isFlagged()).isTrue();
        assertThat(reminder.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("생성자 호출 시 createdAt, updatedAt이 현재 시각으로 설정된다")
    void constructor_sets_timestamps() {
        LocalDateTime before = LocalDateTime.now();
        Reminder reminder = new Reminder("제목", null, null, null, false);
        LocalDateTime after = LocalDateTime.now();

        assertThat(reminder.getCreatedAt()).isBetween(before, after);
        assertThat(reminder.getUpdatedAt()).isBetween(before, after);
        assertThat(reminder.getCreatedAt()).isEqualTo(reminder.getUpdatedAt());
    }

    @Test
    @DisplayName("priority가 null이면 기본값 NONE이 설정된다")
    void null_priority_defaults_to_none() {
        Reminder reminder = new Reminder("제목", null, null, null, false);

        assertThat(reminder.getPriority()).isEqualTo(Priority.NONE);
    }

    @Test
    @DisplayName("update() 호출 시 필드가 변경되고 updatedAt이 갱신된다")
    void update_changes_fields_and_refreshes_updatedAt() throws InterruptedException {
        Reminder reminder = new Reminder("제목", null, null, Priority.NONE, false);
        LocalDateTime createdAt = reminder.getCreatedAt();
        Thread.sleep(10);

        reminder.update("수정된 제목", "메모", null, Priority.MEDIUM, true);

        assertThat(reminder.getTitle()).isEqualTo("수정된 제목");
        assertThat(reminder.getNotes()).isEqualTo("메모");
        assertThat(reminder.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(reminder.isFlagged()).isTrue();
        assertThat(reminder.getCreatedAt()).isEqualTo(createdAt);
        assertThat(reminder.getUpdatedAt()).isAfter(createdAt);
    }

    @Test
    @DisplayName("toggleComplete() 호출 시 완료 상태가 반전된다")
    void toggleComplete_flips_completed() {
        Reminder reminder = new Reminder("제목", null, null, null, false);
        assertThat(reminder.isCompleted()).isFalse();

        reminder.toggleComplete();
        assertThat(reminder.isCompleted()).isTrue();

        reminder.toggleComplete();
        assertThat(reminder.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("toggleFlag() 호출 시 깃발 상태가 반전된다")
    void toggleFlag_flips_flagged() {
        Reminder reminder = new Reminder("제목", null, null, null, false);
        assertThat(reminder.isFlagged()).isFalse();

        reminder.toggleFlag();
        assertThat(reminder.isFlagged()).isTrue();

        reminder.toggleFlag();
        assertThat(reminder.isFlagged()).isFalse();
    }
}
