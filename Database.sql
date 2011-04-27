-- phpMyAdmin SQL Dump
-- version 3.1.3.1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Oct 21, 2010 at 03:03 AM
-- Server version: 5.1.33
-- PHP Version: 5.2.9

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `pk`
--

-- --------------------------------------------------------

--
-- Table structure for table `bans`
--

CREATE TABLE IF NOT EXISTS `bans` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(200) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `email` varchar(80) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `expire` int(10) unsigned DEFAULT NULL,
  `ban_creator` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `bans_username_idx` (`username`(25))
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Dumping data for table `bans`
--


-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE IF NOT EXISTS `categories` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `cat_name` varchar(80) NOT NULL DEFAULT 'New Category',
  `disp_position` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `cat_name`, `disp_position`) VALUES
(1, 'Test category', 1);

-- --------------------------------------------------------

--
-- Table structure for table `censoring`
--

CREATE TABLE IF NOT EXISTS `censoring` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `search_for` varchar(60) NOT NULL DEFAULT '',
  `replace_with` varchar(60) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Dumping data for table `censoring`
--


-- --------------------------------------------------------

--
-- Table structure for table `config`
--

CREATE TABLE IF NOT EXISTS `config` (
  `conf_name` varchar(255) NOT NULL DEFAULT '',
  `conf_value` text,
  PRIMARY KEY (`conf_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `config`
--

INSERT INTO `config` (`conf_name`, `conf_value`) VALUES
('o_cur_version', '1.4.1'),
('o_database_revision', '7'),
('o_searchindex_revision', '1'),
('o_parser_revision', '1'),
('o_board_title', 'My FluxBB forum'),
('o_board_desc', '<p><span>Unfortunately no one can be told what FluxBB is - you have to see it for yourself.</span></p>'),
('o_default_timezone', '0'),
('o_time_format', 'H:i:s'),
('o_date_format', 'Y-m-d'),
('o_timeout_visit', '1800'),
('o_timeout_online', '300'),
('o_redirect_delay', '1'),
('o_show_version', '0'),
('o_show_user_info', '1'),
('o_show_post_count', '1'),
('o_signatures', '1'),
('o_smilies', '1'),
('o_smilies_sig', '1'),
('o_make_links', '1'),
('o_default_lang', 'English'),
('o_default_style', 'Air'),
('o_default_user_group', '4'),
('o_topic_review', '15'),
('o_disp_topics_default', '30'),
('o_disp_posts_default', '25'),
('o_indent_num_spaces', '4'),
('o_quote_depth', '3'),
('o_quickpost', '1'),
('o_users_online', '1'),
('o_censoring', '0'),
('o_ranks', '1'),
('o_show_dot', '0'),
('o_topic_views', '1'),
('o_quickjump', '1'),
('o_gzip', '0'),
('o_additional_navlinks', ''),
('o_report_method', '0'),
('o_regs_report', '0'),
('o_default_email_setting', '1'),
('o_mailing_list', 'crazyonedude@hotmail.com'),
('o_avatars', '1'),
('o_avatars_dir', 'img/avatars'),
('o_avatars_width', '60'),
('o_avatars_height', '60'),
('o_avatars_size', '10240'),
('o_search_all_forums', '1'),
('o_base_url', 'http://localhost'),
('o_admin_email', 'crazyonedude@hotmail.com'),
('o_webmaster_email', 'crazyonedude@hotmail.com'),
('o_subscriptions', '1'),
('o_smtp_host', NULL),
('o_smtp_user', NULL),
('o_smtp_pass', NULL),
('o_smtp_ssl', '0'),
('o_regs_allow', '1'),
('o_regs_verify', '0'),
('o_announcement', '0'),
('o_announcement_message', 'Enter your announcement here.'),
('o_rules', '0'),
('o_rules_message', 'Enter your rules here.'),
('o_maintenance', '0'),
('o_maintenance_message', 'The forums are temporarily down for maintenance. Please try again in a few minutes.<br />\n<br />\n/Administrator'),
('o_default_dst', '0'),
('o_feed_type', '2'),
('p_message_bbcode', '1'),
('p_message_img_tag', '1'),
('p_message_all_caps', '1'),
('p_subject_all_caps', '1'),
('p_sig_all_caps', '1'),
('p_sig_bbcode', '1'),
('p_sig_img_tag', '0'),
('p_sig_length', '400'),
('p_sig_lines', '4'),
('p_allow_banned_email', '1'),
('p_allow_dupe_email', '0'),
('p_force_guest_email', '1');

-- --------------------------------------------------------

--
-- Table structure for table `drops`
--

CREATE TABLE IF NOT EXISTS `drops` (
  `id` int(11) DEFAULT NULL,
  `item` varchar(255) DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  `weight` int(11) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `drops`
--


-- --------------------------------------------------------

--
-- Table structure for table `dupe_data`
--

CREATE TABLE IF NOT EXISTS `dupe_data` (
  `user` varchar(255) NOT NULL,
  `userhash` varchar(255) NOT NULL,
  `string` text NOT NULL,
  `time` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `dupe_data`
--

INSERT INTO `dupe_data` (`user`, `userhash`, `string`, `time`) VALUES
('Laserline', '42293815201319', '1287525847 Laserline picked up an item Party Hat (577) amount: 1 at: 216/442|216/442\n', '1287525847506');

-- --------------------------------------------------------

--
-- Table structure for table `error_reports`
--

CREATE TABLE IF NOT EXISTS `error_reports` (
  `data` text,
  `email` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `unix` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `error_reports`
--


-- --------------------------------------------------------

--
-- Table structure for table `forums`
--

CREATE TABLE IF NOT EXISTS `forums` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `forums`
--

INSERT INTO `forums` (`id`, `forum_name`, `forum_desc`, `redirect_url`, `moderators`, `num_topics`, `num_posts`, `last_post`, `last_post_id`, `last_poster`, `sort_by`, `disp_position`, `cat_id`) VALUES
(1, 'Test forum', 'This is just a test forum', NULL, NULL, 1, 1, 1281311594, 1, 'admin', 0, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `forum_perms`
--

CREATE TABLE IF NOT EXISTS `forum_perms` (
  `group_id` int(10) NOT NULL DEFAULT '0',
  `forum_id` int(10) NOT NULL DEFAULT '0',
  `read_forum` tinyint(1) NOT NULL DEFAULT '1',
  `post_replies` tinyint(1) NOT NULL DEFAULT '1',
  `post_topics` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`group_id`,`forum_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `forum_perms`
--


-- --------------------------------------------------------

--
-- Table structure for table `gp_count`
--

CREATE TABLE IF NOT EXISTS `gp_count` (
  `unixtime` varchar(255) DEFAULT NULL,
  `amount` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `gp_count`
--


-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE IF NOT EXISTS `groups` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `groups`
--

INSERT INTO `groups` (`g_id`, `g_title`, `g_user_title`, `g_moderator`, `g_mod_edit_users`, `g_mod_rename_users`, `g_mod_change_passwords`, `g_mod_ban_users`, `g_read_board`, `g_view_users`, `g_post_replies`, `g_post_topics`, `g_edit_posts`, `g_delete_posts`, `g_delete_topics`, `g_set_title`, `g_search`, `g_search_users`, `g_send_email`, `g_post_flood`, `g_search_flood`, `g_email_flood`) VALUES
(1, 'Administrators', 'Administrator', 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0),
(2, 'Moderators', 'Moderator', 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0),
(3, 'Guest', NULL, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 60, 30, 0),
(4, 'Members', NULL, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 60, 30, 60);

-- --------------------------------------------------------

--
-- Table structure for table `invites`
--

CREATE TABLE IF NOT EXISTS `invites` (
  `owner` varchar(255) NOT NULL DEFAULT '',
  `code` varchar(255) NOT NULL DEFAULT '',
  `time` int(10) unsigned NOT NULL DEFAULT '0',
  `invites` varchar(255) NOT NULL DEFAULT '1',
  `email` varchar(255) NOT NULL DEFAULT ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `invites`
--


-- --------------------------------------------------------

--
-- Table structure for table `irc_online`
--

CREATE TABLE IF NOT EXISTS `irc_online` (
  `username` varchar(255) NOT NULL,
  `rank` int(1) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `irc_online`
--


-- --------------------------------------------------------

--
-- Table structure for table `irc_stats`
--

CREATE TABLE IF NOT EXISTS `irc_stats` (
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

--
-- Dumping data for table `irc_stats`
--


-- --------------------------------------------------------

--
-- Table structure for table `items`
--

CREATE TABLE IF NOT EXISTS `items` (
  `id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `stackable` varchar(255) DEFAULT NULL,
  `wieldable` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `items`
--


-- --------------------------------------------------------

--
-- Table structure for table `logins`
--

CREATE TABLE IF NOT EXISTS `logins` (
  `id` int(10) DEFAULT NULL,
  `login_ip` varchar(15) DEFAULT NULL,
  `time` int(10) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `logins`
--


-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE IF NOT EXISTS `messages` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=23964 ;

--
-- Dumping data for table `messages`
--


-- --------------------------------------------------------

--
-- Table structure for table `online`
--

CREATE TABLE IF NOT EXISTS `online` (
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

--
-- Dumping data for table `online`
--

INSERT INTO `online` (`user_id`, `ident`, `logged`, `idle`, `last_post`, `last_search`) VALUES
(3, 'admin', 1287651758, 0, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `online_count`
--

CREATE TABLE IF NOT EXISTS `online_count` (
  `unixtime` varchar(255) DEFAULT NULL,
  `online` int(20) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `online_count`
--


-- --------------------------------------------------------

--
-- Table structure for table `pk_bank`
--

CREATE TABLE IF NOT EXISTS `pk_bank` (
  `user` varchar(255) DEFAULT NULL,
  `id` int(10) unsigned NOT NULL,
  `amount` int(10) unsigned NOT NULL DEFAULT '1',
  `slot` int(5) unsigned NOT NULL,
  KEY `user` (`user`),
  KEY `id` (`id`),
  KEY `amount` (`amount`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pk_bank`
--

INSERT INTO `pk_bank` (`user`, `id`, `amount`, `slot`) VALUES
('231514', 376, 1, 5),
('221303401880', 4, 65929216, 0),
('231514', 70, 1, 4),
('231514', 4, 1, 3),
('231514', 156, 1, 2),
('231514', 1263, 2, 1),
('231514', 87, 1, 0),
('1605225260', 10, 65929216, 0),
('1222846212', 10, 65929216, 0),
('500766777', 10, 65929216, 0),
('1326000417', 10, 65929216, 0),
('429496599', 256, 82771968, 2),
('429496599', 0, 1291845632, 1),
('429496599', 316, 1, 0),
('429496599', 1, 256, 3);

-- --------------------------------------------------------

--
-- Table structure for table `pk_banlog`
--

CREATE TABLE IF NOT EXISTS `pk_banlog` (
  `user` varchar(255) DEFAULT NULL,
  `staff` varchar(255) DEFAULT NULL,
  `time` int(10) DEFAULT NULL,
  KEY `staff` (`staff`),
  KEY `time` (`time`),
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pk_banlog`
--


-- --------------------------------------------------------

--
-- Table structure for table `pk_curstats`
--

CREATE TABLE IF NOT EXISTS `pk_curstats` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=350943 ;

--
-- Dumping data for table `pk_curstats`
--

INSERT INTO `pk_curstats` (`user`, `cur_attack`, `cur_defense`, `cur_strength`, `cur_hits`, `cur_ranged`, `cur_prayer`, `cur_magic`, `cur_cooking`, `cur_woodcut`, `cur_fletching`, `cur_fishing`, `cur_firemaking`, `cur_crafting`, `cur_smithing`, `cur_mining`, `cur_herblaw`, `cur_agility`, `cur_thieving`, `id`) VALUES
('105157704', 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350908),
('59712851', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350917),
('231514', 99, 99, 99, 99, 99, 78, 99, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350910),
('1286614266', 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350922),
('1421652498', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350923),
('500766777', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350924),
('568285893', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350925),
('500766777', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350926),
('568285893', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350927),
('105157704', 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350928),
('521970', 1, 1, 1, 8, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350929),
('42293815201319', 1, 1, 1, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350930),
('42293815201319', 1, 1, 1, 6, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350931),
('105157704', 1, 1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350932),
('429496599', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350933),
('497015715', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350934),
('37763264', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350935),
('37763265', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350936),
('37763266', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350937),
('37763267', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350938),
('37763268', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350939),
('37763269', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350940),
('37763270', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350941),
('37763271', 1, 1, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 350942);

-- --------------------------------------------------------

--
-- Table structure for table `pk_experience`
--

CREATE TABLE IF NOT EXISTS `pk_experience` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=350943 ;

--
-- Dumping data for table `pk_experience`
--

INSERT INTO `pk_experience` (`user`, `exp_attack`, `exp_defense`, `exp_strength`, `exp_hits`, `exp_ranged`, `exp_prayer`, `exp_magic`, `exp_cooking`, `exp_woodcut`, `exp_fletching`, `exp_fishing`, `exp_firemaking`, `exp_crafting`, `exp_smithing`, `exp_mining`, `exp_herblaw`, `exp_agility`, `exp_thieving`, `id`, `oo_attack`, `oo_defense`, `oo_strength`, `oo_ranged`, `oo_prayer`, `oo_magic`, `oo_cooking`, `oo_woodcut`, `oo_fletching`, `oo_fishing`, `oo_firemaking`, `oo_crafting`, `oo_smithing`, `oo_mining`, `oo_herblaw`, `oo_agility`, `oo_thieving`, `oo_hits`) VALUES
('500766777', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350926, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('105157704', 0, 0, 0, 1200, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350908, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('59712851', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350917, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('231514', 135000000, 135000000, 135000000, 135000000, 135000000, 135000000, 135000000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350910, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('1286614266', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350922, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('1421652498', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350923, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('500766777', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350924, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('568285893', 0, 0, 0, 1200, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350925, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('568285893', 0, 0, 0, 1200, 0, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350927, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('105157704', 0, 0, 0, 1200, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350928, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('521970', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350929, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('42293815201319', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350930, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('42293815201319', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350931, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('105157704', 0, 0, 0, 1200, 0, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350932, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('429496599', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350933, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('497015715', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350934, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('37763264', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350935, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('37763265', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350936, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('37763266', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350937, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('37763267', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350938, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('37763268', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350939, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('37763269', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350940, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('37763270', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350941, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
('37763271', 0, 0, 0, 1200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 350942, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `pk_friends`
--

CREATE TABLE IF NOT EXISTS `pk_friends` (
  `user` varchar(255) NOT NULL,
  `friend` varchar(255) NOT NULL,
  KEY `user` (`user`),
  KEY `friend` (`friend`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pk_friends`
--


-- --------------------------------------------------------

--
-- Table structure for table `pk_ignores`
--

CREATE TABLE IF NOT EXISTS `pk_ignores` (
  `user` varchar(255) NOT NULL,
  `ignore` varchar(255) NOT NULL,
  KEY `ignore` (`ignore`),
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pk_ignores`
--


-- --------------------------------------------------------

--
-- Table structure for table `pk_invitems`
--

CREATE TABLE IF NOT EXISTS `pk_invitems` (
  `user` varchar(255) NOT NULL,
  `id` int(10) unsigned NOT NULL,
  `amount` int(10) unsigned NOT NULL DEFAULT '1',
  `wielded` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `slot` int(5) unsigned NOT NULL,
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pk_invitems`
--

INSERT INTO `pk_invitems` (`user`, `id`, `amount`, `wielded`, `slot`) VALUES
('59712851', 70, 1, 0, 5),
('59712851', 4, 1, 0, 4),
('59712851', 1263, 1, 0, 3),
('59712851', 1006, 1, 0, 0),
('59712851', 316, 1, 1, 1),
('59712851', 77, 1, 1, 2),
('1286614266', 156, 1, 0, 7),
('59712851', 376, 1, 0, 6),
('1286614266', 376, 1, 0, 6),
('231514', 31, 6, 0, 25),
('231514', 42, 1, 0, 24),
('231514', 190, 414, 0, 23),
('231514', 60, 1, 1, 22),
('231514', 400, 1, 1, 21),
('231514', 402, 1, 1, 20),
('231514', 401, 1, 0, 19),
('231514', 81, 1, 0, 18),
('231514', 828, 1, 1, 17),
('231514', 1313, 1, 0, 16),
('231514', 1314, 1, 0, 15),
('231514', 1315, 1, 0, 14),
('231514', 1316, 1, 0, 13),
('231514', 1317, 1, 0, 12),
('231514', 1319, 1, 0, 11),
('231514', 1320, 1, 0, 10),
('231514', 1312, 1, 0, 9),
('231514', 1310, 1, 0, 8),
('231514', 1299, 1, 0, 7),
('231514', 1280, 1, 0, 6),
('231514', 1250, 1, 0, 5),
('231514', 1200, 1, 0, 4),
('231514', 16, 1, 1, 3),
('231514', 77, 1, 0, 2),
('231514', 316, 1, 1, 1),
('231514', 1006, 1, 0, 0),
('1286614266', 70, 1, 0, 5),
('1421652498', 1263, 1, 0, 9),
('1421652498', 87, 1, 0, 8),
('1421652498', 156, 1, 0, 7),
('1421652498', 376, 1, 0, 6),
('1421652498', 70, 1, 0, 5),
('1421652498', 4, 1, 0, 4),
('59712851', 156, 1, 0, 7),
('59712851', 87, 1, 0, 8),
('59712851', 1263, 1, 0, 9),
('1286614266', 4, 1, 0, 4),
('1286614266', 1263, 1, 0, 3),
('1286614266', 77, 1, 1, 2),
('1286614266', 1006, 1, 0, 0),
('1286614266', 316, 1, 1, 1),
('1421652498', 1263, 1, 0, 3),
('1421652498', 77, 1, 0, 2),
('1421652498', 316, 1, 0, 1),
('1421652498', 1006, 1, 0, 0),
('1286614266', 87, 1, 0, 8),
('1286614266', 1263, 1, 0, 9),
('521970', 1263, 1, 0, 9),
('521970', 87, 1, 0, 8),
('105157704', 1263, 1, 0, 13),
('521970', 156, 1, 0, 7),
('105157704', 87, 1, 0, 12),
('105157704', 376, 1, 0, 10),
('521970', 376, 1, 0, 6),
('521970', 70, 1, 0, 5),
('521970', 4, 1, 0, 4),
('521970', 77, 1, 1, 2),
('521970', 1263, 1, 0, 3),
('521970', 316, 1, 1, 1),
('521970', 1006, 1, 0, 0),
('500766777', 77, 1, 0, 1),
('500766777', 316, 1, 0, 0),
('568285893', 77, 1, 1, 2),
('568285893', 316, 1, 0, 1),
('568285893', 316, 1, 1, 0),
('521970', 233, 1, 0, 10),
('37763269', 316, 1, 0, 1),
('37763269', 1006, 1, 0, 0),
('37763268', 1263, 1, 0, 3),
('37763268', 77, 1, 0, 2),
('37763268', 316, 1, 0, 1),
('37763268', 1006, 1, 0, 0),
('37763267', 1263, 1, 0, 3),
('37763267', 77, 1, 0, 2),
('37763267', 316, 1, 0, 1),
('37763267', 1006, 1, 0, 0),
('497015715', 1263, 1, 0, 3),
('497015715', 77, 1, 0, 2),
('497015715', 316, 1, 0, 1),
('497015715', 1006, 1, 0, 0),
('37763266', 1263, 1, 0, 3),
('37763266', 77, 1, 0, 2),
('37763266', 316, 1, 0, 1),
('37763266', 1006, 1, 0, 0),
('37763265', 1263, 1, 0, 3),
('37763265', 77, 1, 0, 2),
('37763265', 316, 1, 0, 1),
('37763265', 1006, 1, 0, 0),
('37763264', 1263, 1, 0, 3),
('37763264', 77, 1, 0, 2),
('37763264', 316, 1, 0, 1),
('37763264', 1006, 1, 0, 0),
('105157704', 156, 1, 0, 11),
('105157704', 70, 1, 0, 9),
('105157704', 4, 1, 0, 8),
('105157704', 1263, 1, 0, 7),
('105157704', 1263, 1, 0, 6),
('105157704', 77, 1, 0, 5),
('105157704', 77, 1, 1, 4),
('105157704', 316, 1, 1, 3),
('105157704', 316, 1, 0, 2),
('105157704', 1006, 1, 0, 1),
('105157704', 1006, 1, 0, 0),
('37763269', 77, 1, 0, 2),
('37763269', 1263, 1, 0, 3),
('37763270', 1006, 1, 0, 0),
('37763270', 316, 1, 0, 1),
('37763270', 77, 1, 0, 2),
('37763270', 1263, 1, 0, 3),
('37763271', 1006, 1, 0, 0),
('37763271', 316, 1, 0, 1),
('37763271', 77, 1, 0, 2),
('37763271', 1263, 1, 0, 3);

-- --------------------------------------------------------

--
-- Table structure for table `pk_kills`
--

CREATE TABLE IF NOT EXISTS `pk_kills` (
  `user` varchar(255) NOT NULL,
  `type` tinyint(1) NOT NULL DEFAULT '0',
  `killed` varchar(45) NOT NULL,
  `time` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pk_kills`
--


-- --------------------------------------------------------

--
-- Table structure for table `pk_logins`
--

CREATE TABLE IF NOT EXISTS `pk_logins` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user` varchar(45) NOT NULL,
  `time` int(5) unsigned NOT NULL,
  `ip` varchar(15) NOT NULL DEFAULT '0.0.0.0',
  PRIMARY KEY (`id`),
  KEY `ip` (`ip`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=4254376 ;

--
-- Dumping data for table `pk_logins`
--

INSERT INTO `pk_logins` (`id`, `user`, `time`, `ip`) VALUES
(4254356, '42293815201319', 1287447911, '127.0.0.1'),
(4254355, '42293815201319', 1287447465, '127.0.0.1'),
(4254354, '521970', 1287446648, '127.0.0.1'),
(4254353, '500766777', 1287446338, '127.0.0.1'),
(4254352, '500766777', 1287445987, '127.0.0.1'),
(4254351, '568285893', 1287445883, '127.0.0.1'),
(4254350, '568285893', 1287445743, '127.0.0.1'),
(4254349, '568285893', 1287445535, '127.0.0.1'),
(4254348, '568285893', 1287445503, '127.0.0.1'),
(4254347, '500766777', 1287445443, '127.0.0.1'),
(4254346, '568285893', 1287444890, '127.0.0.1'),
(4254345, '568285893', 1287441310, '127.0.0.1'),
(4254344, '500766777', 1287441301, '127.0.0.1'),
(4254233, '105157704', 1281662336, '127.0.0.1'),
(4254234, '105157704', 1281662511, '127.0.0.1'),
(4254343, '500766777', 1287441293, '127.0.0.1'),
(4254342, '500766777', 1287441108, '127.0.0.1'),
(4254340, '500766777', 1287441053, '127.0.0.1'),
(4254339, '500766777', 1287441038, '127.0.0.1'),
(4254338, '1286614266', 1287125707, '127.0.0.1'),
(4254241, '105157704', 1281739696, '127.0.0.1'),
(4254337, '1421652498', 1287125695, '127.0.0.1'),
(4254323, '59712851', 1287037576, '127.0.0.1'),
(4254244, '105157704', 1281740520, '127.0.0.1'),
(4254245, '105157704', 1281740583, '127.0.0.1'),
(4254246, '105157704', 1281743157, '127.0.0.1'),
(4254247, '105157704', 1281743738, '127.0.0.1'),
(4254248, '105157704', 1281744127, '127.0.0.1'),
(4254322, '59712851', 1287037523, '127.0.0.1'),
(4254250, '105157704', 1281753461, '127.0.0.1'),
(4254251, '105157704', 1281754704, '127.0.0.1'),
(4254252, '105157704', 1281759336, '127.0.0.1'),
(4254253, '105157704', 1281759553, '127.0.0.1'),
(4254254, '105157704', 1281760744, '127.0.0.1'),
(4254255, '105157704', 1281760871, '127.0.0.1'),
(4254256, '105157704', 1281761406, '127.0.0.1'),
(4254257, '105157704', 1281762136, '127.0.0.1'),
(4254258, '105157704', 1281762423, '127.0.0.1'),
(4254341, '500766777', 1287441068, '127.0.0.1'),
(4254260, '231514', 1286350050, '127.0.0.1'),
(4254261, '231514', 1286435056, '127.0.0.1'),
(4254262, '231514', 1286435308, '127.0.0.1'),
(4254263, '231514', 1286435408, '127.0.0.1'),
(4254264, '231514', 1286435473, '127.0.0.1'),
(4254265, '231514', 1286436753, '127.0.0.1'),
(4254266, '231514', 1286436991, '127.0.0.1'),
(4254267, '231514', 1286437238, '127.0.0.1'),
(4254268, '231514', 1286437254, '127.0.0.1'),
(4254269, '231514', 1286437536, '127.0.0.1'),
(4254270, '231514', 1286438257, '127.0.0.1'),
(4254271, '231514', 1286438763, '127.0.0.1'),
(4254272, '231514', 1286439108, '127.0.0.1'),
(4254273, '231514', 1286439379, '127.0.0.1'),
(4254274, '231514', 1286439501, '127.0.0.1'),
(4254275, '231514', 1286439561, '127.0.0.1'),
(4254276, '231514', 1286439618, '127.0.0.1'),
(4254277, '231514', 1286488134, '127.0.0.1'),
(4254278, '231514', 1286488616, '127.0.0.1'),
(4254279, '231514', 1286522176, '127.0.0.1'),
(4254280, '231514', 1286522684, '127.0.0.1'),
(4254281, '231514', 1286523279, '127.0.0.1'),
(4254282, '231514', 1286524053, '127.0.0.1'),
(4254283, '231514', 1286524518, '127.0.0.1'),
(4254284, '231514', 1286780517, '127.0.0.1'),
(4254285, '231514', 1286780555, '127.0.0.1'),
(4254286, '231514', 1286781216, '127.0.0.1'),
(4254287, '231514', 1286820475, '127.0.0.1'),
(4254288, '231514', 1286821247, '127.0.0.1'),
(4254289, '231514', 1286823183, '127.0.0.1'),
(4254290, '231514', 1286830609, '127.0.0.1'),
(4254291, '231514', 1286831465, '127.0.0.1'),
(4254292, '231514', 1286844955, '127.0.0.1'),
(4254293, '231514', 1286846802, '127.0.0.1'),
(4254294, '231514', 1286849133, '127.0.0.1'),
(4254295, '231514', 1286851625, '127.0.0.1'),
(4254296, '231514', 1286852351, '127.0.0.1'),
(4254297, '231514', 1286854781, '127.0.0.1'),
(4254298, '231514', 1286856008, '127.0.0.1'),
(4254299, '231514', 1286856367, '127.0.0.1'),
(4254300, '231514', 1286903269, '127.0.0.1'),
(4254301, '231514', 1286903505, '127.0.0.1'),
(4254302, '231514', 1286903854, '127.0.0.1'),
(4254303, '231514', 1286904045, '127.0.0.1'),
(4254304, '231514', 1286904550, '127.0.0.1'),
(4254305, '231514', 1286905019, '127.0.0.1'),
(4254306, '231514', 1286905613, '127.0.0.1'),
(4254307, '231514', 1286905839, '127.0.0.1'),
(4254308, '231514', 1286912978, '127.0.0.1'),
(4254309, '231514', 1286913900, '127.0.0.1'),
(4254310, '231514', 1286934357, '127.0.0.1'),
(4254311, '231514', 1287022663, '127.0.0.1'),
(4254336, '1421652498', 1287125688, '127.0.0.1'),
(4254335, '1421652498', 1287125677, '127.0.0.1'),
(4254331, '1421652498', 1287125644, '127.0.0.1'),
(4254332, '1421652498', 1287125659, '127.0.0.1'),
(4254333, '1421652498', 1287125665, '127.0.0.1'),
(4254334, '1421652498', 1287125672, '127.0.0.1'),
(4254321, '231514', 1287037264, '127.0.0.1'),
(4254357, '42293815201319', 1287450195, '127.0.0.1'),
(4254358, '42293815201319', 1287450615, '127.0.0.1'),
(4254359, '42293815201319', 1287450712, '127.0.0.1'),
(4254360, '105157704', 1287450793, '127.0.0.1'),
(4254361, '105157704', 1287450883, '127.0.0.1'),
(4254362, '105157704', 1287450891, '127.0.0.1'),
(4254363, '105157704', 1287451143, '127.0.0.1'),
(4254364, '105157704', 1287452885, '127.0.0.1'),
(4254365, '105157704', 1287453742, '127.0.0.1'),
(4254366, '105157704', 1287454415, '127.0.0.1'),
(4254367, '42293815201319', 1287454430, '127.0.0.1'),
(4254368, '42293815201319', 1287454553, '127.0.0.1'),
(4254369, '42293815201319', 1287462434, '127.0.0.1'),
(4254370, '42293815201319', 1287466598, '127.0.0.1'),
(4254371, '42293815201319', 1287467029, '127.0.0.1'),
(4254372, '42293815201319', 1287470365, '127.0.0.1'),
(4254373, '429496599', 1287525081, '127.0.0.1'),
(4254374, '429496599', 1287525445, '127.0.0.1'),
(4254375, '42293815201319', 1287525773, '127.0.0.1');

-- --------------------------------------------------------

--
-- Table structure for table `pk_market`
--

CREATE TABLE IF NOT EXISTS `pk_market` (
  `owner` int(10) NOT NULL,
  `item_id` int(10) NOT NULL,
  `amount` int(10) NOT NULL,
  `selling_price` int(10) NOT NULL,
  PRIMARY KEY (`owner`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pk_market`
--


-- --------------------------------------------------------

--
-- Table structure for table `pk_mutelog`
--

CREATE TABLE IF NOT EXISTS `pk_mutelog` (
  `user` varchar(255) NOT NULL,
  `staff` varchar(255) NOT NULL,
  `time` int(11) NOT NULL,
  `report_id` int(11) NOT NULL,
  `duration` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pk_mutelog`
--


-- --------------------------------------------------------

--
-- Table structure for table `pk_online`
--

CREATE TABLE IF NOT EXISTS `pk_online` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user` varchar(45) NOT NULL,
  `username` varchar(45) NOT NULL,
  `x` varchar(45) NOT NULL,
  `y` varchar(45) NOT NULL,
  `world` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='InnoDB free: 9216 kB' AUTO_INCREMENT=1 ;

--
-- Dumping data for table `pk_online`
--


-- --------------------------------------------------------

--
-- Table structure for table `pk_players`
--

CREATE TABLE IF NOT EXISTS `pk_players` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 9216 kB' AUTO_INCREMENT=350944 ;

--
-- Dumping data for table `pk_players`
--

INSERT INTO `pk_players` (`user`, `username`, `pending_deletion`, `group_id`, `owner`, `owner_username`, `sub_expires`, `combat`, `skill_total`, `x`, `y`, `fatigue`, `combatstyle`, `block_chat`, `block_private`, `block_trade`, `block_duel`, `cameraauto`, `onemouse`, `soundoff`, `showroof`, `autoscreenshot`, `combatwindow`, `haircolour`, `topcolour`, `trousercolour`, `skincolour`, `headsprite`, `bodysprite`, `male`, `skulled`, `pass`, `creation_date`, `creation_ip`, `login_date`, `login_ip`, `playermod`, `loggedin`, `banned`, `muted`, `deaths`, `id`, `online`, `world`, `quest_points`, `eventcd`) VALUES
('37763271', 'test8', 0, 0, 3, NULL, 0, 3, 3, 213, 452, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 8, 14, 0, 1, 2, 1, 0, 'f5d1278e8109edd94e1e4197e04873b9', 1287651756, '127.0.0.1', 0, '0.0.0.0', 0, 0, 0, 0, 0, 350943, 0, 1, NULL, 0),
('37763270', 'test7', 0, 0, 3, NULL, 0, 3, 3, 213, 452, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 8, 14, 0, 1, 2, 1, 0, 'f5d1278e8109edd94e1e4197e04873b9', 1287651734, '127.0.0.1', 0, '0.0.0.0', 0, 0, 0, 0, 0, 350942, 0, 1, NULL, 0),
('37763269', 'test6', 0, 0, 3, NULL, 0, 3, 3, 213, 452, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 8, 14, 0, 1, 2, 1, 0, 'f5d1278e8109edd94e1e4197e04873b9', 1287651720, '127.0.0.1', 0, '0.0.0.0', 0, 0, 0, 0, 0, 350941, 0, 1, NULL, 0),
('37763267', 'test4', 0, 0, 3, NULL, 0, 3, 3, 213, 452, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 8, 14, 0, 1, 2, 1, 0, 'f5d1278e8109edd94e1e4197e04873b9', 1287651686, '127.0.0.1', 0, '0.0.0.0', 0, 0, 0, 0, 0, 350939, 0, 1, NULL, 0),
('37763268', 'test5', 0, 0, 3, NULL, 0, 3, 3, 213, 452, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 8, 14, 0, 1, 2, 1, 0, 'f5d1278e8109edd94e1e4197e04873b9', 1287651699, '127.0.0.1', 0, '0.0.0.0', 0, 0, 0, 0, 0, 350940, 0, 1, NULL, 0),
('37763264', 'test1', 0, 0, 3, NULL, 0, 3, 3, 213, 452, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 8, 14, 0, 1, 2, 1, 0, 'f5d1278e8109edd94e1e4197e04873b9', 1287651635, '127.0.0.1', 0, '0.0.0.0', 0, 0, 0, 0, 0, 350936, 0, 1, NULL, 0),
('37763265', 'test2', 0, 0, 3, NULL, 0, 3, 3, 213, 452, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 8, 14, 0, 1, 2, 1, 0, 'f5d1278e8109edd94e1e4197e04873b9', 1287651646, '127.0.0.1', 0, '0.0.0.0', 0, 0, 0, 0, 0, 350937, 0, 1, NULL, 0),
('37763266', 'test3', 0, 0, 3, NULL, 0, 3, 3, 213, 452, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 8, 14, 0, 1, 2, 1, 0, 'f5d1278e8109edd94e1e4197e04873b9', 1287651663, '127.0.0.1', 0, '0.0.0.0', 0, 0, 0, 0, 0, 350938, 0, 1, NULL, 0);

-- --------------------------------------------------------

--
-- Table structure for table `pk_quests`
--

CREATE TABLE IF NOT EXISTS `pk_quests` (
  `id` int(11) DEFAULT NULL,
  `stage` int(11) DEFAULT NULL,
  `user` varchar(255) DEFAULT NULL,
  KEY `user` (`user`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pk_quests`
--

INSERT INTO `pk_quests` (`id`, `stage`, `user`) VALUES
(10, -1, '231514'),
(10, -1, '2094917'),
(9, -1, '2094917'),
(8, -1, '2094917'),
(7, -1, '2094917'),
(3, -1, '2094917'),
(1, -1, '2094917'),
(0, -1, '2094917'),
(9, -1, '221303401880'),
(8, -1, '221303401880'),
(7, -1, '221303401880'),
(3, -1, '221303401880'),
(1, -1, '221303401880'),
(0, 0, '221303401880'),
(10, -1, '105157704'),
(9, -1, '105157704'),
(8, -1, '105157704'),
(7, -1, '105157704'),
(3, -1, '105157704'),
(10, -1, '221303401880'),
(1, -1, '105157704'),
(0, -1, '105157704'),
(9, -1, '231514'),
(8, -1, '231514'),
(7, -1, '231514'),
(3, -1, '231514'),
(1, -1, '231514'),
(0, -1, '231514'),
(0, -1, '1070928201'),
(1, -1, '1070928201'),
(3, -1, '1070928201'),
(7, -1, '1070928201'),
(8, -1, '1070928201'),
(9, -1, '1070928201'),
(10, -1, '1070928201'),
(0, -1, '59712851'),
(1, -1, '59712851'),
(3, -1, '59712851'),
(7, -1, '59712851'),
(8, -1, '59712851'),
(9, -1, '59712851'),
(10, -1, '59712851'),
(10, -1, '1434781215'),
(9, -1, '1434781215'),
(8, -1, '1434781215'),
(7, -1, '1434781215'),
(3, -1, '1434781215'),
(1, -1, '1434781215'),
(0, -1, '1434781215'),
(0, -1, '380732793'),
(1, -1, '380732793'),
(3, -1, '380732793'),
(7, -1, '380732793'),
(8, -1, '380732793'),
(9, -1, '380732793'),
(10, -1, '380732793'),
(0, -1, '1772376795'),
(1, -1, '1772376795'),
(3, -1, '1772376795'),
(7, -1, '1772376795'),
(8, -1, '1772376795'),
(9, -1, '1772376795'),
(10, -1, '1772376795'),
(0, -1, '1286614266'),
(10, -1, '1421652498'),
(9, -1, '1421652498'),
(8, -1, '1421652498'),
(7, -1, '1421652498'),
(3, -1, '1421652498'),
(1, -1, '1421652498'),
(0, -1, '1421652498'),
(1, -1, '1286614266'),
(3, -1, '1286614266'),
(7, -1, '1286614266'),
(8, -1, '1286614266'),
(9, -1, '1286614266'),
(10, -1, '1286614266'),
(0, -1, '521970'),
(10, -1, '500766777'),
(9, -1, '500766777'),
(8, -1, '500766777'),
(7, -1, '500766777'),
(3, -1, '500766777'),
(1, -1, '500766777'),
(0, -1, '500766777'),
(10, -1, '568285893'),
(9, -1, '568285893'),
(8, -1, '568285893'),
(7, -1, '568285893'),
(3, -1, '568285893'),
(1, -1, '568285893'),
(0, -1, '568285893'),
(1, -1, '521970'),
(3, -1, '521970'),
(7, -1, '521970'),
(8, -1, '521970'),
(9, -1, '521970'),
(10, -1, '521970'),
(10, -1, '42293815201319'),
(9, -1, '42293815201319'),
(8, -1, '42293815201319'),
(7, -1, '42293815201319'),
(3, -1, '42293815201319'),
(1, -1, '42293815201319'),
(0, -1, '42293815201319');

-- --------------------------------------------------------

--
-- Table structure for table `pk_reports`
--

CREATE TABLE IF NOT EXISTS `pk_reports` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4336 ;

--
-- Dumping data for table `pk_reports`
--

INSERT INTO `pk_reports` (`id`, `from`, `about`, `time`, `reason`, `snapshot_from`, `snapshot_about`, `chatlogs`, `from_x`, `from_y`, `about_x`, `about_y`, `zapped`, `zapped_by`, `sendToMod`) VALUES
(4335, '2094917', '26052', 1281515445, 1, '', '', '', 215, 505, 0, 0, NULL, NULL, 0);

-- --------------------------------------------------------

--
-- Table structure for table `pk_reports_comments`
--

CREATE TABLE IF NOT EXISTS `pk_reports_comments` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `report_id` int(10) NOT NULL,
  `poster` int(10) NOT NULL,
  `time` int(10) NOT NULL,
  `text` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4222 ;

--
-- Dumping data for table `pk_reports_comments`
--


-- --------------------------------------------------------

--
-- Table structure for table `pk_stat_reduction`
--

CREATE TABLE IF NOT EXISTS `pk_stat_reduction` (
  `user` int(10) NOT NULL,
  `account` varchar(255) NOT NULL,
  `account_hash` varchar(255) NOT NULL,
  `skill` int(10) NOT NULL,
  `voucher` varchar(255) DEFAULT NULL,
  `time` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `pk_stat_reduction`
--


-- --------------------------------------------------------

--
-- Table structure for table `pk_tradelog`
--

CREATE TABLE IF NOT EXISTS `pk_tradelog` (
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

--
-- Dumping data for table `pk_tradelog`
--

INSERT INTO `pk_tradelog` (`from`, `to`, `time`, `id`, `x`, `y`, `amount`, `type`) VALUES
('0', '42293815201319', 1287525847, 577, 216, 442, 1, 3);

-- --------------------------------------------------------

--
-- Table structure for table `pk_worlds`
--

CREATE TABLE IF NOT EXISTS `pk_worlds` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `location` varchar(45) NOT NULL,
  `ip` varchar(45) NOT NULL,
  `port` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC AUTO_INCREMENT=2 ;

--
-- Dumping data for table `pk_worlds`
--


-- --------------------------------------------------------

--
-- Table structure for table `posts`
--

CREATE TABLE IF NOT EXISTS `posts` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `posts`
--

INSERT INTO `posts` (`id`, `poster`, `poster_id`, `poster_ip`, `poster_email`, `message`, `hide_smilies`, `posted`, `edited`, `edited_by`, `topic_id`) VALUES
(1, 'admin', 2, '127.0.0.1', NULL, 'If you are looking at this (which I guess you are), the install of FluxBB appears to have worked! Now log in and head over to the administration control panel to configure your forum.', 0, 1281311594, NULL, NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `ranks`
--

CREATE TABLE IF NOT EXISTS `ranks` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `rank` varchar(50) NOT NULL DEFAULT '',
  `min_posts` mediumint(8) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `ranks`
--

INSERT INTO `ranks` (`id`, `rank`, `min_posts`) VALUES
(1, 'New member', 0),
(2, 'Member', 10);

-- --------------------------------------------------------

--
-- Table structure for table `reports`
--

CREATE TABLE IF NOT EXISTS `reports` (
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
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Dumping data for table `reports`
--


-- --------------------------------------------------------

--
-- Table structure for table `reputation`
--

CREATE TABLE IF NOT EXISTS `reputation` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=15940 ;

--
-- Dumping data for table `reputation`
--


-- --------------------------------------------------------

--
-- Table structure for table `search_cache`
--

CREATE TABLE IF NOT EXISTS `search_cache` (
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `ident` varchar(200) NOT NULL DEFAULT '',
  `search_data` mediumtext,
  PRIMARY KEY (`id`),
  KEY `search_cache_ident_idx` (`ident`(8))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `search_cache`
--


-- --------------------------------------------------------

--
-- Table structure for table `search_matches`
--

CREATE TABLE IF NOT EXISTS `search_matches` (
  `post_id` int(10) unsigned NOT NULL DEFAULT '0',
  `word_id` int(10) unsigned NOT NULL DEFAULT '0',
  `subject_match` tinyint(1) NOT NULL DEFAULT '0',
  KEY `search_matches_word_id_idx` (`word_id`),
  KEY `search_matches_post_id_idx` (`post_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `search_matches`
--

INSERT INTO `search_matches` (`post_id`, `word_id`, `subject_match`) VALUES
(1, 1, 0),
(1, 2, 0),
(1, 3, 0),
(1, 4, 0),
(1, 5, 0),
(1, 6, 0),
(1, 7, 0),
(1, 8, 0),
(1, 9, 0),
(1, 10, 0),
(1, 11, 0),
(1, 12, 0),
(1, 13, 0),
(1, 15, 1),
(1, 14, 1);

-- --------------------------------------------------------

--
-- Table structure for table `search_words`
--

CREATE TABLE IF NOT EXISTS `search_words` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `word` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`word`),
  KEY `search_words_id_idx` (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=16 ;

--
-- Dumping data for table `search_words`
--

INSERT INTO `search_words` (`id`, `word`) VALUES
(1, 'looking'),
(2, 'guess'),
(3, 'install'),
(4, 'fluxbb'),
(5, 'appears'),
(6, 'worked'),
(7, 'log'),
(8, 'head'),
(9, 'administration'),
(10, 'control'),
(11, 'panel'),
(12, 'configure'),
(13, 'forum'),
(14, 'test'),
(15, 'post');

-- --------------------------------------------------------

--
-- Table structure for table `stats`
--

CREATE TABLE IF NOT EXISTS `stats` (
  `date` int(10) unsigned NOT NULL DEFAULT '0',
  `posts` varchar(255) NOT NULL DEFAULT '',
  `users` varchar(255) NOT NULL DEFAULT '',
  `players` varchar(255) NOT NULL DEFAULT '',
  `active_users` varchar(255) NOT NULL DEFAULT '',
  `active_players` varchar(255) NOT NULL DEFAULT '',
  `topics` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `stats`
--


-- --------------------------------------------------------

--
-- Table structure for table `stats_2`
--

CREATE TABLE IF NOT EXISTS `stats_2` (
  `logins` int(5) DEFAULT NULL,
  `unique_logins` int(5) DEFAULT NULL,
  `updated` int(11) DEFAULT NULL,
  `hits` int(5) DEFAULT NULL,
  `unique_hits` int(5) DEFAULT NULL,
  `alexa_rank` int(5) DEFAULT NULL,
  `kills` int(5) DEFAULT NULL,
  `total_kills` int(5) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `stats_2`
--


-- --------------------------------------------------------

--
-- Table structure for table `subs`
--

CREATE TABLE IF NOT EXISTS `subs` (
  `user_id` int(10) DEFAULT NULL,
  `months` int(10) DEFAULT NULL,
  `google_no` varchar(255) DEFAULT NULL,
  `redeem` int(10) DEFAULT NULL,
  `status` int(5) DEFAULT NULL,
  `order_time` int(10) DEFAULT NULL,
  `last_time` int(10) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `subs`
--


-- --------------------------------------------------------

--
-- Table structure for table `subscriptions`
--

CREATE TABLE IF NOT EXISTS `subscriptions` (
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `topic_id` int(10) unsigned NOT NULL DEFAULT '0',
  `forum_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`,`topic_id`,`forum_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `subscriptions`
--


-- --------------------------------------------------------

--
-- Table structure for table `topics`
--

CREATE TABLE IF NOT EXISTS `topics` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `topics`
--

INSERT INTO `topics` (`id`, `poster`, `subject`, `posted`, `first_post_id`, `last_post`, `last_post_id`, `last_poster`, `num_views`, `num_replies`, `closed`, `sticky`, `moved_to`, `forum_id`) VALUES
(1, 'admin', 'Test post', 1281311594, 1, 1281311594, 1, 'admin', 0, 0, 0, 0, NULL, 1);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
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
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `group_id`, `username`, `password`, `email`, `title`, `realname`, `url`, `jabber`, `icq`, `msn`, `aim`, `yahoo`, `location`, `signature`, `disp_topics`, `disp_posts`, `email_setting`, `notify_with_post`, `auto_notify`, `show_smilies`, `show_img`, `show_img_sig`, `show_avatars`, `show_sig`, `timezone`, `dst`, `time_format`, `date_format`, `language`, `style`, `num_posts`, `last_post`, `last_search`, `last_email_sent`, `registered`, `registration_ip`, `last_visit`, `admin_note`, `activate_string`, `activate_key`, `sub_expires`) VALUES
(1, 3, 'Guest', 'Guest', 'Guest', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 'English', 'Air', 0, NULL, NULL, NULL, 0, '0.0.0.0', 0, NULL, NULL, NULL, 0),
(3, 4, 'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', 'crazyonedude@hotmail.com', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 'English', 'Air', 0, NULL, NULL, NULL, 1281384799, '127.0.0.1', 1287450782, NULL, NULL, NULL, 0);
