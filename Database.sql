-- MoparRSC DB Structure
--
-- ------------------------------------------------------
-- Server version	5.1.49-1ubuntu8.1-log


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

--
-- Definition of table `drops`
--

DROP TABLE IF EXISTS `drops`;
CREATE TABLE  `drops` (
  `id` int(11) DEFAULT NULL,
  `item` varchar(255) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `weight` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `dupe_data`
--

DROP TABLE IF EXISTS `dupe_data`;
CREATE TABLE  `dupe_data` (
  `user` varchar(255) NOT NULL,
  `userhash` varchar(255) NOT NULL,
  `string` text NOT NULL,
  `time` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `error_reports`
--

DROP TABLE IF EXISTS `error_reports`;
CREATE TABLE  `error_reports` (
  `data` text,
  `email` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `unix` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `gp_count`
--

DROP TABLE IF EXISTS `gp_count`;
CREATE TABLE  `gp_count` (
  `unixtime` varchar(255) DEFAULT NULL,
  `amount` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `items`
--

DROP TABLE IF EXISTS `items`;
CREATE TABLE  `items` (
  `id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `stackable` varchar(255) DEFAULT NULL,
  `wieldable` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `online_count`
--

DROP TABLE IF EXISTS `online_count`;
CREATE TABLE  `online_count` (
  `unixtime` varchar(255) DEFAULT NULL,
  `online` int(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_bank`
--

DROP TABLE IF EXISTS `pk_bank`;
CREATE TABLE  `pk_bank` (
  `user` varchar(255) DEFAULT NULL,
  `id` int(10) unsigned NOT NULL,
  `amount` int(10) unsigned NOT NULL DEFAULT '1',
  `slot` int(5) unsigned NOT NULL,
  KEY `user` (`user`),
  KEY `id` (`id`),
  KEY `amount` (`amount`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_banlog`
--

DROP TABLE IF EXISTS `pk_banlog`;
CREATE TABLE  `pk_banlog` (
  `user` varchar(255) DEFAULT NULL,
  `staff` varchar(255) DEFAULT NULL,
  `time` int(10) DEFAULT NULL,
  KEY `staff` (`staff`),
  KEY `time` (`time`),
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_curstats`
--

DROP TABLE IF EXISTS `pk_curstats`;
CREATE TABLE  `pk_curstats` (
  `user` varchar(255) NOT NULL,
  `cur_attack` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_defense` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_strength` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_hits` int(5) unsigned NOT NULL DEFAULT '10',
  `cur_ranged` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_prayer` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_magic` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_cooking` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_woodcut` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_fletching` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_fishing` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_firemaking` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_crafting` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_smithing` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_mining` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_herblaw` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_agility` int(5) unsigned NOT NULL DEFAULT '1',
  `cur_thieving` int(5) unsigned NOT NULL DEFAULT '1',
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_experience`
--

DROP TABLE IF EXISTS `pk_experience`;
CREATE TABLE  `pk_experience` (
  `user` varchar(255) NOT NULL,
  `exp_attack` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_defense` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_strength` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_hits` int(10) unsigned NOT NULL DEFAULT '1200',
  `exp_ranged` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_prayer` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_magic` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_cooking` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_woodcut` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_fletching` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_fishing` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_firemaking` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_crafting` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_smithing` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_mining` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_herblaw` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_agility` int(10) unsigned NOT NULL DEFAULT '0',
  `exp_thieving` int(10) unsigned NOT NULL DEFAULT '0',
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `oo_attack` int(11) DEFAULT NULL,
  `oo_defense` int(11) DEFAULT NULL,
  `oo_strength` int(11) DEFAULT NULL,
  `oo_ranged` int(11) DEFAULT NULL,
  `oo_prayer` int(11) DEFAULT NULL,
  `oo_magic` int(11) DEFAULT NULL,
  `oo_cooking` int(11) DEFAULT NULL,
  `oo_woodcut` int(11) DEFAULT NULL,
  `oo_fletching` int(11) DEFAULT NULL,
  `oo_fishing` int(11) DEFAULT NULL,
  `oo_firemaking` int(11) DEFAULT NULL,
  `oo_crafting` int(11) DEFAULT NULL,
  `oo_smithing` int(11) DEFAULT NULL,
  `oo_mining` int(11) DEFAULT NULL,
  `oo_herblaw` int(11) DEFAULT NULL,
  `oo_agility` int(11) DEFAULT NULL,
  `oo_thieving` int(11) DEFAULT NULL,
  `oo_hits` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_friends`
--

DROP TABLE IF EXISTS `pk_friends`;
CREATE TABLE  `pk_friends` (
  `user` varchar(255) NOT NULL,
  `friend` varchar(255) NOT NULL,
  KEY `user` (`user`),
  KEY `friend` (`friend`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_ignores`
--

DROP TABLE IF EXISTS `pk_ignores`;
CREATE TABLE  `pk_ignores` (
  `user` varchar(255) NOT NULL,
  `ignore` varchar(255) NOT NULL,
  KEY `ignore` (`ignore`),
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_invitems`
--

DROP TABLE IF EXISTS `pk_invitems`;
CREATE TABLE  `pk_invitems` (
  `user` varchar(255) NOT NULL,
  `id` int(10) unsigned NOT NULL,
  `amount` int(10) unsigned NOT NULL DEFAULT '1',
  `wielded` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `slot` int(5) unsigned NOT NULL,
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_ipbans`
--

DROP TABLE IF EXISTS `pk_ipbans`;
CREATE TABLE  `pk_ipbans` (
  `ip` varchar(15) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ip`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_kills`
--

DROP TABLE IF EXISTS `pk_kills`;
CREATE TABLE  `pk_kills` (
  `user` varchar(255) NOT NULL,
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `killed` varchar(45) NOT NULL,
  `time` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_logins`
--

DROP TABLE IF EXISTS `pk_logins`;
CREATE TABLE  `pk_logins` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user` varchar(45) NOT NULL,
  `time` int(5) unsigned NOT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  PRIMARY KEY (`id`),
  KEY `ip` (`ip`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Definition of table `pk_market`
--

DROP TABLE IF EXISTS `pk_market`;
CREATE TABLE  `pk_market` (
  `owner` int(10) NOT NULL,
  `item_id` int(10) NOT NULL,
  `amount` int(10) NOT NULL,
  `selling_price` int(10) NOT NULL,
  PRIMARY KEY (`owner`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_mutelog`
--

DROP TABLE IF EXISTS `pk_mutelog`;
CREATE TABLE  `pk_mutelog` (
  `user` varchar(255) NOT NULL,
  `staff` varchar(255) NOT NULL,
  `time` int(11) NOT NULL,
  `report_id` int(11) NOT NULL,
  `duration` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_online`
--

DROP TABLE IF EXISTS `pk_online`;
CREATE TABLE  `pk_online` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user` varchar(45) NOT NULL,
  `username` varchar(45) NOT NULL,
  `x` varchar(45) NOT NULL,
  `y` varchar(45) NOT NULL,
  `world` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Definition of table `pk_players`
--

DROP TABLE IF EXISTS `pk_players`;
CREATE TABLE  `pk_players` (
  `user` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL DEFAULT '',
  `pending_deletion` int(11) DEFAULT '0',
  `group_id` int(10) DEFAULT '0',
  `owner` int(5) unsigned NOT NULL,
  `owner_username` varchar(255) DEFAULT NULL,
  `sub_expires` int(5) unsigned DEFAULT '0',
  `combat` int(10) DEFAULT '3',
  `skill_total` int(10) DEFAULT '3',
  `x` int(5) unsigned DEFAULT '213',
  `y` int(5) unsigned DEFAULT '452',
  `fatigue` int(10) DEFAULT '0',
  `combatstyle` tinyint(1) DEFAULT '0',
  `block_chat` tinyint(1) unsigned DEFAULT '0',
  `block_private` tinyint(1) unsigned DEFAULT '0',
  `block_trade` tinyint(1) unsigned DEFAULT '0',
  `block_duel` tinyint(1) unsigned DEFAULT '0',
  `cameraauto` tinyint(1) unsigned DEFAULT '0',
  `onemouse` tinyint(1) unsigned DEFAULT '0',
  `soundoff` tinyint(1) unsigned DEFAULT '0',
  `showroof` tinyint(1) DEFAULT '0',
  `autoscreenshot` tinyint(1) DEFAULT '0',
  `combatwindow` tinyint(1) DEFAULT '0',
  `haircolour` int(5) unsigned DEFAULT '2',
  `topcolour` int(5) unsigned DEFAULT '8',
  `trousercolour` int(5) unsigned DEFAULT '14',
  `skincolour` int(5) unsigned DEFAULT '0',
  `headsprite` int(5) unsigned DEFAULT '1',
  `bodysprite` int(5) unsigned DEFAULT '2',
  `male` tinyint(1) unsigned DEFAULT '1',
  `skulled` int(10) unsigned DEFAULT '0',
  `pass` varchar(255) NOT NULL,
  `creation_date` int(10) unsigned NOT NULL DEFAULT '0',
  `creation_ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  `login_date` int(10) unsigned DEFAULT '0',
  `login_ip` varchar(15) DEFAULT '0.0.0.0',
  `playermod` tinyint(1) unsigned DEFAULT '0',
  `loggedin` tinyint(1) DEFAULT '0',
  `banned` tinyint(1) DEFAULT '0',
  `muted` int(10) DEFAULT '0',
  `deaths` int(10) DEFAULT '0',
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `online` tinyint(1) unsigned zerofill DEFAULT '0',
  `world` int(10) DEFAULT '1',
  `quest_points` int(5) DEFAULT NULL,
  `eventcd` int(10) unsigned DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_quests`
--

DROP TABLE IF EXISTS `pk_quests`;
CREATE TABLE  `pk_quests` (
  `id` int(11) DEFAULT NULL,
  `stage` int(11) DEFAULT NULL,
  `user` varchar(255) DEFAULT NULL,
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_reports`
--

DROP TABLE IF EXISTS `pk_reports`;
CREATE TABLE  `pk_reports` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `from` varchar(255) NOT NULL,
  `about` varchar(255) NOT NULL,
  `time` int(10) unsigned NOT NULL,
  `reason` int(5) unsigned NOT NULL,
  `snapshot_from` longtext NOT NULL,
  `snapshot_about` longtext NOT NULL,
  `chatlogs` longtext NOT NULL,
  `from_x` int(10) NOT NULL,
  `from_y` int(10) NOT NULL,
  `about_x` int(10) NOT NULL,
  `about_y` int(10) NOT NULL,
  `zapped` int(10) unsigned DEFAULT NULL,
  `zapped_by` varchar(255) DEFAULT NULL,
  `sendToMod` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_reports_comments`
--

DROP TABLE IF EXISTS `pk_reports_comments`;
CREATE TABLE  `pk_reports_comments` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `report_id` int(10) NOT NULL,
  `poster` int(10) NOT NULL,
  `time` int(10) NOT NULL,
  `text` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_stat_reduction`
--

DROP TABLE IF EXISTS `pk_stat_reduction`;
CREATE TABLE  `pk_stat_reduction` (
  `user` int(10) NOT NULL,
  `account` varchar(255) NOT NULL,
  `account_hash` varchar(255) NOT NULL,
  `skill` int(10) NOT NULL,
  `voucher` varchar(255) DEFAULT NULL,
  `time` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_tradelog`
--

DROP TABLE IF EXISTS `pk_tradelog`;
CREATE TABLE  `pk_tradelog` (
  `from` varchar(255) DEFAULT NULL,
  `to` varchar(255) DEFAULT NULL,
  `time` int(10) DEFAULT NULL,
  `id` int(10) DEFAULT NULL,
  `x` int(10) DEFAULT NULL,
  `y` int(10) DEFAULT NULL,
  `amount` int(10) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  KEY `trade_from` (`from`),
  KEY `trade_to` (`to`),
  KEY `tradelog_time` (`time`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Definition of table `pk_worlds`
--

DROP TABLE IF EXISTS `pk_worlds`;
CREATE TABLE  `pk_worlds` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `location` varchar(45) NOT NULL,
  `ip` varchar(45) NOT NULL,
  `port` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
