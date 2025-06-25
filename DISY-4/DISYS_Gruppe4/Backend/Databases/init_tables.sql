-- Erstelle die Tabellen
CREATE TABLE energy_messages (
    id SERIAL PRIMARY KEY,
    type VARCHAR(10) NOT NULL,
    association VARCHAR(10) NOT NULL,
    kwh DECIMAL(10,3) NOT NULL,
    datetime TIMESTAMP NOT NULL
);

CREATE TABLE hourly_usage (
    id SERIAL PRIMARY KEY,
    hour TIMESTAMP NOT NULL,
    community_produced DECIMAL(10,3) NOT NULL DEFAULT 0,
    community_used DECIMAL(10,3) NOT NULL DEFAULT 0,
    grid_used DECIMAL(10,3) NOT NULL DEFAULT 0,
    UNIQUE(hour)
);

CREATE TABLE current_percentages (
    id SERIAL PRIMARY KEY,
    hour TIMESTAMP NOT NULL,
    community_depleted DECIMAL(5,2) NOT NULL,
    grid_portion DECIMAL(5,2) NOT NULL,
    UNIQUE(hour)
);