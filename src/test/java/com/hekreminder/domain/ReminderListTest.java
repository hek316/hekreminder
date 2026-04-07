package com.hekreminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderListTest {

    @Test
    @DisplayName("생성자 호출 시 name, color, icon이 저장된다")
    void constructor_sets_fields() {
        ReminderList list = new ReminderList("개인", "#007AFF", "🏠");

        assertThat(list.getName()).isEqualTo("개인");
        assertThat(list.getColor()).isEqualTo("#007AFF");
        assertThat(list.getIcon()).isEqualTo("🏠");
    }

    @Test
    @DisplayName("생성자 호출 시 createdAt, updatedAt이 현재 시각으로 설정된다")
    void constructor_sets_createdAt_and_updatedAt() {
        LocalDateTime before = LocalDateTime.now();

        ReminderList list = new ReminderList("업무", "#FF3B30", "💼");

        LocalDateTime after = LocalDateTime.now();
        assertThat(list.getCreatedAt()).isBetween(before, after);
        assertThat(list.getUpdatedAt()).isBetween(before, after);
    }

    @Test
    @DisplayName("생성 시 createdAt과 updatedAt은 같은 시각이다")
    void createdAt_equals_updatedAt_on_creation() {
        ReminderList list = new ReminderList("건강", "#34C759", "🏃");

        assertThat(list.getCreatedAt()).isEqualTo(list.getUpdatedAt());
    }

    @Test
    @DisplayName("color가 null이면 기본값 #007AFF가 설정된다")
    void null_color_falls_back_to_default() {
        ReminderList list = new ReminderList("기본", null, null);

        assertThat(list.getColor()).isEqualTo("#007AFF");
    }

    @Test
    @DisplayName("update() 호출 시 name, color, icon이 변경된다")
    void update_changes_fields() {
        ReminderList list = new ReminderList("개인", "#007AFF", "🏠");

        list.update("업무", "#FF3B30", "💼");

        assertThat(list.getName()).isEqualTo("업무");
        assertThat(list.getColor()).isEqualTo("#FF3B30");
        assertThat(list.getIcon()).isEqualTo("💼");
    }

    @Test
    @DisplayName("update() 호출 시 updatedAt이 갱신되고 createdAt은 불변이다")
    void update_refreshes_updatedAt_but_not_createdAt() throws InterruptedException {
        ReminderList list = new ReminderList("개인", "#007AFF", "🏠");
        LocalDateTime createdAt = list.getCreatedAt();
        LocalDateTime updatedAtBefore = list.getUpdatedAt();

        Thread.sleep(10);
        list.update("업무", "#FF3B30", "💼");

        assertThat(list.getCreatedAt()).isEqualTo(createdAt);
        assertThat(list.getUpdatedAt()).isAfter(updatedAtBefore);
    }
}
