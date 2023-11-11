package ru.datamatica.points.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.datamatica.points.repository.ClusterRepository;
import ru.datamatica.points.repository.TasksQueueRepository;
import ru.datamatica.points.repository.model.TaskQueueModel;
import ru.datamatica.points.service.common.Zoom;
import ru.datamatica.points.util.TimeUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskProcessor {
    private final TasksQueueRepository tasksQueueRepository;
    private final ClusterRepository clusterRepository;
    private final TimeUtil timeUtil;

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

        if (tasks.isEmpty()) {
            log.info("Нет заданий для обработки");
            return false;
        }
        doProcessTasks(tasks);
        tasksQueueRepository.setProcessedNow(tasks);
        log.info("Задания обработаны");

        return true;
    }

    private void doProcessTasks(List<TaskQueueModel> tasks) {
//        tasks.forEach(
//                task -> {
//                    List<ClusterRepository.CLusterCentroidAndPoints> updst= new LinkedList<>();
//                    for (Zoom zoom : Zoom.values()) {
//                        var geoHash = task.getGeoHash().substring(0, zoom.getPrecision());
//                        var upd = ClusterRepository.CLusterCentroidAndPoints.builder()
//                                .longitude(task.getLongitude())
//                                .latitude(task.getLatitude())
//                                .geoHash(geoHash)
//                                .pointsQty(1)
//                                .build();
//                        updst.add(upd);
//                    }
//                    clusterRepository.upsertCluster(updst);
//                }
//        );

//        tasks.forEach(
//                task -> {
//                        var upd = ClusterRepository.CLusterCentroidAndPoints.builder()
//                                .longitude(task.getLongitude())
//                                .latitude(task.getLatitude())
//                                .geoHash(task.getGeoHash())
//                                .pointsQty(1)
//                                .build();
//                    clusterRepository.upsertClusterOne(upd);
//                }
//        );

//        geoHashToTasks.forEach((key, value) -> clusterRepository.upsertCluster(List.of(calculateCentroid(value, key))));

        var geoHashToTasks = tasks.stream()
                .collect(Collectors.groupingBy(TaskQueueModel::getGeoHash));

        geoHashToTasks.forEach((key, value) -> clusterRepository.upsertCluster(List.of(calculateCentroid(value, key))));

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
