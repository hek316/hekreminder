package com.hekreminder.service.ports.inp;

import com.hekreminder.domain.Reminder;
import com.hekreminder.dto.ReminderCountsResponse;
import com.hekreminder.dto.ReminderRequest;

import java.util.List;

public interface ReminderService {

    List<Reminder> findAll(String filter);

    ReminderCountsResponse getCounts();

    Reminder findById(Long id);

    Reminder create(ReminderRequest request);

    Reminder update(Long id, ReminderRequest request);

    Reminder toggleComplete(Long id);

    Reminder toggleFlag(Long id);

    void delete(Long id);
}
