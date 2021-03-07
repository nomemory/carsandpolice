CREATE STREAM carLocations (profileId VARCHAR, color VARCHAR, hasPapers BOOLEAN, location VARCHAR)
  WITH (kafka_topic='carLocations', value_format='json', partitions=1);

CREATE STREAM policeLocations(profileId VARCHAR, location VARCHAR)
 WITH (kafka_topic='policeCarLocations', value_format='json', partitions=1);

CREATE STREAM policeStops AS
    SELECT
        c.profileId AS carProfileId,
        p.profileId AS policeProfileId,
        c.hasPapers,
        c.location AS location
    FROM
        carLocations c
    INNER JOIN policeLocations p WITHIN 10 SECONDS ON c.location = p.location
    EMIT CHANGES;

CREATE STREAM carsBlocked AS
    SELECT
        p.carProfileId,
        p.policeProfileId,
        p.location
    FROM
        policeStops p
    WHERE
        p.hasPapers = FALSE;


--
--SELECT * FROM carLocations cl, policeLocations pl
--  WHERE GEO_DISTANCE(cl.latitude, cl.longitude, pl.latitude, pl.longitude) <= 5 EMIT CHANGES;