CREATE database AgeRoyale;
use AgeRoyale;

CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) DEFAULT NULL,
  `password` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `isConnected` tinyint DEFAULT '0',
  `isBanned` tinyint DEFAULT '0',
  `banDate`  datetime DEFAULT NULL,
  `wins` int DEFAULT '0',
  `loses` int DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
);

CREATE TABLE `friendinvitations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `invitingUser` int NOT NULL,
  `invitedUser` int NOT NULL,
  `status` enum('Pending','Refuse','Accepted') DEFAULT NULL,
  `invitationDate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_invited_id` (`invitedUser`),
  KEY `fk_invitingUser_id` (`invitingUser`),
  CONSTRAINT `fk_invited_id` FOREIGN KEY (`invitedUser`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_invitingUser_id` FOREIGN KEY (`invitingUser`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `friendships` (
  `user1` int NOT NULL,
  `user2` int NOT NULL,
  `friendshipDate` datetime DEFAULT NULL,
  PRIMARY KEY (`user1`,`user2`),
  KEY `fk_frienships_user2_users_id` (`user2`),
  CONSTRAINT `fk_frienships_user1_users_id` FOREIGN KEY (`user1`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_frienships_user2_users_id` FOREIGN KEY (`user2`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE `players` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userId` int DEFAULT NULL,
  `userName` varchar(50) DEFAULT NULL,
  `vitalityPoints` int DEFAULT '0',
  `availableMoney` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_players_userid_id_ibfk1` (`userId`),
  CONSTRAINT `fk_players_userid_id_ibfk1` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `matches` (
  `id` int NOT NULL AUTO_INCREMENT,
  `matchName` varchar(100) DEFAULT NULL UNIQUE,
  `hostId` int DEFAULT NULL,
  `guestId` int DEFAULT NULL,
  `matchDate` datetime DEFAULT NULL,
  `isPublic` tinyint DEFAULT NULL,
  `status` enum('InProgress','Finished','Pending','Cancelled') DEFAULT NULL,
  `winner` int DEFAULT NULL,
  `matchTime` int DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `fk_winner_id` (`winner`),
  KEY `fk_guestId_id` (`guestId`),
  KEY `fk_hostId_id` (`hostId`),
  CONSTRAINT `fk_guestId_id` FOREIGN KEY (`guestId`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_hostId_id` FOREIGN KEY (`hostId`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_winner_id` FOREIGN KEY (`winner`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `matches_spectators` (
  `id_match` int NOT NULL,
  `id_user` int NOT NULL,
  PRIMARY KEY (`id_match`,`id_user`),
  CONSTRAINT `fk_id_user_usersId` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `matchesinvitations` (
  `id` int NOT NULL AUTO_INCREMENT,
  `matchId` int DEFAULT '0',
  `isAccepted` tinyint DEFAULT '0',
  `idUserHost` int NOT NULL,
  `idUserGuest` int NOT NULL,
  `status` enum('Pending','Refuse','Accepted') DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idUserHost` (`idUserHost`),
  KEY `idUserGuest` (`idUserGuest`),
  KEY `fk_matchinvitations_matchid` (`matchId`),
  CONSTRAINT `fk_matchinvitations_matchid` FOREIGN KEY (`matchId`) REFERENCES `matches` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `matchesinvitations_ibfk_1` FOREIGN KEY (`idUserHost`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `matchesinvitations_ibfk_2` FOREIGN KEY (`idUserGuest`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `troops` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `troopType` enum('Ofensive','Defensive') DEFAULT NULL,
  `class` enum('Warrior','Archer','Cannon','ArcherTower') DEFAULT NULL,
  `vitalityPoints` int DEFAULT NULL,
  `speed` int DEFAULT NULL,
  `damage` int DEFAULT NULL,
  `troopRange` int DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `cost` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
);

CREATE TABLE `matches_troops` (
`id_match` int NOT NULL,
`id_troop` int NOT NULL,
`id_user` int NOT NULL,
`matchTroopId` int NOT NULL AUTO_INCREMENT,
`isLocated` tinyint NOT NULL DEFAULT 0,
`rowsLocation` int DEFAULT '0',
`colLocation` int DEFAULT '0',
`vitalityPoints` int DEFAULT '0',
`isDead` varchar(45) DEFAULT NULL,
primary key (`matchTroopId`),
foreign key (`id_match`) references `matches` (`id`),
foreign key (`id_troop`) references `troops` (`id`),
foreign key (`id_user`) references `users` (`id`)
);

insert into troops (name, troopType, class, vitalityPoints, speed, damage, troopRange, icon, cost) values ("Warrior", "Ofensive", "Warrior", 150, 1, 50, 1, "/warrior.png", 100);
insert into troops (name, troopType, class, vitalityPoints, speed, damage, troopRange, icon, cost) values ("Archer", "Ofensive", "Archer", 100, 1, 60, 1, "/archer.jpg", 200);
insert into troops (name, troopType, class, vitalityPoints, speed, damage, troopRange, icon, cost) values ("Cannon", "Defensive", "Cannon", 200, 0, 70, 6, "/cannon.png", 300);
insert into troops (name, troopType, class, vitalityPoints, speed, damage, troopRange, icon, cost) values ("ArcherTower", "Defensive", "ArcherTower", 300, 0, 80, 8, "/archer-tower.jpg", 400);