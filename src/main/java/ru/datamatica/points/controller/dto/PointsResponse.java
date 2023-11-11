package ru.datamatica.points.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PointsResponse {
    private List<Point> points;
    private Integer page;
    private Integer pageSize;
    private boolean hasMore;

    @Data
    @Builder
    public static class Point {
        private double longitude;
        private double latitude;
    }
}
