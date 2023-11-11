package ru.datamatica.points.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ClusterModel {
    private String geoHash;
    private double centroidLatitude;
    private double centroidLongitude;
    private int pointsQty;
    private int precision;

}
