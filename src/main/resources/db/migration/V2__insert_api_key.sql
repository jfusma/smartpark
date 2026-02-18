
-- Inserts iniciales (merge para no duplicar)
MERGE INTO sm_api_key (key_value, active)
KEY(key_value)
VALUES ('ABC123-PERM', TRUE);

MERGE INTO sm_api_key (key_value, active)
KEY(key_value)
VALUES ('XYZ999-NOPERM', FALSE);