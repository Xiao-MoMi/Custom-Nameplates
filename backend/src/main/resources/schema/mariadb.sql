SET DEFAULT_STORAGE_ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS `{prefix}_data`
(
    `uuid`       char(36)    NOT NULL UNIQUE,
    `data`       longblob    NOT NULL,
    PRIMARY KEY (`uuid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;