CREATE TABLE IF NOT EXISTS PARKING_SPOTS (
    id INT PRIMARY KEY,
    has_charger BOOLEAN NOT NULL,
    occupied BOOLEAN NOT NULL
);

-- insert charger spots (1-20)
INSERT INTO PARKING_SPOTS (id, has_charger, occupied)
SELECT x, TRUE, FALSE
FROM SYSTEM_RANGE(1,20);

-- insert non-charger spots (21-100)
INSERT INTO PARKING_SPOTS (id, has_charger, occupied)
SELECT x, FALSE, FALSE
FROM SYSTEM_RANGE(21,100);