package com.hekreminder.repository;

import com.hekreminder.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    // filter=today: 오늘 마감, 미완료
    @Query("SELECT r FROM Reminder r WHERE r.dueDate >= :start AND r.dueDate < :end AND r.completed = false")
    List<Reminder> findTodayReminders(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // filter=scheduled: 미래 마감, 미완료
    @Query("SELECT r FROM Reminder r WHERE r.dueDate >= :from AND r.completed = false")
    List<Reminder> findScheduledReminders(@Param("from") LocalDateTime from);

    // filter=flagged: 깃발, 미완료
    List<Reminder> findByFlaggedTrueAndCompletedFalse();

    // filter=completed: 완료됨
    List<Reminder> findByCompletedTrue();

    // 기본: 미완료 전체
    List<Reminder> findByCompletedFalse();

    // counts
    @Query("SELECT COUNT(r) FROM Reminder r WHERE r.dueDate >= :start AND r.dueDate < :end AND r.completed = false")
    long countTodayReminders(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(r) FROM Reminder r WHERE r.dueDate >= :from AND r.completed = false")
    long countScheduledReminders(@Param("from") LocalDateTime from);

    long countByFlaggedTrueAndCompletedFalse();

    long countByCompletedFalse();
}
