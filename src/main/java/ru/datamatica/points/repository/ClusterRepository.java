package ru.datamatica.points.repository;

import lombok.*;
import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateSetMoreStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import ru.datamatica.points.generated.jooq.tables.records.ClustersRecord;
import ru.datamatica.points.repository.model.ClusterModel;

import java.util.List;
import java.util.Objects;

import static ru.datamatica.points.generated.jooq.tables.Clusters.CLUSTERS;

@Repository
@RequiredArgsConstructor
public class ClusterRepository {

    private final DSLContext dsl;

    public List<ClusterModel> find(Integer page,
                                   Integer pageSize,
                                   double eastLong,
                                   double westLong,
                                   double northLat,
                                   double southLat,
                                   int precision) {
        return dsl.selectFrom(CLUSTERS)
                .where(CLUSTERS.CENTROID_LATITUDE.between(southLat, northLat))
                .and(CLUSTERS.CENTROID_LONGITUDE.between(westLong, eastLong))
                .and(CLUSTERS.PRECISION.eq(precision))
                .limit(pageSize)
                .offset((page - 1) * pageSize)
                .fetchInto(ClusterModel.class);
    }

    public int[] upserth3(List<H3Cluster> tsk) {
        var tt = tsk.stream()
                .map(t -> dsl.insertInto(CLUSTERS)
                        .set(CLUSTERS.POINTS_QTY, t.pointsQty)
                        .set(CLUSTERS.CENTROID_LONGITUDE, t.longitude)
                        .set(CLUSTERS.CENTROID_LATITUDE, t.latitude)
                        .set(CLUSTERS.GEO_HASH, t.geoHash)
                        .set(CLUSTERS.PRECISION, t.precision)
                        .onConflict(CLUSTERS.GEO_HASH)
                        .doUpdate()
                        .set(CLUSTERS.POINTS_QTY, CLUSTERS.POINTS_QTY.add(t.pointsQty)))
                .toList();

        return dsl.batch(tt).execute();
    }

    public int upsertClusterOne(ClusterRepository.CLusterCentroidAndPoints cl) {
        var t = upsertCluster(cl);
        return t.execute();
    }

    public int[] upsertCluster(List<ClusterRepository.CLusterCentroidAndPoints> cLusterCentroidAndPoints) {
        var records = cLusterCentroidAndPoints.stream()
                .map(this::upsertCluster)
                .filter(Objects::nonNull)
                .toList();

        return dsl.batch(records).execute();
    }

    private InsertOnDuplicateSetMoreStep<ClustersRecord> upsertCluster(CLusterCentroidAndPoints cLusterCentroidAndPoints) {
        var pointsQty = cLusterCentroidAndPoints.getPointsQty();
        if (pointsQty == 0) {
            // TODO: 05.11.23 log message
            return null;
        }

        var longitude = cLusterCentroidAndPoints.getLongitude();
        var latitude = cLusterCentroidAndPoints.getLatitude();
        var geoHash = cLusterCentroidAndPoints.getGeoHash();

        var newPointsQty = DSL.coalesce(CLUSTERS.POINTS_QTY.plus(pointsQty), 0);

        var longitudeChange = DSL.when(newPointsQty.eq(0), DSL.inline(0.0))
                .otherwise(CLUSTERS.CENTROID_LONGITUDE.mul(CLUSTERS.POINTS_QTY).plus(longitude).div(newPointsQty));

        var latitudeChange = DSL.when(newPointsQty.eq(0), DSL.inline(0.0))
                .otherwise(CLUSTERS.CENTROID_LATITUDE.mul(CLUSTERS.POINTS_QTY).plus(latitude).div(newPointsQty));

//        var longitudeChange = DSL.when(newPointsQty.eq(0), DSL.inline(0.0))
//                .otherwise(
//                        switch (type) {
//                            case INCREMENT -> CLUSTERS.CENTROID_LONGITUDE.mul(CLUSTERS.POINTS_QTY).plus(longitude)
//                                    .div(newPointsQty);
//                            case DECREMENT -> CLUSTERS.CENTROID_LONGITUDE.mul(CLUSTERS.POINTS_QTY).minus(longitude)
//                                    .div(newPointsQty);
//                        }
//
//                );

//        var latitudeChange = DSL.when(newPointsQty.eq(0), DSL.inline(0.0))
//                .otherwise(
//                        switch (type) {
//                            case INCREMENT -> CLUSTERS.CENTROID_LATITUDE.mul(CLUSTERS.POINTS_QTY).plus(latitude)
//                                    .div(newPointsQty);
//                            case DECREMENT -> CLUSTERS.CENTROID_LATITUDE.mul(CLUSTERS.POINTS_QTY).minus(latitude)
//                                    .div(newPointsQty);
//                        }
//
//                );

        return dsl.insertInto(CLUSTERS)
                .set(CLUSTERS.POINTS_QTY, pointsQty)
                .set(CLUSTERS.CENTROID_LONGITUDE, longitude)
                .set(CLUSTERS.CENTROID_LATITUDE, latitude)
                .set(CLUSTERS.GEO_HASH, geoHash)
                .set(CLUSTERS.PRECISION, geoHash.length())
                .onConflict(CLUSTERS.GEO_HASH)
                .doUpdate()
                .set(CLUSTERS.CENTROID_LONGITUDE, longitudeChange)
                .set(CLUSTERS.CENTROID_LATITUDE, latitudeChange)
                .set(CLUSTERS.POINTS_QTY, CLUSTERS.POINTS_QTY.add(pointsQty));
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class H3Cluster {
        private double longitude;
        private double latitude;
        private String geoHash;
        private int pointsQty;
        private int precision;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CLusterCentroidAndPoints {
        private double longitude;
        private double latitude;
        private String geoHash;
        private int pointsQty;
    }

}
