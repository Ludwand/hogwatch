DROP DATABASE IF EXISTS dat257;
DROP ROLE IF EXISTS dat257;

CREATE USER dat257 WITH PASSWORD 'dat257';
CREATE DATABASE dat257;
ALTER DATABASE dat257 OWNER TO dat257;
\c dat257

CREATE TABLE Sightings (
    id SERIAL PRIMARY KEY,
    userid TEXT NOT NULL,
    lat Float NOT NULL,
    long Float NOT NULL,
    time BIGINT NOT NULL DEFAULT (extract(epoch from now())),
    image TEXT
);
ALTER TABLE Sightings OWNER TO dat257;

\set CLUSTER_DISTANCE 1.0
\set MIN_TIME_DIFF 3600

CREATE VIEW Pins (id, lat, long, quantity)
    AS WITH SightingsOrder AS (SELECT * FROM Sightings ORDER BY id ASC),
    ExcludedSightings AS (SELECT DISTINCT a.id FROM SightingsOrder a INNER JOIN SightingsOrder b ON a.id > b.id WHERE ABS(a.time - b.time) < :MIN_TIME_DIFF AND (a.lat - b.lat) ^ 2 + (a.long - b.long) ^ 2 < :CLUSTER_DISTANCE),
    IncludedSightings AS (SELECT * FROM SightingsOrder WHERE id NOT IN (SELECT id FROM ExcludedSightings)),
    AggregatedSightings AS (SELECT a.id AS a_id, MIN(b.id) AS b_id FROM IncludedSightings a INNER JOIN IncludedSightings b ON a.id >= b.id WHERE (a.lat - b.lat) ^ 2 + (a.long - b.long) ^ 2 < :CLUSTER_DISTANCE GROUP BY a.id)
    SELECT a.id, lat, long, COUNT(b_id) FROM IncludedSightings a INNER JOIN AggregatedSightings b ON a.id = b_id GROUP BY a.id, lat, long;

ALTER VIEW Pins OWNER TO dat257;
