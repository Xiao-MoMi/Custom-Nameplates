CREATE TABLE IF NOT EXISTS `{prefix}_data`
(
    `uuid`       char(36)    NOT NULL UNIQUE,
    `data`       longblob    NOT NULL,
    PRIMARY KEY (`uuid`)
);