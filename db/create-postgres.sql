DROP TABLE IF EXISTS evenement_incident, evaluation, incident, prestataire, equipement, station, utilisateur CASCADE;

CREATE TABLE utilisateur (
                             id SERIAL PRIMARY KEY,
                             login VARCHAR(50) UNIQUE NOT NULL,
                             mot_de_passe VARCHAR(255) NOT NULL,
                             prenom VARCHAR(50) NOT NULL,
                             profil VARCHAR(50) NOT NULL
);

CREATE TABLE station (
                         id SERIAL PRIMARY KEY,
                         nom VARCHAR(100) NOT NULL
);

CREATE TABLE equipement (
                            id SERIAL PRIMARY KEY,
                            reference VARCHAR(50) UNIQUE NOT NULL,
                            description VARCHAR(255),
                            type VARCHAR(50) NOT NULL,
                            etat_fonctionnement VARCHAR(50) NOT NULL,
                            station_id INT NOT NULL REFERENCES station(id)
);

CREATE TABLE prestataire (
                             id SERIAL PRIMARY KEY,
                             nom VARCHAR(100) NOT NULL,
                             contact VARCHAR(255) NOT NULL
);

CREATE TABLE incident (
                          id SERIAL PRIMARY KEY,
                          description TEXT NOT NULL,
                          date_ouverture TIMESTAMP NOT NULL,
                          statut VARCHAR(50) NOT NULL,
                          date_resolution TIMESTAMP,
                          equipement_id INT NOT NULL REFERENCES equipement(id),
                          prestataire_id INT REFERENCES prestataire(id)
);

CREATE TABLE evaluation (
                            id SERIAL PRIMARY KEY,
                            note INT NOT NULL CHECK (note BETWEEN 1 AND 5),
                            commentaire TEXT,
                            date_evaluation TIMESTAMP NOT NULL,
                            incident_id INT NOT NULL UNIQUE REFERENCES incident(id)
);

CREATE TABLE evenement_incident (
                                    id SERIAL PRIMARY KEY,
                                    incident_id INT NOT NULL REFERENCES incident(id),
                                    type VARCHAR(50) NOT NULL,
                                    date_evenement TIMESTAMP NOT NULL,
                                    auteur VARCHAR(50) NOT NULL,
                                    detail VARCHAR(500) NOT NULL
);

-- Utilisateurs (mots de passe hachés en BCrypt)
INSERT INTO utilisateur (login, mot_de_passe, prenom, profil) VALUES
                                                                  ('resp1', '$2a$10$0XIuBpZc6qHjZnLFGD3EkOALDsSYdQDcZFcs6wbl3PkFRnezF.SVm', 'Alice', 'RESPONSABLE'),
                                                                  ('agent1', '$2a$10$WCl25yIjZaqdKPg15oYBAuyflirCK44nNVXX0DiAVOfqWL1/5IfCK', 'Bob', 'AGENT');

INSERT INTO station (nom) VALUES
                              ('Gare du Nord'),
                              ('Châtelet'),
                              ('Montparnasse');

INSERT INTO equipement (reference, description, type, etat_fonctionnement, station_id) VALUES
                                                                                           ('ASC-001', 'Ascenseur Quai 1', 'ASCENSEUR', 'EN_PANNE', 1),
                                                                                           ('ESC-002', 'Escalator Sortie Sud', 'ESCALATOR', 'EN_PANNE', 2),
                                                                                           ('POR-003', 'Portique Entrée Principale', 'PORTIQUE', 'EN_PANNE', 3),
                                                                                           ('DIS-004', 'Distributeur Billets', 'DISTRIBUTEUR', 'FONCTIONNEL', 1);

INSERT INTO prestataire (nom, contact) VALUES
                                           ('ElevatorTech', 'contact@elevatortech.fr - 0123456789'),
                                           ('Portails & Co', 'support@portailsco.fr - 0987654321');

-- Incidents : dates relatives à l'instant du chargement du script
INSERT INTO incident (description, date_ouverture, statut, date_resolution, equipement_id, prestataire_id) VALUES
-- non affectés : vert, orange, rouge, et les cas limites
('Porte bloquée',              NOW() - INTERVAL '47 hours',  'NON_AFFECTE', NULL, 1, NULL),
('Marches à l''arrêt',         NOW() - INTERVAL '48 hours',  'NON_AFFECTE', NULL, 2, NULL),
('Écran cassé',                NOW() - INTERVAL '73 hours',  'NON_AFFECTE', NULL, 3, NULL),
-- affecté : « pris en charge »
('Moteur grillé',              NOW() - INTERVAL '20 hours',  'AFFECTE',     NULL, 1, 1),
-- résolu, avec évaluation
('Vitre fêlée',                NOW() - INTERVAL '10 days',   'RESOLU',      NOW() - INTERVAL '8 days', 2, 2),
-- cas limites supplémentaires
('Écran tactile figé',         NOW() - INTERVAL '60 hours',  'NON_AFFECTE', NULL, 4, NULL),
('Barrière bloquée',           NOW() - INTERVAL '72 hours',  'NON_AFFECTE', NULL, 3, NULL),
-- affecté depuis longtemps : doit rester « pris en charge », jamais rouge
('Panne électrique prolongée', NOW() - INTERVAL '100 hours', 'AFFECTE',     NULL, 2, 2),
-- résolus supplémentaires, pour vérifier l'ordre de l'historique
('Écran éteint',               NOW() - INTERVAL '5 days',    'RESOLU',      NOW() - INTERVAL '1 day',  3, 1),
('Moteur remplacé',            NOW() - INTERVAL '7 days',    'RESOLU',      NOW() - INTERVAL '3 days', 1, 1);

INSERT INTO evaluation (note, commentaire, date_evaluation, incident_id) VALUES
    (4, 'Intervention rapide et efficace', NOW() - INTERVAL '7 days', 5);

-- Journal métier reconstitué à partir des incidents
INSERT INTO evenement_incident (incident_id, type, date_evenement, auteur, detail)
SELECT id, 'DECLARATION', date_ouverture, 'agent1', 'Incident déclaré' FROM incident;

INSERT INTO evenement_incident (incident_id, type, date_evenement, auteur, detail)
SELECT id, 'AFFECTATION', date_ouverture + INTERVAL '1 hour', 'resp1', 'Affecté à un prestataire'
FROM incident WHERE prestataire_id IS NOT NULL;

INSERT INTO evenement_incident (incident_id, type, date_evenement, auteur, detail)
SELECT id, 'RESOLUTION', date_resolution, 'resp1', 'Incident résolu' FROM incident WHERE statut = 'RESOLU';

INSERT INTO evenement_incident (incident_id, type, date_evenement, auteur, detail)
SELECT incident_id, 'EVALUATION', date_evaluation, 'resp1', CONCAT('Évaluation saisie : ', note, '/5')
FROM evaluation;