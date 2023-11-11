package ru.datamatica.points.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TaskQueueModel {
    private UUID id;
    private double latitude;
    private double longitude;
    private String geoHash;
    private boolean processed;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
}
