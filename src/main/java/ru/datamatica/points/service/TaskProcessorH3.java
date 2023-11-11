package ru.datamatica.points.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.datamatica.points.repository.ClusterRepository;
import ru.datamatica.points.repository.TasksQueueRepository;
import ru.datamatica.points.repository.model.TaskQueueModel;
import ru.datamatica.points.service.common.Zoom;
import ru.datamatica.points.util.GeoUtil;
import ru.datamatica.points.util.TimeUtil;

import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskProcessorH3 {
    private final TasksQueueRepository tasksQueueRepository;
    private final ClusterRepository clusterRepository;
    private final TimeUtil timeUtil;
    private final GeoUtil geoUtil;


    @Transactional
    public boolean processTasks() {
        var lastCreated = tasksQueueRepository.findLastCreatedAt();
        if (lastCreated == null) {
            log.info("Нет заданий для обработки");
            return false;
        }

        if (lastCreated.plusSeconds(10).isAfter(timeUtil.now())) {
            log.info("Не прошло 10 секунд с момента создания последнего задания");
            return false;
        }

        log.info("Запускаем задание по обработке задач");
        var tasks = tasksQueueRepository.findAllNotProcessedSkipLocked(2000);
        log.info("Найдено {} заданий для обработки", tasks.size());

        doProcessTasks(tasks);
        tasksQueueRepository.setProcessedNow(tasks);
        log.info("Задания обработаны");

        return true;
    }

    private void doProcessTasks(List<TaskQueueModel> tasks) {
        List<ClusterRepository.H3Cluster> tskkk = new LinkedList<>();
        tasks.forEach(taskQueueModel -> {
            for (int i = Zoom.MIN_ZOOM.getPrecision(); i <= Zoom.MAX_ZOOM.getPrecision(); i++) {

                var hash = geoUtil.calculateGeoHash(taskQueueModel.getLatitude(), taskQueueModel.getLongitude(), i);
                var centroid = geoUtil.getCentroidH3(hash);
                var tsk = ClusterRepository.H3Cluster.builder()
                        .geoHash(hash)
                        .latitude(centroid.lat)
                        .longitude(centroid.lng)
                        .pointsQty(1)
                        .precision(i)
                        .build();
                tskkk.add(tsk);
            }
        });
        clusterRepository.upserth3(tskkk);
    }

    private ClusterRepository.CLusterCentroidAndPoints calculateCentroid(List<TaskQueueModel> tasks, String geoHash) {
        double totalLongitude = 0.0;
        double totalLatitude = 0.0;
        int count = 0;

        if (tasks.isEmpty()) {
            return ClusterRepository.CLusterCentroidAndPoints.builder()
                    .geoHash(geoHash)
                    .longitude(0.0)
                    .latitude(0.0)
                    .build();
        }

        for (TaskQueueModel task : tasks) {
            totalLongitude += task.getLongitude();
            totalLatitude += task.getLatitude();
            count++;
        }

        double centroidLongitude = totalLongitude / count;
        double centroidLatitude = totalLatitude / count;

        return ClusterRepository.CLusterCentroidAndPoints.builder()
                .geoHash(geoHash)
                .longitude(centroidLongitude)
                .latitude(centroidLatitude)
                .pointsQty(count)
                .build();
    }

}
