package ru.datamatica.points.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.datamatica.points.controller.dto.CreatePointsRequest;
import ru.datamatica.points.repository.PointRepository;
import ru.datamatica.points.repository.TasksQueueRepository;
import ru.datamatica.points.repository.model.PointModel;
import ru.datamatica.points.repository.model.TaskQueueModel;
import ru.datamatica.points.service.common.Zoom;
import ru.datamatica.points.util.GeoUtil;
import ru.datamatica.points.util.TimeUtil;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository deliveryPointRepository;
    private final TasksQueueRepository tasksQueueRepository;
    private final TimeUtil timeUtil;

    @Transactional
    public void save(CreatePointsRequest req) {
        var pointModels = CollectionUtils.emptyIfNull(req.getPoints()).stream()
                .map(this::toDeliveryPoint)
                .toList();
        deliveryPointRepository.insert(pointModels);
//        pointModels.forEach(this::saveTasks);

        var tasks = pointModels.stream()
                .map(this::toTask)
                .toList();

        tasksQueueRepository.insert(tasks);
    }

    private TaskQueueModel toTask(PointModel pointModel) {
        return TaskQueueModel.builder()
                .id(UUID.randomUUID())
                .latitude(pointModel.getLatitude())
                .longitude(pointModel.getLongitude())
                .geoHash(pointModel.getGeoHash())
                .processed(false)
                .createdAt(timeUtil.now())
                .build();
    }

    private void saveTasks(PointModel pointModel) {
        List<TaskQueueModel> tasks = new LinkedList<>();
        var now = timeUtil.now();
        for (Zoom zoom : Zoom.values()) {
//            var geoHash = pointModel.getGeoHash().substring(0, zoom.getPrecision());
            var task = TaskQueueModel.builder()
                    .id(UUID.randomUUID())
                    .latitude(pointModel.getLatitude())
                    .longitude(pointModel.getLongitude())
//                    .geoHash(geoHash)
                    .geoHash("fake")
                    .processed(false)
                    .createdAt(now)
                    .build();
            tasks.add(task);
        }
        tasksQueueRepository.insert(tasks);
    }

    private PointModel toDeliveryPoint(CreatePointsRequest.Point point) {
        return PointModel.builder()
                .id(UUID.randomUUID())
                .longitude(point.getLongitude())
                .latitude(point.getLatitude())
//                .geoHash(GeoUtil.calculateGeoHash(point.getLatitude(), point.getLongitude(), 2))
                .geoHash("fake")
                .build();
    }

}
