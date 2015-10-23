
use soulmate;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
   `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
   `password` varchar(255) DEFAULT NULL,
   `avatar` varchar(255) DEFAULT NULL,
   `name` varchar(255) DEFAULT NULL,
   `gender` int(11) DEFAULT NULL,
   `phone` varchar(255) DEFAULT NULL,
   `email` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 

DROP TABLE IF EXISTS `relation`;
CREATE TABLE `relation` (
`uid` BIGINT(20) NOT NULL COMMENT '',
`friend_id` BIGINT(20) NOT NULL COMMENT '',
`memo` VARCHAR(255) NULL COMMENT '',
`version` BIGINT(20) NULL COMMENT '',
PRIMARY KEY(`uid`, `friend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `relation_reverse`;
CREATE TABLE `relation_reverse` (
`uid` BIGINT(20) NOT NULL COMMENT '',
`belongs_to_uid` BIGINT(20) NOT NULL COMMENT '',
`version` BIGINT(20) NULL COMMENT '',
PRIMARY KEY(`uid`, `belongs_to_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `msg`;
CREATE TABLE `msg` (
`from_uid` BIGINT(20) NULL COMMENT '',
`to_uid` BIGINT(20) NULL COMMENT '',
`msg` VARCHAR(255) NULL COMMENT '',
`sent_time` TIMESTAMP,
KEY(`from_uid`),
KEY(`to_uid`),
KEY(`sent_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `login_status`;
CREATE TABLE `login_status` (
`uid` BIGINT(20) NOT NULL COMMENT '',
`token` VARCHAR(255) NULL COMMENT '',
`last_login_time` TIMESTAMP,
location Point NOT NULL COMMENT 'current location',
PRIMARY KEY(`uid`),
SPATIAL INDEX(location)
)ENGINE=MyISAM DEFAULT CHARSET=utf8;


