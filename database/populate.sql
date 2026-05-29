DELETE FROM Users;

INSERT INTO Users (username, password, last_token)
    VALUES
        ('casemiro', '$2a$10$3BOfEUowWYSHT9Orbav/5O.cLQenifIufm2miZKFH2rvMXF.aZEny', NULL),
        ('heitor', '$2a$10$3BOfEUowWYSHT9Orbav/5O.cLQenifIufm2miZKFH2rvMXF.aZEny', NULL),
        ('tomas', '$2a$10$3BOfEUowWYSHT9Orbav/5O.cLQenifIufm2miZKFH2rvMXF.aZEny', NULL),
        ('tester', '$2a$10$3BOfEUowWYSHT9Orbav/5O.cLQenifIufm2miZKFH2rvMXF.aZEny', NULL);