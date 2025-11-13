CREATE TABLE IF NOT EXISTS tweets
(
    id              UUID PRIMARY KEY,
    tweet_id        VARCHAR(255)   NOT NULL,
    author_id       VARCHAR(255)   NOT NULL,
    created         VARCHAR(255)   NOT NULL,
    text            MEDIUMTEXT     NOT NULL
);

