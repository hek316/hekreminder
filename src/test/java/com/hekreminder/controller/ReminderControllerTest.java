package com.hekreminder.controller;

import com.hekreminder.domain.Priority;
import com.hekreminder.dto.ReminderRequest;
import com.hekreminder.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ReminderControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReminderRepository reminderRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        reminderRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/reminders - 빈 목록을 반환한다")
    void getAll_returns_empty() throws Exception {
        mockMvc.perform(get("/api/reminders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/reminders - 리마인더를 생성하고 201을 반환한다")
    void create_returns_201() throws Exception {
        mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req("마트 다녀오기", "우유, 계란", null, Priority.HIGH, true))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("마트 다녀오기"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.flagged").value(true))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    @DisplayName("POST /api/reminders - title이 없으면 400과 필드 에러를 반환한다")
    void create_returns_400_with_field_errors_when_title_blank() throws Exception {
        mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req("", null, null, null, false))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("title"))
                .andExpect(jsonPath("$.errors[0].message").isNotEmpty());
    }

    @Test
    @DisplayName("GET /api/reminders?filter=flagged - 깃발 리마인더만 반환한다")
    void getAll_filter_flagged() throws Exception {
        mockMvc.perform(post("/api/reminders").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req("깃발", null, null, Priority.NONE, true))));
        mockMvc.perform(post("/api/reminders").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req("일반", null, null, Priority.NONE, false))));

        mockMvc.perform(get("/api/reminders?filter=flagged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("깃발"));
    }

    @Test
    @DisplayName("GET /api/reminders?filter=today - 오늘 마감 리마인더만 반환한다")
    void getAll_filter_today() throws Exception {
        LocalDateTime today = LocalDate.now().atTime(12, 0);
        LocalDateTime tomorrow = today.plusDays(1);

        mockMvc.perform(post("/api/reminders").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req("오늘", null, today, Priority.NONE, false))));
        mockMvc.perform(post("/api/reminders").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req("내일", null, tomorrow, Priority.NONE, false))));

        mockMvc.perform(get("/api/reminders?filter=today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("오늘"));
    }

    @Test
    @DisplayName("PUT /api/reminders/{id} - 수정된 값을 반환한다")
    void update_returns_updated() throws Exception {
        String body = mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req("제목", null, null, Priority.NONE, false))))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(put("/api/reminders/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req("수정된 제목", "메모", null, Priority.HIGH, true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @DisplayName("PATCH /api/reminders/{id}/complete - 완료 상태가 반전된다")
    void toggleComplete_flips_state() throws Exception {
        String body = mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req("제목", null, null, Priority.NONE, false))))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(patch("/api/reminders/" + id + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("PATCH /api/reminders/{id}/flag - 깃발 상태가 반전된다")
    void toggleFlag_flips_state() throws Exception {
        String body = mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req("제목", null, null, Priority.NONE, false))))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(patch("/api/reminders/" + id + "/flag"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flagged").value(true));
    }

    @Test
    @DisplayName("GET /api/reminders/{id} - 존재하지 않는 id는 404를 반환한다")
    void getById_returns_404_when_not_found() throws Exception {
        mockMvc.perform(get("/api/reminders/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/reminders/{id} - 존재하지 않는 id 수정 시 404를 반환한다")
    void update_returns_404_when_not_found() throws Exception {
        mockMvc.perform(put("/api/reminders/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req("제목", null, null, Priority.NONE, false))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/reminders/{id} - 존재하지 않는 id 삭제 시 404를 반환한다")
    void delete_returns_404_when_not_found() throws Exception {
        mockMvc.perform(delete("/api/reminders/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/reminders/{id} - 204를 반환하고 삭제된다")
    void delete_returns_204() throws Exception {
        String body = mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req("제목", null, null, Priority.NONE, false))))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(delete("/api/reminders/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/reminders"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/reminders/counts - 카운트를 반환한다")
    void getCounts_returns_counts() throws Exception {
        mockMvc.perform(post("/api/reminders").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req("깃발", null, null, Priority.NONE, true))));
        mockMvc.perform(post("/api/reminders").contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req("일반", null, null, Priority.NONE, false))));

        mockMvc.perform(get("/api/reminders/counts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.all").value(2))
                .andExpect(jsonPath("$.flagged").value(1));
    }

    private ReminderRequest req(String title, String notes, LocalDateTime dueDate,
                                Priority priority, boolean flagged) {
        return new ReminderRequest(title, notes, dueDate, priority, flagged);
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
