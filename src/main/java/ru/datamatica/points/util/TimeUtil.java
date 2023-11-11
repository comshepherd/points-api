package ru.datamatica.points.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TimeUtil {

    private final Clock clock;

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

}
