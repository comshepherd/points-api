package ru.datamatica.points.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.datamatica.points.controller.dto.ClusterResponse;
import ru.datamatica.points.service.ClusterService;
import ru.datamatica.points.service.common.Zoom;

@Slf4j
@RestController
@RequestMapping("/api/v1/clusters")
@RequiredArgsConstructor
public class ClusterController {
    private final ClusterService clusterService;

    // TODO: 11.11.23 можно добавить Cache-Control
    @GetMapping
    public ClusterResponse getClustersPaged(@RequestParam @Min(1) int page,
                                            @RequestParam int pageSize,
                                            @RequestParam @Max(20) int zoom,
                                            @RequestParam double east,
                                            @RequestParam double west,
                                            @RequestParam double north,
                                            @RequestParam double south) {
        var result = clusterService.getClustersForMap(
                page,
                pageSize + 1,
                east,
                west,
                north,
                south,
                zoom
        );
        var hasMore = result.size() > pageSize;
        if (hasMore) {
            result.remove(result.size() - 1);
        }
        var clusters = result.stream()
                .map(cluster -> ClusterResponse.Cluster.builder()
                        .latitude(cluster.getCentroidLatitude())
                        .longitude(cluster.getCentroidLongitude())
                        .pointsQty(cluster.getPointsQty())
                        .build())
                .toList();

        return ClusterResponse.builder()
                .clusters(clusters)
                .page(page)
                .pageSize(pageSize)
                .hasMore(hasMore)
                .build();
    }

}
