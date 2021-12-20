package com.printtex.printtex_pos.scheduler;

import com.printtex.printtex_pos.repository.EventLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class EventLogDeleteScheduler {
    private final EventLogRepository eventLogRepository;

    public EventLogDeleteScheduler(EventLogRepository eventLogRepository) {
        this.eventLogRepository = eventLogRepository;
    }

    @Scheduled(cron = "0 0 12 3 * *", zone = "Asia/Dhaka")
//    @Scheduled(cron = "*/10 * * * * *")
    public void deleteEventLog() {
        eventLogRepository.deleteAll();
    }
}
