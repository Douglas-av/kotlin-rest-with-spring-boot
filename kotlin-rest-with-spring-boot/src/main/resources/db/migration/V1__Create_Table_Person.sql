CREATE TABLE IF NOT EXISTS`person` (
  `gender` varchar(6) NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(80) NOT NULL,
  `last_name` varchar(80) NOT NULL,
  `address` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
)