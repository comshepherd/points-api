package ru.datamatica.points.repository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.datamatica.points.generated.jooq.tables.records.PointsRecord;
import ru.datamatica.points.repository.model.PointModel;

import java.util.List;

import static ru.datamatica.points.generated.jooq.Tables.POINTS;


@Repository
@RequiredArgsConstructor
public class PointRepository {

    private final DSLContext dsl;


    public int[] insert(List<PointModel> models) {
        var records = CollectionUtils.emptyIfNull(models).stream()
                .map(model -> dsl.insertInto(POINTS).set(toRecord(model)))
                .toList();

        return dsl.batch(records).execute();
    }

    private PointsRecord toRecord(PointModel model) {
        var record = new PointsRecord();
        record.setId(model.getId());
        record.setLongitude(model.getLongitude());
        record.setLatitude(model.getLatitude());
        record.setGeoHash(model.getGeoHash());
        return record;
    }

}
