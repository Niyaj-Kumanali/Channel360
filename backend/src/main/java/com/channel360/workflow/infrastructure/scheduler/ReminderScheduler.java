package com.channel360.workflow.infrastructure.scheduler;

import com.channel360.workflow.domain.entity.WorkflowTask;
import com.channel360.workflow.domain.enums.TaskStatus;
import com.channel360.workflow.infrastructure.persistence.repository.WorkflowTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final WorkflowTaskRepository taskRepository;

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyReminders() {
        List<WorkflowTask> pendingTasks = taskRepository.findByStatus(TaskStatus.PENDING);
        log.info("Found {} pending tasks for daily reminder", pendingTasks.size());

        for (WorkflowTask task : pendingTasks) {
            try {
                log.info("Reminder: Task {} is pending for request {}, assigned to user {}",
                    task.getId(), task.getRequest().getId(), task.getAssignedUserId());
            } catch (Exception e) {
                log.error("Failed to send reminder for task {}", task.getId(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 14 * * MON-FRI")
    public void sendAfternoonReminders() {
        List<WorkflowTask> pendingTasks = taskRepository.findByStatus(TaskStatus.PENDING);
        for (WorkflowTask task : pendingTasks) {
            try {
                log.debug("Afternoon reminder: Task {} is still pending", task.getId());
            } catch (Exception e) {
                log.error("Failed to send afternoon reminder for task {}", task.getId(), e);
            }
        }
    }
}
