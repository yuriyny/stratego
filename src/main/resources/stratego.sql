-- phpMyAdmin SQL Dump
-- version 4.9.0.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Sep 21, 2019 at 06:43 AM
-- Server version: 10.4.6-MariaDB
-- PHP Version: 7.3.9

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `stratego`
--

-- --------------------------------------------------------

--
-- Table structure for table `Game`
--

CREATE TABLE `Game` (
  `GameID` int(11) NOT NULL,
  `UserInitSetup` int(11) NOT NULL,
  `AIInitSetup` int(11) NOT NULL,
  `GameHistoryID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `GameHistory`
--

CREATE TABLE `GameHistory` (
  `StepID` int(11) NOT NULL,
  `stepNumber` int(11) NOT NULL,
  `playerType` varchar(20) NOT NULL,
  `pieceName` varchar(20) NOT NULL,
  `position` varchar(20) NOT NULL,
  `gameID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `GameInitialSetup`
--

CREATE TABLE `GameInitialSetup` (
  `GameInitSetupID` int(20) NOT NULL,
  `1` varchar(10) NOT NULL,
  `2_1` varchar(10) NOT NULL,
  `2_2` varchar(10) NOT NULL,
  `2_3` varchar(10) NOT NULL,
  `2_4` varchar(10) NOT NULL,
  `2_5` varchar(10) NOT NULL,
  `2_6` varchar(10) NOT NULL,
  `2_7` varchar(10) NOT NULL,
  `2_8` varchar(10) NOT NULL,
  `3_1` varchar(10) NOT NULL,
  `3_2` varchar(10) NOT NULL,
  `3_3` varchar(10) NOT NULL,
  `3_4` varchar(10) NOT NULL,
  `3_5` varchar(10) NOT NULL,
  `4_1` varchar(10) NOT NULL,
  `4_2` varchar(10) NOT NULL,
  `4_3` varchar(10) NOT NULL,
  `4_4` varchar(10) NOT NULL,
  `5_1` varchar(10) NOT NULL,
  `5_2` varchar(10) NOT NULL,
  `5_3` varchar(10) NOT NULL,
  `5_4` varchar(10) NOT NULL,
  `6_1` varchar(10) NOT NULL,
  `6_2` varchar(10) NOT NULL,
  `6_3` varchar(10) NOT NULL,
  `6_4` varchar(10) NOT NULL,
  `7_1` varchar(10) NOT NULL,
  `7_2` varchar(10) NOT NULL,
  `7_3` varchar(10) NOT NULL,
  `8_1` varchar(10) NOT NULL,
  `8_2` varchar(10) NOT NULL,
  `9` varchar(10) NOT NULL,
  `10` varchar(10) NOT NULL,
  `bomb_1` varchar(10) NOT NULL,
  `bomb_2` varchar(10) NOT NULL,
  `bomb_3` varchar(10) NOT NULL,
  `bomb_4` varchar(10) NOT NULL,
  `bomb_5` varchar(10) NOT NULL,
  `bomb_6` varchar(10) NOT NULL,
  `flag` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `User`
--

CREATE TABLE `User` (
  `UserName` varchar(20) NOT NULL,
  `Password` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `UserGames`
--

CREATE TABLE `UserGames` (
  `UserGamesID` int(11) NOT NULL,
  `UserName` varchar(20) NOT NULL,
  `GameID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Game`
--
ALTER TABLE `Game`
  ADD PRIMARY KEY (`GameID`),
  ADD KEY `UserInitSetup` (`UserInitSetup`),
  ADD KEY `AIInitSetup` (`AIInitSetup`),
  ADD KEY `HistoryID` (`GameHistoryID`);

--
-- Indexes for table `GameHistory`
--
ALTER TABLE `GameHistory`
  ADD PRIMARY KEY (`StepID`),
  ADD KEY `gameID` (`gameID`);

--
-- Indexes for table `GameInitialSetup`
--
ALTER TABLE `GameInitialSetup`
  ADD PRIMARY KEY (`GameInitSetupID`),
  ADD UNIQUE KEY `1` (`1`);

--
-- Indexes for table `User`
--
ALTER TABLE `User`
  ADD PRIMARY KEY (`UserName`);

--
-- Indexes for table `UserGames`
--
ALTER TABLE `UserGames`
  ADD PRIMARY KEY (`UserGamesID`),
  ADD UNIQUE KEY `GameID` (`GameID`),
  ADD KEY `UserName` (`UserName`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Game`
--
ALTER TABLE `Game`
  ADD CONSTRAINT `Game_ibfk_1` FOREIGN KEY (`AIInitSetup`) REFERENCES `GameInitialSetup` (`GameInitSetupId`) ON UPDATE CASCADE,
  ADD CONSTRAINT `Game_ibfk_2` FOREIGN KEY (`UserInitSetup`) REFERENCES `GameInitialSetup` (`GameInitSetupId`) ON UPDATE CASCADE;

--
-- Constraints for table `GameHistory`
--
ALTER TABLE `GameHistory`
  ADD CONSTRAINT `GameHistory_ibfk_1` FOREIGN KEY (`gameID`) REFERENCES `Game` (`GameID`) ON UPDATE CASCADE;

--
-- Constraints for table `UserGames`
--
ALTER TABLE `UserGames`
  ADD CONSTRAINT `UserGames_ibfk_1` FOREIGN KEY (`UserName`) REFERENCES `User` (`UserName`) ON UPDATE CASCADE,
  ADD CONSTRAINT `UserGames_ibfk_2` FOREIGN KEY (`GameID`) REFERENCES `GameInitialSetup` (`GameInitSetupId`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
