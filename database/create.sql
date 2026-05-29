DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    id INTEGER PRIMARY KEY,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    last_token TEXT UNIQUE DEFAULT NULL,
    token_expires_at_ms INTEGER DEFAULT 0
);
