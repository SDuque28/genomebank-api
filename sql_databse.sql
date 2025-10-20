CREATE DATABASE genomebank_db;
use genomebank_db;

CREATE TABLE `species` (
  `species_id` int NOT NULL AUTO_INCREMENT,
  `scientific_name` varchar(100) NOT NULL,
  `common_name` varchar(100) DEFAULT NULL,
  `description` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`species_id`)
);

CREATE TABLE `genomes` (
  `genome_id` int NOT NULL AUTO_INCREMENT,
  `species_id` int NOT NULL,
  `version` varchar(50) NOT NULL,
  `description` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`genome_id`),
  KEY `species_id` (`species_id`),
  CONSTRAINT `genomes_ibfk_1` FOREIGN KEY (`species_id`) REFERENCES `species` (`species_id`) ON DELETE CASCADE
);

CREATE TABLE `chromosomes` (
  `chromosome_id` int NOT NULL AUTO_INCREMENT,
  `genome_id` int NOT NULL,
  `name` varchar(50) NOT NULL,
  `length` int NOT NULL,
  `sequence` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`chromosome_id`),
  KEY `genome_id` (`genome_id`),
  CONSTRAINT `chromosomes_ibfk_1` FOREIGN KEY (`genome_id`) REFERENCES `genomes` (`genome_id`) ON DELETE CASCADE
);

CREATE TABLE `functions` (
  `function_id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `category` enum('BP','MF','CC') NOT NULL,
  `description` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`function_id`),
  UNIQUE KEY `code` (`code`)
);

CREATE TABLE `genes` (
  `gene_id` int NOT NULL AUTO_INCREMENT,
  `chromosome_id` int NOT NULL,
  `symbol` varchar(50) NOT NULL,
  `start_position` int NOT NULL,
  `end_position` int NOT NULL,
  `strand` char(1) NOT NULL,
  `sequence` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`gene_id`),
  KEY `chromosome_id` (`chromosome_id`),
  CONSTRAINT `genes_ibfk_1` FOREIGN KEY (`chromosome_id`) REFERENCES `chromosomes` (`chromosome_id`) ON DELETE CASCADE,
  CONSTRAINT `genes_chk_1` CHECK ((`strand` in (_utf8mb4'+',_utf8mb4'-')))
);

CREATE TABLE `gene_function` (
  `gene_id` int NOT NULL,
  `function_id` int NOT NULL,
  `evidence` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`gene_id`,`function_id`),
  KEY `function_id` (`function_id`),
  CONSTRAINT `gene_function_ibfk_1` FOREIGN KEY (`gene_id`) REFERENCES `genes` (`gene_id`) ON DELETE CASCADE,
  CONSTRAINT `gene_function_ibfk_2` FOREIGN KEY (`function_id`) REFERENCES `functions` (`function_id`) ON DELETE CASCADE
);

CREATE TABLE `rol` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
);

CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `activo` tinyint DEFAULT '1',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
);

CREATE TABLE `usuario_rol` (
  `user_id` bigint NOT NULL,
  `rol_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`rol_id`),
  KEY `fk_rol` (`rol_id`),
  CONSTRAINT `fk_rol` FOREIGN KEY (`rol_id`) REFERENCES `rol` (`id`),
  CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);
