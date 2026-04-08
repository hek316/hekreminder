package com.hekreminder.service;

import com.hekreminder.domain.ReminderList;
import com.hekreminder.exception.ReminderListNotFoundException;
import com.hekreminder.service.ports.inp.ReminderListService;
import com.hekreminder.repository.ReminderListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderListService implements ReminderListService {

    private final ReminderListRepository reminderListRepository;

    @Override
    public List<ReminderList> findAll() {
        return reminderListRepository.findAll();
    }

    @Override
    public ReminderList findById(Long id) {
        return reminderListRepository.findById(id)
                .orElseThrow(() -> new ReminderListNotFoundException(id));
    }

    @Override
    @Transactional
    public ReminderList create(String name, String color, String icon) {
        return reminderListRepository.save(new ReminderList(name, color, icon));
    }

    @Override
    @Transactional
    public ReminderList update(Long id, String name, String color, String icon) {
        ReminderList list = findById(id);
        list.update(name, color, icon);
        return list;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!reminderListRepository.existsById(id)) {
            throw new ReminderListNotFoundException(id);
        }
        reminderListRepository.deleteById(id);
    }
}
