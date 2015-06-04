CREATE DATABASE IF NOT EXISTS `pefilesdb`
USE `pefilesdb`;

CREATE TABLE IF NOT EXISTS `pe_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(100) NOT NULL,
  `location` varchar(300) NOT NULL,
  `name` varchar(300) NOT NULL,
  `category` varchar(100) NOT NULL,
  `description` text,
  `license` enum('FREE','FREE TO TRY','PURCHASE','UPDATE') NOT NULL,
  `version` varchar(100) DEFAULT NULL,
  `operation_system` varchar(300) DEFAULT NULL,
  `system_requirements` varchar(200) DEFAULT NULL,
  `md5` varchar(32) NOT NULL,
  `sha1` varchar(40) NOT NULL,
  `sha256` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;