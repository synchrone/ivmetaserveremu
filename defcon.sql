-- phpMyAdmin SQL Dump
-- version 3.3.4
-- http://www.phpmyadmin.net
--
-- Хост: localhost:3306
-- Время создания: Июл 18 2010 г., 01:30
-- Версия сервера: 5.1.48
-- Версия PHP: 5.3.2

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- База данных: `defcon`
--

-- --------------------------------------------------------

--
-- Структура таблицы `keys`
--

DROP TABLE IF EXISTS `keys`;
CREATE TABLE `keys` (
  `keyid` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(31) NOT NULL,
  PRIMARY KEY (`keyid`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Структура таблицы `servers`
--

DROP TABLE IF EXISTS `servers`;
CREATE TABLE `servers` (
  `name` text NOT NULL,
  `game` text NOT NULL,
  `version` text NOT NULL,
  `localip` text NOT NULL,
  `localport` int(4) NOT NULL,
  `playersOnline` int(1) NOT NULL,
  `dk` int(11) NOT NULL,
  `maxPlayers` int(1) NOT NULL,
  `spectatorsOnline` int(1) NOT NULL,
  `maxSpectators` int(1) NOT NULL,
  `gameType` int(1) NOT NULL,
  `scoreMode` int(1) NOT NULL,
  `timePlaying` int(11) NOT NULL,
  `gameStarted` tinyint(1) NOT NULL,
  `teamCount` int(1) NOT NULL,
  `globalPort` int(4) NOT NULL,
  `uniqueId` text NOT NULL,
  `lastUpdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uniqueId_unique` (`uniqueId`(31)),
  KEY `uniqueId_idx` (`uniqueId`(100))
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Структура таблицы `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `keyid` int(11) NOT NULL,
  `email` text NOT NULL,
  PRIMARY KEY (`keyid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
