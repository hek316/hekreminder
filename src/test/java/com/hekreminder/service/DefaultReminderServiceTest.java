package com.hekreminder.service;

import com.hekreminder.domain.Priority;
import com.hekreminder.domain.Reminder;
import com.hekreminder.dto.ReminderRequest;
import com.hekreminder.exception.ReminderNotFoundException;
import com.hekreminder.repository.ReminderRepository;
import com.hekreminder.service.ports.inp.ReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class DefaultReminderServiceTest {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private ReminderRepository reminderRepository;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
    }

    @Test
    @DisplayName("create 후 findAll - 생성된 리마인더가 조회된다")
    void create_then_findAll() {
        reminderService.create(req("마트 다녀오기", null, null, Priority.NONE, false));
        reminderService.create(req("독서", null, null, Priority.LOW, false));

        List<Reminder> result = reminderService.findAll(null);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("filter=today - 오늘 마감 미완료 리마인더만 반환한다")
    void findAll_filter_today() {
        LocalDateTime today = LocalDate.now().atTime(12, 0);
        LocalDateTime tomorrow = today.plusDays(1);

        reminderService.create(req("오늘 할 일", null, today, Priority.NONE, false));
        reminderService.create(req("내일 할 일", null, tomorrow, Priority.NONE, false));

        List<Reminder> result = reminderService.findAll("today");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("오늘 할 일");
    }

    @Test
    @DisplayName("filter=flagged - 깃발 미완료 리마인더만 반환한다")
    void findAll_filter_flagged() {
        reminderService.create(req("깃발", null, null, Priority.NONE, true));
        reminderService.create(req("일반", null, null, Priority.NONE, false));

        List<Reminder> result = reminderService.findAll("flagged");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("깃발");
    }

    @Test
    @DisplayName("filter=completed - 완료된 리마인더만 반환한다")
    void findAll_filter_completed() {
        Reminder r = reminderService.create(req("할 일", null, null, Priority.NONE, false));
        reminderService.toggleComplete(r.getId());

        List<Reminder> result = reminderService.findAll("completed");
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("update - 필드가 변경된다")
    void update_changes_fields() {
        Reminder created = reminderService.create(req("제목", null, null, Priority.NONE, false));

        Reminder updated = reminderService.update(created.getId(),
                req("수정된 제목", "메모", null, Priority.HIGH, true));

        assertThat(updated.getTitle()).isEqualTo("수정된 제목");
        assertThat(updated.getNotes()).isEqualTo("메모");
        assertThat(updated.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(updated.isFlagged()).isTrue();
    }

    @Test
    @DisplayName("toggleComplete - 완료 상태가 반전된다")
    void toggleComplete_flips_state() {
        Reminder created = reminderService.create(req("제목", null, null, Priority.NONE, false));
        assertThat(created.isCompleted()).isFalse();

        Reminder toggled = reminderService.toggleComplete(created.getId());
        assertThat(toggled.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("toggleFlag - 깃발 상태가 반전된다")
    void toggleFlag_flips_state() {
        Reminder created = reminderService.create(req("제목", null, null, Priority.NONE, false));
        assertThat(created.isFlagged()).isFalse();

        Reminder toggled = reminderService.toggleFlag(created.getId());
        assertThat(toggled.isFlagged()).isTrue();
    }

    @Test
    @DisplayName("delete 후 findById가 예외를 던진다")
    void delete_then_findById_throws() {
        Reminder created = reminderService.create(req("제목", null, null, Priority.NONE, false));
        reminderService.delete(created.getId());

        assertThatThrownBy(() -> reminderService.findById(created.getId()))
                .isInstanceOf(ReminderNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 id면 예외를 던진다")
    void findById_throws_when_not_found() {
        assertThatThrownBy(() -> reminderService.findById(999L))
                .isInstanceOf(ReminderNotFoundException.class)
                .hasMessageContaining("999");
    }

    private ReminderRequest req(String title, String notes, LocalDateTime dueDate,
                                Priority priority, boolean flagged) {
        return new ReminderRequest(title, notes, dueDate, priority, flagged);
    }
}
