package com.hekreminder.service;

import com.hekreminder.domain.ReminderList;
import com.hekreminder.service.ports.inp.ReminderListService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ReminderListServiceTest {

    @Autowired
    private ReminderListService reminderListService;

    @Test
    @DisplayName("create 후 findAll - 저장된 목록이 조회된다")
    void create_then_findAll() {
        reminderListService.create("개인", "#007AFF", "🏠");
        reminderListService.create("업무", "#FF3B30", "💼");

        List<ReminderList> result = reminderListService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("create 후 findById - 저장된 목록이 id로 조회된다")
    void create_then_findById() {
        ReminderList saved = reminderListService.create("건강", "#34C759", "🏃");

        ReminderList found = reminderListService.findById(saved.getId());

        assertThat(found.getName()).isEqualTo("건강");
        assertThat(found.getColor()).isEqualTo("#34C759");
        assertThat(found.getIcon()).isEqualTo("🏃");
    }

    @Test
    @DisplayName("findById - 존재하지 않는 id면 예외를 던진다")
    void findById_throws_when_not_found() {
        assertThatThrownBy(() -> reminderListService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("create - color가 null이면 기본값 #007AFF로 저장된다")
    void create_with_null_color_uses_default() {
        ReminderList saved = reminderListService.create("기본", null, null);

        assertThat(saved.getColor()).isEqualTo("#007AFF");
    }

    @Test
    @DisplayName("create - createdAt, updatedAt이 자동 설정된다")
    void create_sets_timestamps() {
        ReminderList saved = reminderListService.create("개인", "#007AFF", "🏠");

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("update - name, color, icon이 변경된다")
    void update_changes_fields() throws InterruptedException {
        ReminderList saved = reminderListService.create("개인", "#007AFF", "🏠");
        Thread.sleep(10);

        ReminderList updated = reminderListService.update(saved.getId(), "업무", "#FF3B30", "💼");

        assertThat(updated.getName()).isEqualTo("업무");
        assertThat(updated.getColor()).isEqualTo("#FF3B30");
        assertThat(updated.getIcon()).isEqualTo("💼");
        assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
    }

    @Test
    @DisplayName("update - 존재하지 않는 id면 예외를 던진다")
    void update_throws_when_not_found() {
        assertThatThrownBy(() -> reminderListService.update(999L, "업무", "#FF3B30", "💼"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("delete - 삭제 후 findById가 예외를 던진다")
    void delete_then_findById_throws() {
        ReminderList saved = reminderListService.create("개인", "#007AFF", "🏠");

        reminderListService.delete(saved.getId());

        assertThatThrownBy(() -> reminderListService.findById(saved.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("delete - 존재하지 않는 id면 예외를 던진다")
    void delete_throws_when_not_found() {
        assertThatThrownBy(() -> reminderListService.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("999");
    }
}
