CREATE TABLE user(
    id  BIGINT  NOT NULL AUTO_INCREMENT,
    account     VARCHAR(50) UNIQUE,
    password    VARCHAR(50),
    find_email  VARCHAR(50) UNIQUE,
    sns_email   VARCHAR(50) UNIQUE,
    nickname    VARCHAR(50) UNIQUE,
    profile     VARCHAR(50),
    user_type   tinyint NOT NULL DEFAULT 0,
    is_auth     tinyint NOT NULL DEFAULT 0,
    created_at  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(id)
);