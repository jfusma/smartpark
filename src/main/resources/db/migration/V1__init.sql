-- Crear tabla sm_api_key
CREATE TABLE IF NOT EXISTS sm_api_key (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    key_value VARCHAR(255) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL
);
