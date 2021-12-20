package com.printtex.printtex_pos.repository;

import com.printtex.printtex_pos.model.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {
    List<EventLog> findAll();

    void deleteAll();
}
