package ru.datamatica.points.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.datamatica.points.service.TaskProcessor;
import ru.datamatica.points.service.TaskProcessorH3;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final TaskProcessorH3 taskProcessor;

    // TODO: 06.11.23 нужно использовать SchedLock
    @Scheduled(fixedDelay = 5000)
    public void processTasks() {
        boolean tasksWereProcessed;
        do {
            tasksWereProcessed = taskProcessor.processTasks();
        } while (tasksWereProcessed);
    }
}
