-- Sample data for TaskFlow application
-- This file is automatically loaded when using H2 database in development

-- Work Tasks
INSERT INTO tasks (task_type, task_uuid, title, description, due_date, completed, priority, created_date, updated_date, project, client, department)
VALUES
    ('WORK', 'wt001234', 'Ferdigstille prosjektrapport', 'Skriv sammendrag og konklusjon for Q4 rapport', CURRENT_DATE + 3, false, 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Prosjekt Alpha', 'Kunde AS', 'IT'),
    ('WORK', 'wt005678', 'Teammøte', 'Ukentlig statusmøte med utviklingsteamet', CURRENT_DATE + 2, false, 'MEDIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Drift', 'Intern', 'IT'),
    ('WORK', 'wt009876', 'Code review', 'Gjennomgå kode for ny funksjonalitet', CURRENT_DATE + 1, false, 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'TaskFlow', 'Intern', 'Utvikling'),
    ('WORK', 'wt005432', 'Kundemøte', 'Presentere ny løsning for kunde', CURRENT_DATE + 5, false, 'MEDIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Salgsprosjekt', 'TechCorp', 'Salg'),
    ('WORK', 'wt001111', 'Deployment', 'Deploy applikasjon til produksjon', CURRENT_DATE - 1, true, 'HIGH', CURRENT_TIMESTAMP - 2, CURRENT_TIMESTAMP, 'TaskFlow', 'Intern', 'DevOps');

-- Personal Tasks
INSERT INTO tasks (task_type, task_uuid, title, description, due_date, completed, priority, created_date, updated_date, category, location)
VALUES
    ('PERSONAL', 'pt001234', 'Handle mat', 'Kjøp ingredienser til middag og frokost', CURRENT_DATE + 1, false, 'MEDIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Shopping', 'Rema 1000'),
    ('PERSONAL', 'pt005678', 'Tannlegetime', 'Årlig kontroll hos tannlege', CURRENT_DATE + 7, false, 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Helse', 'Oslo sentrum'),
    ('PERSONAL', 'pt009876', 'Trimavgift', 'Betale månedlig treningsstudio avgift', CURRENT_DATE + 2, false, 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sport', 'SATS'),
    ('PERSONAL', 'pt005432', 'Planlegge helgetur', 'Finne hotell og aktiviteter for helgetur', CURRENT_DATE + 4, false, 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Reise', 'Hjemme'),
    ('PERSONAL', 'pt002222', 'Lese bok', 'Fullføre "Clean Code" bok', CURRENT_DATE - 2, true, 'MEDIUM', CURRENT_TIMESTAMP - 3, CURRENT_TIMESTAMP, 'Utdanning', 'Hjemme');

-- Additional variety for demo
INSERT INTO tasks (task_type, task_uuid, title, description, due_date, completed, priority, created_date, updated_date, project, client, department)
VALUES
    ('WORK', 'wt007777', 'Sikkerheitsgjennomgang', 'Gjennomgå sikkerhetsrutiner for Q1', CURRENT_DATE + 10, false, 'MEDIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Sikkerhet', 'Intern', 'IT'),
    ('WORK', 'wt008888', 'Dokumentasjon', 'Oppdatere API dokumentasjon', CURRENT_DATE + 6, false, 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'TaskFlow', 'Intern', 'Utvikling');

INSERT INTO tasks (task_type, task_uuid, title, description, due_date, completed, priority, created_date, updated_date, category, location)
VALUES
    ('PERSONAL', 'pt003333', 'Vaskedag', 'Vaske og rydde i hjemmet', CURRENT_DATE + 3, false, 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Hjem', 'Hjemme'),
    ('PERSONAL', 'pt004444', 'Bursdagsgave', 'Kjøpe gave til søsters bursdag', CURRENT_DATE + 8, false, 'MEDIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Familie', 'Sentralen');