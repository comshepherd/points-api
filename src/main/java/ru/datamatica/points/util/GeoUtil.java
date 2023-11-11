package ru.datamatica.points.util;

import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.uber.h3core.H3Core;
import com.uber.h3core.util.LatLng;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.datamatica.points.service.common.Zoom;

@Component
@RequiredArgsConstructor
public class GeoUtil {
    private final H3Core h3Core;

    public S2LatLng getCentroidS2(String cellToken) {
        S2CellId cellId = S2CellId.fromToken(cellToken);
        return cellId.toLatLng();
    }

    public LatLng getCentroidH3(String h3Address) {
        return h3Core.cellToLatLng(h3Address);
    }

    public String calculateGeoHash(double latitude, double longitude) {
//        return GeoHash.encodeHash(latitude, longitude, Zoom.MAX_ZOOM.getPrecision());
//        return GeoHash.geoHashStringWithCharacterPrecision(latitude, longitude, Zoom.MAX_ZOOM.getPrecision());
//        return GeohashUtils.encodeLatLon(latitude, longitude, Zoom.MAX_ZOOM.getPrecision());
        return h3Core.latLngToCellAddress(latitude, longitude, Zoom.MAX_ZOOM.getPrecision());
    }

    public String calculateGeoHash(double latitude, double longitude, int precision) {
//        return GeoHash.encodeHash(latitude, longitude, Zoom.MAX_ZOOM.getPrecision());
//        return GeoHash.geoHashStringWithCharacterPrecision(latitude, longitude, Zoom.MAX_ZOOM.getPrecision());
//        return GeohashUtils.encodeLatLon(latitude, longitude, Zoom.MAX_ZOOM.getPrecision());
        return h3Core.latLngToCellAddress(latitude, longitude, precision);
//        S2LatLng latLng = S2LatLng.fromDegrees(latitude, longitude);
//        S2CellId cellId = S2CellId.fromLatLng(latLng).parent(precision);
//        return cellId.toToken();
    }

}
