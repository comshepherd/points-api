CREATE TABLE points
(
    id        UUID PRIMARY KEY,
    latitude  DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    geo_hash  TEXT             NOT NULL
);

CREATE INDEX idx_delivery_points_processed_geo_hash ON points (geo_hash);


CREATE TABLE clusters
(
    geo_hash           TEXT             NOT NULL UNIQUE,
    centroid_latitude  DOUBLE PRECISION NOT NULL,
    centroid_longitude DOUBLE PRECISION NOT NULL,
    points_qty         INTEGER          NOT NULL,
    precision          INTEGER          NOT NULL
);

CREATE INDEX idx_clusters_centroid ON clusters (centroid_latitude, centroid_longitude, precision);

CREATE TABLE tasks_queue
(
    id           UUID             NOT NULL PRIMARY KEY,
    latitude     DOUBLE PRECISION NOT NULL,
    longitude    DOUBLE PRECISION NOT NULL,
    geo_hash     TEXT             NOT NULL,
    processed    BOOLEAN          NOT NULL,
    created_at   TIMESTAMP        NOT NULL,
    processed_at TIMESTAMP
);

CREATE INDEX idx_tasks_queue_unprocessed ON tasks_queue (processed, created_at, geo_hash);
