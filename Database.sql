-- MoparRSC DB Structure
--
-- ------------------------------------------------------
-- Server version	5.1.49-1ubuntu8.1


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema moparclassic
--

CREATE DATABASE IF NOT EXISTS moparclassic;
USE moparclassic;
CREATE TABLE  `moparclassic`.`bans` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(200) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `email` varchar(80) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `expire` int(10) unsigned DEFAULT NULL,
  `ban_creator` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `bans_username_idx` (`username`(25))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
CREATE TABLE  `moparclassic`.`categories` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `cat_name` varchar(80) NOT NULL DEFAULT 'New Category',
  `disp_position` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE  `moparclassic`.`censoring` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `search_for` varchar(60) NOT NULL DEFAULT '',
  `replace_with` varchar(60) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
CREATE TABLE  `moparclassic`.`config` (
  `conf_name` varchar(255) NOT NULL DEFAULT '',
  `conf_value` text,
  PRIMARY KEY (`conf_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE  `moparclassic`.`drops` (
  `id` int(11) DEFAULT NULL,
  `item` varchar(255) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `weight` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`dupe_data` (
  `user` varchar(255) NOT NULL,
  `userhash` varchar(255) NOT NULL,
  `string` text NOT NULL,
  `time` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE  `moparclassic`.`error_reports` (
  `data` text,
  `email` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `unix` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`forum_perms` (
  `group_id` int(10) NOT NULL DEFAULT '0',
  `forum_id` int(10) NOT NULL DEFAULT '0',
  `read_forum` tinyint(1) NOT NULL DEFAULT '1',
  `post_replies` tinyint(1) NOT NULL DEFAULT '1',
  `post_topics` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`group_id`,`forum_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
CREATE TABLE  `moparclassic`.`forums` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `forum_name` varchar(80) NOT NULL DEFAULT 'New forum',
  `forum_desc` text,
  `redirect_url` varchar(100) DEFAULT NULL,
  `moderators` text,
  `num_topics` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `num_posts` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `last_post` int(10) unsigned DEFAULT NULL,
  `last_post_id` int(10) unsigned DEFAULT NULL,
  `last_poster` varchar(200) DEFAULT NULL,
  `sort_by` tinyint(1) NOT NULL DEFAULT '0',
  `disp_position` int(10) NOT NULL DEFAULT '0',
  `cat_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE  `moparclassic`.`gp_count` (
  `unixtime` varchar(255) DEFAULT NULL,
  `amount` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`groups` (
  `g_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `g_title` varchar(50) NOT NULL DEFAULT '',
  `g_user_title` varchar(50) DEFAULT NULL,
  `g_moderator` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_edit_users` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_rename_users` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_change_passwords` tinyint(1) NOT NULL DEFAULT '0',
  `g_mod_ban_users` tinyint(1) NOT NULL DEFAULT '0',
  `g_read_board` tinyint(1) NOT NULL DEFAULT '1',
  `g_view_users` tinyint(1) NOT NULL DEFAULT '1',
  `g_post_replies` tinyint(1) NOT NULL DEFAULT '1',
  `g_post_topics` tinyint(1) NOT NULL DEFAULT '1',
  `g_edit_posts` tinyint(1) NOT NULL DEFAULT '1',
  `g_delete_posts` tinyint(1) NOT NULL DEFAULT '1',
  `g_delete_topics` tinyint(1) NOT NULL DEFAULT '1',
  `g_set_title` tinyint(1) NOT NULL DEFAULT '1',
  `g_search` tinyint(1) NOT NULL DEFAULT '1',
  `g_search_users` tinyint(1) NOT NULL DEFAULT '1',
  `g_send_email` tinyint(1) NOT NULL DEFAULT '1',
  `g_post_flood` smallint(6) NOT NULL DEFAULT '30',
  `g_search_flood` smallint(6) NOT NULL DEFAULT '30',
  `g_email_flood` smallint(6) NOT NULL DEFAULT '60',
  PRIMARY KEY (`g_id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE  `moparclassic`.`invites` (
  `owner` varchar(255) NOT NULL DEFAULT '',
  `code` varchar(255) NOT NULL DEFAULT '',
  `time` int(10) unsigned NOT NULL DEFAULT '0',
  `invites` varchar(255) NOT NULL DEFAULT '1',
  `email` varchar(255) NOT NULL DEFAULT ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`irc_online` (
  `username` varchar(255) NOT NULL,
  `rank` int(1) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`irc_stats` (
  `username` varchar(255) NOT NULL,
  `messages` int(10) DEFAULT NULL,
  `modes` int(10) DEFAULT NULL,
  `kicks` int(10) DEFAULT NULL,
  `kicked` int(10) DEFAULT NULL,
  `lastTimeSpoken` bigint(11) DEFAULT NULL,
  `joins` int(10) DEFAULT NULL,
  `parts` int(10) DEFAULT NULL,
  `randomstring` text,
  `moderatedchan` int(10) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`items` (
  `id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `stackable` varchar(255) DEFAULT NULL,
  `wieldable` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`logins` (
  `id` int(10) DEFAULT NULL,
  `login_ip` varchar(15) DEFAULT NULL,
  `time` int(10) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`messages` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `owner` int(10) NOT NULL DEFAULT '0',
  `subject` varchar(120) NOT NULL DEFAULT '',
  `message` text,
  `sender` varchar(120) NOT NULL DEFAULT '',
  `sender_id` int(10) NOT NULL DEFAULT '0',
  `posted` int(10) NOT NULL DEFAULT '0',
  `sender_ip` varchar(120) NOT NULL DEFAULT '0.0.0.0',
  `smileys` tinyint(1) NOT NULL DEFAULT '1',
  `status` tinyint(1) NOT NULL DEFAULT '0',
  `showed` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `messages_owner_idx` (`owner`)
) ENGINE=MyISAM AUTO_INCREMENT=23964 DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`online` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '1',
  `ident` varchar(200) NOT NULL DEFAULT '',
  `logged` int(10) unsigned NOT NULL DEFAULT '0',
  `idle` tinyint(1) NOT NULL DEFAULT '0',
  `last_post` int(10) unsigned DEFAULT NULL,
  `last_search` int(10) unsigned DEFAULT NULL,
  UNIQUE KEY `online_user_id_ident_idx` (`user_id`,`ident`(25)),
  KEY `online_ident_idx` (`ident`(25)),
  KEY `online_logged_idx` (`logged`)
) ENGINE=MEMORY DEFAULT CHARSET=utf8;
CREATE TABLE  `moparclassic`.`online_count` (
  `unixtime` varchar(255) DEFAULT NULL,
  `online` int(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`pk_bank` (
  `user` varchar(255) DEFAULT NULL,
  `id` int(10) unsigned NOT NULL,
  `amount` int(10) unsigned NOT NULL DEFAULT '1',
  `slot` int(5) unsigned NOT NULL,
  KEY `user` (`user`),
  KEY `id` (`id`),
  KEY `amount` (`amount`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE  `moparclassic`.`pk_banlog` (
  `user` varchar(255) DEFAULT NULL,
  `staff` varchar(255) DEFAULT NULL,
  `time` int(10) DEFAULT NULL,
  KEY `staff` (`staff`),
  KEY `time` (`time`),
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`pk_curstats` (
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
) ENGINE=MyISAM AUTO_INCREMENT=350943 DEFAULT CHARSET=latin1;

CREATE TABLE  `moparclassic`.`pk_experience` (
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
) ENGINE=MyISAM AUTO_INCREMENT=350943 DEFAULT CHARSET=latin1;

CREATE TABLE  `moparclassic`.`pk_friends` (
  `user` varchar(255) NOT NULL,
  `friend` varchar(255) NOT NULL,
  KEY `user` (`user`),
  KEY `friend` (`friend`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`pk_ignores` (
  `user` varchar(255) NOT NULL,
  `ignore` varchar(255) NOT NULL,
  KEY `ignore` (`ignore`),
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`pk_invitems` (
  `user` varchar(255) NOT NULL,
  `id` int(10) unsigned NOT NULL,
  `amount` int(10) unsigned NOT NULL DEFAULT '1',
  `wielded` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `slot` int(5) unsigned NOT NULL,
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE  `moparclassic`.`pk_ipbans` (
  `ip` varchar(15) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ip`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
CREATE TABLE  `moparclassic`.`pk_kills` (
  `user` varchar(255) NOT NULL,
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `killed` varchar(45) NOT NULL,
  `time` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`pk_logins` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user` varchar(45) NOT NULL,
  `time` int(5) unsigned NOT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  PRIMARY KEY (`id`),
  KEY `ip` (`ip`)
) ENGINE=MyISAM AUTO_INCREMENT=4254489 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

CREATE TABLE  `moparclassic`.`pk_market` (
  `owner` int(10) NOT NULL,
  `item_id` int(10) NOT NULL,
  `amount` int(10) NOT NULL,
  `selling_price` int(10) NOT NULL,
  PRIMARY KEY (`owner`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`pk_mutelog` (
  `user` varchar(255) NOT NULL,
  `staff` varchar(255) NOT NULL,
  `time` int(11) NOT NULL,
  `report_id` int(11) NOT NULL,
  `duration` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`pk_online` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user` varchar(45) NOT NULL,
  `username` varchar(45) NOT NULL,
  `x` varchar(45) NOT NULL,
  `y` varchar(45) NOT NULL,
  `world` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 9216 kB';
CREATE TABLE  `moparclassic`.`pk_players` (
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
) ENGINE=MyISAM AUTO_INCREMENT=350944 DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 9216 kB';

CREATE TABLE  `moparclassic`.`pk_quests` (
  `id` int(11) DEFAULT NULL,
  `stage` int(11) DEFAULT NULL,
  `user` varchar(255) DEFAULT NULL,
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE  `moparclassic`.`pk_reports` (
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
) ENGINE=MyISAM AUTO_INCREMENT=4340 DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`pk_reports_comments` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `report_id` int(10) NOT NULL,
  `poster` int(10) NOT NULL,
  `time` int(10) NOT NULL,
  `text` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4222 DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`pk_stat_reduction` (
  `user` int(10) NOT NULL,
  `account` varchar(255) NOT NULL,
  `account_hash` varchar(255) NOT NULL,
  `skill` int(10) NOT NULL,
  `voucher` varchar(255) DEFAULT NULL,
  `time` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`pk_tradelog` (
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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE  `moparclassic`.`pk_worlds` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `location` varchar(45) NOT NULL,
  `ip` varchar(45) NOT NULL,
  `port` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
CREATE TABLE  `moparclassic`.`posts` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `poster` varchar(200) NOT NULL DEFAULT '',
  `poster_id` int(10) unsigned NOT NULL DEFAULT '1',
  `poster_ip` varchar(39) DEFAULT NULL,
  `poster_email` varchar(80) DEFAULT NULL,
  `message` text,
  `hide_smilies` tinyint(1) NOT NULL DEFAULT '0',
  `posted` int(10) unsigned NOT NULL DEFAULT '0',
  `edited` int(10) unsigned DEFAULT NULL,
  `edited_by` varchar(200) DEFAULT NULL,
  `topic_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `posts_topic_id_idx` (`topic_id`),
  KEY `posts_multi_idx` (`poster_id`,`topic_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE  `moparclassic`.`ranks` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `rank` varchar(50) NOT NULL DEFAULT '',
  `min_posts` mediumint(8) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

CREATE TABLE  `moparclassic`.`reports` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `post_id` int(10) unsigned NOT NULL DEFAULT '0',
  `topic_id` int(10) unsigned NOT NULL DEFAULT '0',
  `forum_id` int(10) unsigned NOT NULL DEFAULT '0',
  `reported_by` int(10) unsigned NOT NULL DEFAULT '0',
  `created` int(10) unsigned NOT NULL DEFAULT '0',
  `message` text,
  `zapped` int(10) unsigned DEFAULT NULL,
  `zapped_by` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `reports_zapped_idx` (`zapped`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
CREATE TABLE  `moparclassic`.`reputation` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `from_user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `time` int(10) unsigned NOT NULL DEFAULT '0',
  `post_id` int(10) unsigned NOT NULL DEFAULT '0',
  `reason` text NOT NULL,
  `rep_plus` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `rep_minus` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `topics_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `rep_post_id_idx` (`post_id`),
  KEY `rep_multi_user_id_idx` (`topics_id`,`from_user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=15940 DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`search_cache` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `ident` varchar(200) NOT NULL DEFAULT '',
  `search_data` mediumtext,
  PRIMARY KEY (`id`),
  KEY `search_cache_ident_idx` (`ident`(8))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
CREATE TABLE  `moparclassic`.`search_matches` (
  `post_id` int(10) unsigned NOT NULL DEFAULT '0',
  `word_id` int(10) unsigned NOT NULL DEFAULT '0',
  `subject_match` tinyint(1) NOT NULL DEFAULT '0',
  KEY `search_matches_word_id_idx` (`word_id`),
  KEY `search_matches_post_id_idx` (`post_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE  `moparclassic`.`search_words` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `word` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`word`),
  KEY `search_words_id_idx` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

CREATE TABLE  `moparclassic`.`stats` (
  `date` int(10) unsigned NOT NULL DEFAULT '0',
  `posts` varchar(255) NOT NULL DEFAULT '',
  `users` varchar(255) NOT NULL DEFAULT '',
  `players` varchar(255) NOT NULL DEFAULT '',
  `active_users` varchar(255) NOT NULL DEFAULT '',
  `active_players` varchar(255) NOT NULL DEFAULT '',
  `topics` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`stats_2` (
  `logins` int(5) DEFAULT NULL,
  `unique_logins` int(5) DEFAULT NULL,
  `updated` int(11) DEFAULT NULL,
  `hits` int(5) DEFAULT NULL,
  `unique_hits` int(5) DEFAULT NULL,
  `alexa_rank` int(5) DEFAULT NULL,
  `kills` int(5) DEFAULT NULL,
  `total_kills` int(5) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`subs` (
  `user_id` int(10) DEFAULT NULL,
  `months` int(10) DEFAULT NULL,
  `google_no` varchar(255) DEFAULT NULL,
  `redeem` int(10) DEFAULT NULL,
  `status` int(5) DEFAULT NULL,
  `order_time` int(10) DEFAULT NULL,
  `last_time` int(10) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
CREATE TABLE  `moparclassic`.`subscriptions` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `topic_id` int(10) unsigned NOT NULL DEFAULT '0',
  `forum_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`topic_id`,`forum_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
CREATE TABLE  `moparclassic`.`topics` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `poster` varchar(200) NOT NULL DEFAULT '',
  `subject` varchar(255) NOT NULL DEFAULT '',
  `posted` int(10) unsigned NOT NULL DEFAULT '0',
  `first_post_id` int(10) unsigned NOT NULL DEFAULT '0',
  `last_post` int(10) unsigned NOT NULL DEFAULT '0',
  `last_post_id` int(10) unsigned NOT NULL DEFAULT '0',
  `last_poster` varchar(200) DEFAULT NULL,
  `num_views` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `num_replies` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `closed` tinyint(1) NOT NULL DEFAULT '0',
  `sticky` tinyint(1) NOT NULL DEFAULT '0',
  `moved_to` int(10) unsigned DEFAULT NULL,
  `forum_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `topics_forum_id_idx` (`forum_id`),
  KEY `topics_moved_to_idx` (`moved_to`),
  KEY `topics_last_post_idx` (`last_post`),
  KEY `topics_first_post_id_idx` (`first_post_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE  `moparclassic`.`users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` int(10) unsigned NOT NULL DEFAULT '3',
  `username` varchar(200) NOT NULL DEFAULT '',
  `password` varchar(40) NOT NULL DEFAULT '',
  `email` varchar(80) NOT NULL DEFAULT '',
  `title` varchar(50) DEFAULT NULL,
  `realname` varchar(40) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  `jabber` varchar(80) DEFAULT NULL,
  `icq` varchar(12) DEFAULT NULL,
  `msn` varchar(80) DEFAULT NULL,
  `aim` varchar(30) DEFAULT NULL,
  `yahoo` varchar(30) DEFAULT NULL,
  `location` varchar(30) DEFAULT NULL,
  `signature` text,
  `disp_topics` tinyint(3) unsigned DEFAULT NULL,
  `disp_posts` tinyint(3) unsigned DEFAULT NULL,
  `email_setting` tinyint(1) NOT NULL DEFAULT '1',
  `notify_with_post` tinyint(1) NOT NULL DEFAULT '0',
  `auto_notify` tinyint(1) NOT NULL DEFAULT '0',
  `show_smilies` tinyint(1) NOT NULL DEFAULT '1',
  `show_img` tinyint(1) NOT NULL DEFAULT '1',
  `show_img_sig` tinyint(1) NOT NULL DEFAULT '1',
  `show_avatars` tinyint(1) NOT NULL DEFAULT '1',
  `show_sig` tinyint(1) NOT NULL DEFAULT '1',
  `timezone` float NOT NULL DEFAULT '0',
  `dst` tinyint(1) NOT NULL DEFAULT '0',
  `time_format` tinyint(1) NOT NULL DEFAULT '0',
  `date_format` tinyint(1) NOT NULL DEFAULT '0',
  `language` varchar(25) NOT NULL DEFAULT 'English',
  `style` varchar(25) NOT NULL DEFAULT 'Air',
  `num_posts` int(10) unsigned NOT NULL DEFAULT '0',
  `last_post` int(10) unsigned DEFAULT NULL,
  `last_search` int(10) unsigned DEFAULT NULL,
  `last_email_sent` int(10) unsigned DEFAULT NULL,
  `registered` int(10) unsigned NOT NULL DEFAULT '0',
  `registration_ip` varchar(39) NOT NULL DEFAULT '0.0.0.0',
  `last_visit` int(10) unsigned NOT NULL DEFAULT '0',
  `admin_note` varchar(30) DEFAULT NULL,
  `activate_string` varchar(80) DEFAULT NULL,
  `activate_key` varchar(8) DEFAULT NULL,
  `sub_expires` int(10) unsigned DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_username_idx` (`username`(25)),
  KEY `users_registered_idx` (`registered`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
