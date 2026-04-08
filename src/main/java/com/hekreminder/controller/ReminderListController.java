package com.hekreminder.controller;

import com.hekreminder.dto.ReminderListRequest;
import com.hekreminder.dto.ReminderListResponse;
import com.hekreminder.service.ports.inp.ReminderListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ReminderListController {

    private final ReminderListService reminderListService;

    @GetMapping
    public List<ReminderListResponse> getAll() {
        return reminderListService.findAll()
                .stream()
                .map(ReminderListResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderListResponse create(@Valid @RequestBody ReminderListRequest request) {
        return ReminderListResponse.from(
                reminderListService.create(request.getName(), request.getColor(), request.getIcon())
        );
    }

    @PutMapping("/{id}")
    public ReminderListResponse update(@PathVariable Long id,
                                       @Valid @RequestBody ReminderListRequest request) {
        return ReminderListResponse.from(
                reminderListService.update(id, request.getName(), request.getColor(), request.getIcon())
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reminderListService.delete(id);
    }
}
