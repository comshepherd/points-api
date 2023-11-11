package ru.datamatica.points.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterResponse {
    private Integer page;
    private Integer pageSize;
    private boolean hasMore;
    private List<Cluster> clusters;

    @Data
    @Builder
    public static class Cluster {
        private int pointsQty;
        private double longitude;
        private double latitude;
    }
}
