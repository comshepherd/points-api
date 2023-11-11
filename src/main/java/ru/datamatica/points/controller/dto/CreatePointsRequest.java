package ru.datamatica.points.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


@Data
public class CreatePointsRequest {

    @Valid
    private List<Point> points;

    @Data
    public static class Point {
        @NotNull
        private Double longitude;
        @NotNull
        private Double latitude;
    }
}
