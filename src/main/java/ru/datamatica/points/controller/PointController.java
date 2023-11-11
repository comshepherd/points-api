package ru.datamatica.points.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.datamatica.points.controller.dto.CreatePointsRequest;
import ru.datamatica.points.service.PointService;

@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;

    @PostMapping
    public void createPoints(@RequestBody @Valid CreatePointsRequest req) {
        pointService.save(req);
        // TODO: 07.10.23 id не важны, но нужно сохранить все
    }

}
