package ru.datamatica.points.service.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;

@Getter
@RequiredArgsConstructor
public enum Zoom {
//    CONTINENT(0, 6, 2),
//    CITY(7, 8, 3),
//    STREET(9, 10, 4),
//    BUILDING(11, 12, 5),
//    D(13, 21, 6);

//    WORLD(0, 2, 1),
//    CONTINENT(3, 7, 2),
//    CITY(8, 9, 3),
//    STREET(9, 10, 4),
//    BUILDING(11, 12, 5),
//    D(13, 14, 6),
//    A(15, 16, 7),
//    B(17, 18, 8);

    Z1(0, 5, 1),
    Z2(6, 6, 2),
    Z3(7, 7, 3),
    Z4(8, 8, 4),
    Z5(9, 9, 5),
    Z6(10, 10, 6),
    Z7(11, 11, 7),
    Z8(12, 12, 8),
    Z9(13, 13, 9),
    Z10(14, 14, 9),
    Z11(15, 15, 11),
    Z12(16, 16, 12),
    Z13(17, 17, 13),
    Z14(18, 18, 14),
    Z15(19, 19, 15);

    public final static Zoom MAX_ZOOM = Arrays.stream(values()).max(Comparator.comparingInt(a -> a.precision)).get();
    public final static Zoom MIN_ZOOM = Arrays.stream(values()).min(Comparator.comparingInt(a -> a.precision)).get();

    private final int zoomFrom;
    private final int zoomTo;
    private final int precision;

    public static int getPrecision(int zoom) {
        return Arrays.stream(values())
                .filter(z -> z.zoomFrom <= zoom && z.zoomTo >= zoom)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Zoom " + zoom + " is not supported"))
                .precision;
    }
}
