-- Beispieldaten für energy_messages
INSERT INTO energy_messages (type, association, kwh, datetime) VALUES
    ('PRODUCER', 'COMMUNITY', 0.003, '2025-01-10 14:33:00'),
    ('PRODUCER', 'COMMUNITY', 0.004, '2025-01-10 14:34:00'),
    ('USER', 'COMMUNITY', 0.002, '2025-01-10 14:33:00'),
    ('USER', 'COMMUNITY', 0.001, '2025-01-10 14:34:00'),
    ('PRODUCER', 'COMMUNITY', 0.005, '2025-01-10 15:33:00'),
    ('USER', 'COMMUNITY', 0.003, '2025-01-10 15:34:00');

-- Beispieldaten für hourly_usage
INSERT INTO hourly_usage (hour, community_produced, community_used, grid_used) VALUES
    ('2025-01-10 14:00:00', 18.05, 18.02, 1.056),
    ('2025-01-10 13:00:00', 15.015, 14.033, 2.049),
    ('2025-01-10 15:00:00', 20.123, 19.891, 0.856),
    ('2025-01-10 16:00:00', 12.567, 11.987, 1.234),
    ('2025-01-10 17:00:00', 10.234, 9.876, 2.345);

-- Beispieldaten für current_percentages
INSERT INTO current_percentages (hour, community_depleted, grid_portion) VALUES
    ('2025-01-10 14:00:00', 99.83, 5.53),
    ('2025-01-10 15:00:00', 98.85, 4.12),
    ('2025-01-10 16:00:00', 95.38, 9.33),
    ('2025-01-10 17:00:00', 96.50, 19.19);