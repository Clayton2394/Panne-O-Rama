-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : mer. 04 nov. 2026 à 22:28
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `supervision_incidents`
--

-- --------------------------------------------------------

--
-- Structure de la table `equipement`
--

CREATE TABLE `equipement` (
  `id` int(11) NOT NULL,
  `reference` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `type` varchar(50) NOT NULL,
  `etat_fonctionnement` varchar(50) NOT NULL,
  `station_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `equipement`
--

INSERT INTO `equipement` (`id`, `reference`, `description`, `type`, `etat_fonctionnement`, `station_id`) VALUES
(1, 'ASC-001', 'Ascenseur Quai 1', 'ASCENSEUR', 'EN_PANNE', 1),
(2, 'ESC-002', 'Escalator Sortie Sud', 'ESCALATOR', 'EN_PANNE', 2),
(3, 'POR-003', 'Portique Entrée Principale', 'PORTIQUE', 'EN_PANNE', 3),
(4, 'DIS-004', 'Distributeur Billets', 'DISTRIBUTEUR', 'FONCTIONNEL', 1);

-- --------------------------------------------------------

--
-- Structure de la table `evaluation`
--

CREATE TABLE `evaluation` (
  `id` int(11) NOT NULL,
  `note` int(11) NOT NULL CHECK (`note` between 1 and 5),
  `commentaire` text DEFAULT NULL,
  `date_evaluation` datetime NOT NULL,
  `incident_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `evaluation`
--

INSERT INTO `evaluation` (`id`, `note`, `commentaire`, `date_evaluation`, `incident_id`) VALUES
(1, 4, 'Intervention rapide et efficace', '2026-10-12 15:38:22', 5);

-- --------------------------------------------------------

--
-- Structure de la table `evenement_incident`
--

CREATE TABLE `evenement_incident` (
  `id` int(11) NOT NULL,
  `incident_id` int(11) NOT NULL,
  `type` varchar(50) NOT NULL,
  `date_evenement` datetime NOT NULL,
  `auteur` varchar(50) NOT NULL,
  `detail` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `evenement_incident`
--

INSERT INTO `evenement_incident` (`id`, `incident_id`, `type`, `date_evenement`, `auteur`, `detail`) VALUES
(1, 1, 'DECLARATION', '2026-10-10 16:38:22', 'agent1', 'Incident déclaré'),
(2, 2, 'DECLARATION', '2026-10-10 15:38:22', 'agent1', 'Incident déclaré'),
(3, 3, 'DECLARATION', '2026-10-09 14:38:22', 'agent1', 'Incident déclaré'),
(4, 4, 'DECLARATION', '2026-10-11 19:38:22', 'agent1', 'Incident déclaré'),
(5, 5, 'DECLARATION', '2026-10-02 15:38:22', 'agent1', 'Incident déclaré'),
(6, 6, 'DECLARATION', '2026-10-30 10:02:26', 'agent1', 'Incident déclaré'),
(7, 7, 'DECLARATION', '2026-10-31 20:24:55', 'agent1', 'Incident déclaré'),
(8, 8, 'DECLARATION', '2026-10-31 19:24:55', 'agent1', 'Incident déclaré'),
(9, 9, 'DECLARATION', '2026-10-30 15:24:55', 'agent1', 'Incident déclaré'),
(16, 9, 'AFFECTATION', '2026-10-30 16:24:55', 'resp1', 'Affecté à un prestataire'),
(17, 1, 'RESOLUTION', '2026-11-01 20:28:18', 'resp1', 'Incident résolu'),
(18, 4, 'RESOLUTION', '2026-11-01 20:27:43', 'resp1', 'Incident résolu'),
(19, 5, 'RESOLUTION', '2026-10-04 15:38:22', 'resp1', 'Incident résolu'),
(20, 5, 'EVALUATION', '2026-10-12 15:38:22', 'resp1', 'Évaluation saisie : 4/5');

-- --------------------------------------------------------

--
-- Structure de la table `incident`
--

CREATE TABLE `incident` (
  `id` int(11) NOT NULL,
  `description` text NOT NULL,
  `date_ouverture` datetime NOT NULL,
  `statut` varchar(50) NOT NULL,
  `date_resolution` datetime DEFAULT NULL,
  `equipement_id` int(11) NOT NULL,
  `prestataire_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `incident`
--

INSERT INTO `incident` (`id`, `description`, `date_ouverture`, `statut`, `date_resolution`, `equipement_id`, `prestataire_id`) VALUES
(1, 'Porte bloquée', '2026-10-10 16:38:22', 'RESOLU', '2026-11-01 20:28:18', 1, NULL),
(2, 'Marches à l\'arrêt', '2026-10-10 15:38:22', 'NON_AFFECTE', NULL, 2, NULL),
(3, 'Écran cassé', '2026-10-09 14:38:22', 'NON_AFFECTE', NULL, 3, NULL),
(4, 'Moteur grillé', '2026-10-11 19:38:22', 'RESOLU', '2026-11-01 20:27:43', 1, 1),
(5, 'Vitre fêlée', '2026-10-02 15:38:22', 'RESOLU', '2026-10-04 15:38:22', 2, 2),
(6, 'zrgt\"rhg\"ezofoefhzeuof', '2026-10-30 10:02:26', 'NON_AFFECTE', NULL, 1, NULL),
(7, 'Écran tactile figé', '2026-10-31 20:24:55', 'NON_AFFECTE', NULL, 4, NULL),
(8, 'Barrière bloquée', '2026-10-31 19:24:55', 'NON_AFFECTE', NULL, 3, NULL),
(9, 'Panne électrique prolongée', '2026-10-30 15:24:55', 'AFFECTE', NULL, 2, 2);

-- --------------------------------------------------------

--
-- Structure de la table `prestataire`
--

CREATE TABLE `prestataire` (
  `id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `contact` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `prestataire`
--

INSERT INTO `prestataire` (`id`, `nom`, `contact`) VALUES
(1, 'ElevatorTech', 'contact@elevatortech.fr - 0123456789'),
(2, 'Portails & Co', 'support@portailsco.fr - 0987654321');

-- --------------------------------------------------------

--
-- Structure de la table `station`
--

CREATE TABLE `station` (
  `id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `station`
--

INSERT INTO `station` (`id`, `nom`) VALUES
(1, 'Gare du Nord'),
(2, 'Châtelet'),
(3, 'Montparnasse');

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `id` int(11) NOT NULL,
  `login` varchar(50) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `profil` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`id`, `login`, `mot_de_passe`, `prenom`, `profil`) VALUES
(1, 'resp1', '$2a$10$0XIuBpZc6qHjZnLFGD3EkOALDsSYdQDcZFcs6wbl3PkFRnezF.SVm', 'Alice', 'RESPONSABLE'),
(2, 'agent1', '$2a$10$WCl25yIjZaqdKPg15oYBAuyflirCK44nNVXX0DiAVOfqWL1/5IfCK', 'Bob', 'AGENT');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `equipement`
--
ALTER TABLE `equipement`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `reference` (`reference`),
  ADD KEY `station_id` (`station_id`);

--
-- Index pour la table `evaluation`
--
ALTER TABLE `evaluation`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `incident_id` (`incident_id`);

--
-- Index pour la table `evenement_incident`
--
ALTER TABLE `evenement_incident`
  ADD PRIMARY KEY (`id`),
  ADD KEY `incident_id` (`incident_id`);

--
-- Index pour la table `incident`
--
ALTER TABLE `incident`
  ADD PRIMARY KEY (`id`),
  ADD KEY `equipement_id` (`equipement_id`),
  ADD KEY `prestataire_id` (`prestataire_id`);

--
-- Index pour la table `prestataire`
--
ALTER TABLE `prestataire`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `station`
--
ALTER TABLE `station`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `login` (`login`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `equipement`
--
ALTER TABLE `equipement`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `evaluation`
--
ALTER TABLE `evaluation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pour la table `evenement_incident`
--
ALTER TABLE `evenement_incident`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT pour la table `incident`
--
ALTER TABLE `incident`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT pour la table `prestataire`
--
ALTER TABLE `prestataire`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `station`
--
ALTER TABLE `station`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `equipement`
--
ALTER TABLE `equipement`
  ADD CONSTRAINT `equipement_ibfk_1` FOREIGN KEY (`station_id`) REFERENCES `station` (`id`);

--
-- Contraintes pour la table `evaluation`
--
ALTER TABLE `evaluation`
  ADD CONSTRAINT `evaluation_ibfk_1` FOREIGN KEY (`incident_id`) REFERENCES `incident` (`id`);

--
-- Contraintes pour la table `evenement_incident`
--
ALTER TABLE `evenement_incident`
  ADD CONSTRAINT `evenement_incident_ibfk_1` FOREIGN KEY (`incident_id`) REFERENCES `incident` (`id`);

--
-- Contraintes pour la table `incident`
--
ALTER TABLE `incident`
  ADD CONSTRAINT `incident_ibfk_1` FOREIGN KEY (`equipement_id`) REFERENCES `equipement` (`id`),
  ADD CONSTRAINT `incident_ibfk_2` FOREIGN KEY (`prestataire_id`) REFERENCES `prestataire` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
