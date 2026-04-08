package com.hekreminder.controller;

import tools.jackson.databind.ObjectMapper;
import com.hekreminder.dto.ReminderListRequest;
import com.hekreminder.repository.ReminderListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ReminderListControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReminderListRepository reminderListRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        reminderListRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/lists - 빈 목록을 반환한다")
    void getAll_returns_empty_list() throws Exception {
        mockMvc.perform(get("/api/lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/lists - 목록을 생성하고 201을 반환한다")
    void create_returns_201_with_created_list() throws Exception {
        mockMvc.perform(post("/api/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new ReminderListRequest("개인", "#007AFF", "🏠"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("개인"))
                .andExpect(jsonPath("$.color").value("#007AFF"))
                .andExpect(jsonPath("$.icon").value("🏠"));
    }

    @Test
    @DisplayName("POST /api/lists - name이 없으면 400을 반환한다")
    void create_returns_400_when_name_is_blank() throws Exception {
        mockMvc.perform(post("/api/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new ReminderListRequest("", "#007AFF", null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/lists - color가 HEX 형식이 아니면 400을 반환한다")
    void create_returns_400_when_color_invalid() throws Exception {
        mockMvc.perform(post("/api/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new ReminderListRequest("개인", "notacolor", null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/lists - 생성된 목록이 조회된다")
    void getAll_returns_created_lists() throws Exception {
        mockMvc.perform(post("/api/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new ReminderListRequest("개인", "#007AFF", "🏠"))));
        mockMvc.perform(post("/api/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(new ReminderListRequest("업무", "#FF3B30", "💼"))));

        mockMvc.perform(get("/api/lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("PUT /api/lists/{id} - 목록을 수정하고 수정된 값을 반환한다")
    void update_returns_updated_list() throws Exception {
        String body = mockMvc.perform(post("/api/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new ReminderListRequest("개인", "#007AFF", "🏠"))))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(put("/api/lists/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new ReminderListRequest("업무", "#FF3B30", "💼"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("업무"))
                .andExpect(jsonPath("$.color").value("#FF3B30"));
    }

    @Test
    @DisplayName("PUT /api/lists/{id} - 존재하지 않는 id면 404를 반환한다")
    void update_returns_404_when_not_found() throws Exception {
        mockMvc.perform(put("/api/lists/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new ReminderListRequest("업무", "#FF3B30", "💼"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/lists/{id} - 목록을 삭제하고 204를 반환한다")
    void delete_returns_204() throws Exception {
        String body = mockMvc.perform(post("/api/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(new ReminderListRequest("개인", "#007AFF", "🏠"))))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(delete("/api/lists/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/lists"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("DELETE /api/lists/{id} - 존재하지 않는 id면 404를 반환한다")
    void delete_returns_404_when_not_found() throws Exception {
        mockMvc.perform(delete("/api/lists/999"))
                .andExpect(status().isNotFound());
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
