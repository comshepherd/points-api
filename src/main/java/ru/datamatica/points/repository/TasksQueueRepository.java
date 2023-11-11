package ru.datamatica.points.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;
import ru.datamatica.points.generated.jooq.tables.records.TasksQueueRecord;
import ru.datamatica.points.repository.model.TaskQueueModel;
import ru.datamatica.points.util.TimeUtil;

import java.time.LocalDateTime;
import java.util.List;

import static ru.datamatica.points.generated.jooq.tables.TasksQueue.TASKS_QUEUE;

@Repository
@RequiredArgsConstructor
public class TasksQueueRepository {

    private final DSLContext dsl;
    private final TimeUtil timeUtil;

    public int[] insert(List<TaskQueueModel> models) {
        var batch = models.stream()
                .map(task -> dsl.insertInto(TASKS_QUEUE)
                        .set(toRecord(task)))
                .toList();

        return dsl.batch(batch).execute();
    }

    public int setProcessedNow(List<TaskQueueModel> model) {
        var ids = model.stream()
                .map(TaskQueueModel::getId)
                .toList();

        return dsl.update(TASKS_QUEUE)
                .set(TASKS_QUEUE.PROCESSED, true)
                .set(TASKS_QUEUE.PROCESSED_AT, timeUtil.now())
                .where(TASKS_QUEUE.ID.in(ids))
                .execute();
    }

    public LocalDateTime findLastCreatedAt() {
        return dsl.select(TASKS_QUEUE.CREATED_AT)
                .from(TASKS_QUEUE)
                .where(TASKS_QUEUE.PROCESSED.eq(false))
                .orderBy(TASKS_QUEUE.CREATED_AT.desc())
                .limit(1)
                .fetchOneInto(LocalDateTime.class);
    }

    public List<TaskQueueModel> findAllNotProcessedSkipLocked(int limit) {
        return dsl.selectFrom(TASKS_QUEUE)
                .where(TASKS_QUEUE.PROCESSED.eq(false))
                .orderBy(TASKS_QUEUE.GEO_HASH)
                .limit(limit)
                .forUpdate().skipLocked()
                .fetchInto(TaskQueueModel.class);
    }

    private Record toRecord(TaskQueueModel model) {
        var record = new TasksQueueRecord();
        record.setId(model.getId());
        record.setLatitude(model.getLatitude());
        record.setLongitude(model.getLongitude());
        record.setGeoHash(model.getGeoHash());
        record.setProcessed(model.isProcessed());
        record.setCreatedAt(model.getCreatedAt());
        record.setProcessedAt(model.getProcessedAt());
        return record;
    }


}
