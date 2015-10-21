DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
   `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
   `password` varchar(255) DEFAULT NULL,
   `user_verify` varchar(255) DEFAULT NULL,
   `country` varchar(255) DEFAULT NULL,
   `province` varchar(255) DEFAULT NULL,
   `avatar` text,   `city` varchar(255) DEFAULT NULL,
   `email` varchar(255) DEFAULT NULL,
   `first_name` varchar(255) DEFAULT NULL,
   `last_name` varchar(255) DEFAULT NULL,
   `nick_name` varchar(255) DEFAULT NULL,
   `gender` int(11) DEFAULT NULL,
   `phone` varchar(255) DEFAULT NULL,
   `postal` varchar(255) DEFAULT NULL,
   `address` varchar(255) DEFAULT NULL,
   `ease_mob_id` varchar(255) DEFAULT NULL,
   `ease_mob_tk` varchar(255) DEFAULT NULL,
   `signature` text, 
   `verify_code` varchar(255) DEFAULT NULL,
   `state` int(11) DEFAULT 0,
   `expiry` timestamp DEFAULT 0,
   PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
#============2.0 Éý¼¶=============
ALTER TABLE `user` 
ADD COLUMN `relation_version` BIGINT(24) NULL DEFAULT 0 COMMENT '' AFTER `expiry`;




CREATE TABLE `relation` (
`id` BIGINT(20) NOT NULL COMMENT '',
`user_id` BIGINT(20) NULL COMMENT '',
`friend_id` BIGINT(20) NULL COMMENT '',
`group_id` BIGINT(20) NULL COMMENT '',
`note_name` VARCHAR(255) NULL COMMENT '',
`relation` INT(11) NULL DEFAULT 0 COMMENT '',
`version` BIGINT(20) NULL COMMENT '',
`invalid` INT(11) NULL DEFAULT 0 COMMENT '',
PRIMARY KEY (`id`) COMMENT '');