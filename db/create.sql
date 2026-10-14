-- ==========================================
-- 0. CRÉATION DE LA BASE DE DONNÉES
-- ==========================================
DROP DATABASE IF EXISTS supervision_incidents;
CREATE DATABASE supervision_incidents CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE supervision_incidents;

-- ==========================================
-- 1. CRÉATION DES TABLES
-- ==========================================

CREATE TABLE utilisateur (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             login VARCHAR(50) UNIQUE NOT NULL,
                             mot_de_passe VARCHAR(255) NOT NULL, -- Rappel : Ne jamais stocker en clair en prod !
                             prenom VARCHAR(50) NOT NULL,
                             profil VARCHAR(50) NOT NULL
);

CREATE TABLE station (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         nom VARCHAR(100) NOT NULL
);

CREATE TABLE equipement (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            reference VARCHAR(50) UNIQUE NOT NULL, -- RG9 : Référence unique[cite: 4]
                            description VARCHAR(255),
                            type VARCHAR(50) NOT NULL,
                            etat_fonctionnement VARCHAR(50) NOT NULL,
                            station_id INT NOT NULL,
                            FOREIGN KEY (station_id) REFERENCES station(id)
);

CREATE TABLE prestataire (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             nom VARCHAR(100) NOT NULL,
                             contact VARCHAR(255) NOT NULL
);

CREATE TABLE incident (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          description TEXT NOT NULL,
                          date_ouverture DATETIME NOT NULL,
                          statut VARCHAR(50) NOT NULL, -- ex: 'NON_AFFECTE', 'AFFECTE', 'RESOLU'[cite: 4]
                          date_resolution DATETIME,
                          equipement_id INT NOT NULL,
                          prestataire_id INT,
                          FOREIGN KEY (equipement_id) REFERENCES equipement(id),
                          FOREIGN KEY (prestataire_id) REFERENCES prestataire(id)
);

CREATE TABLE evaluation (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            note INT NOT NULL CHECK (note BETWEEN 1 AND 5), -- RG8 : Entier de 1 à 5[cite: 4]
                            commentaire TEXT,
                            date_evaluation DATETIME NOT NULL,
                            incident_id INT NOT NULL UNIQUE, -- RG8 : Une seule évaluation par incident[cite: 4]
                            FOREIGN KEY (incident_id) REFERENCES incident(id)
);

-- ==========================================
-- 2. DONNÉES DE DÉMONSTRATION (INIT)
-- ==========================================

-- Utilisateurs
INSERT INTO utilisateur (login, mot_de_passe, prenom, profil) VALUES
                                                                  ('resp1', '$2a$10$xyz...', 'Alice', 'RESPONSABLE'),
                                                                  ('agent1', '$2a$10$xyz...', 'Bob', 'AGENT');

-- Stations
INSERT INTO station (nom) VALUES
                              ('Gare du Nord'),
                              ('Châtelet'),
                              ('Montparnasse');

-- Équipements
INSERT INTO equipement (reference, description, type, etat_fonctionnement, station_id) VALUES
                                                                                           ('ASC-001', 'Ascenseur Quai 1', 'ASCENSEUR', 'EN_PANNE', 1),
                                                                                           ('ESC-002', 'Escalator Sortie Sud', 'ESCALATOR', 'EN_PANNE', 2),
                                                                                           ('POR-003', 'Portique Entrée Principale', 'PORTIQUE', 'EN_PANNE', 3),
                                                                                           ('DIS-004', 'Distributeur Billets', 'DISTRIBUTEUR', 'FONCTIONNEL', 1);

-- Prestataires
INSERT INTO prestataire (nom, contact) VALUES
                                           ('ElevatorTech', 'contact@elevatortech.fr - 0123456789'),
                                           ('Portails & Co', 'support@portailsco.fr - 0987654321');

-- Incidents (Génération dynamique relative à l'instant présent comme demandé au §4[cite: 2])
-- 1. Incident VERT (ouvert il y a 47h)
INSERT INTO incident (description, date_ouverture, statut, equipement_id)
VALUES ('Porte bloquée', DATE_SUB(NOW(), INTERVAL 47 HOUR), 'NON_AFFECTE', 1);

-- 2. Incident ORANGE (ouvert il y a 48h pile)
INSERT INTO incident (description, date_ouverture, statut, equipement_id)
VALUES ('Marches à l''arrêt', DATE_SUB(NOW(), INTERVAL 48 HOUR), 'NON_AFFECTE', 2);

-- 3. Incident ROUGE (ouvert il y a 73h)
INSERT INTO incident (description, date_ouverture, statut, equipement_id)
VALUES ('Écran cassé', DATE_SUB(NOW(), INTERVAL 73 HOUR), 'NON_AFFECTE', 3);

-- 4. Incident AFFECTÉ (Ne doit pas afficher de couleur selon RG4[cite: 4])
INSERT INTO incident (description, date_ouverture, statut, equipement_id, prestataire_id)
VALUES ('Moteur grillé', DATE_SUB(NOW(), INTERVAL 20 HOUR), 'AFFECTE', 1, 1);

-- 5. Incident RÉSOLU
INSERT INTO incident (description, date_ouverture, statut, date_resolution, equipement_id, prestataire_id)
VALUES ('Vitre fêlée', DATE_SUB(NOW(), INTERVAL 10 DAY), 'RESOLU', DATE_SUB(NOW(), INTERVAL 8 DAY), 2, 2);

-- Évaluations (Sur un incident résolu et affecté, RG8[cite: 4])
INSERT INTO evaluation (note, commentaire, date_evaluation, incident_id)
VALUES (4, 'Intervention rapide et efficace', NOW(), 5);