package ru.datamatica.points.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.datamatica.points.repository.ClusterRepository;
import ru.datamatica.points.repository.model.ClusterModel;
import ru.datamatica.points.service.common.Zoom;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterService {
    private final ClusterRepository clusterRepository;

    public List<ClusterModel> getClustersForMap(@NonNull Integer page,
                                                @NonNull Integer pageSize,
                                                double eastLong,
                                                double westLong,
                                                double northLat,
                                                double southLat,
                                                int zoom) {
        var precision = Zoom.getPrecision(zoom);
        return clusterRepository.find(
                page,
                pageSize,
                eastLong,
                westLong,
                northLat,
                southLat,
                precision
        );

    }

}
