package ru.datamatica.points.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointModel {
    private UUID id;
    private double longitude;
    private double latitude;
    private String geoHash;
}
