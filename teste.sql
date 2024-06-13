-- Limpeza dos dados das tabelas
--DELETE FROM users_roles;
--DELETE FROM service;
--DELETE FROM job;
--DELETE FROM favorites_offers;
--DELETE FROM documents;
--DELETE FROM applications;
--DELETE FROM offers;
--DELETE FROM favorite_users;
--DELETE FROM reviews;
--DELETE FROM users;
--DELETE FROM roles;
--DELETE FROM chat_message;
--DELETE FROM chat_notification;
--DELETE FROM general_notification;

-- Inserindo dados na tabela chat_message
INSERT INTO chat_message (id, content, receiver, sender, status, timestamp)
VALUES
    (1, 'Olá! Como posso ajudar?', 'user123', 'user456', 1, '2024-06-08 12:00:00'),
    (2, 'Preciso de ajuda com um pedido.', 'user456', 'user123', 0, '2024-06-08 12:01:00');

-- Inserindo dados na tabela chat_notification
INSERT INTO chat_notification (id, sender)
VALUES
    (1, 'user123'),
    (2, 'user456');

-- Inserindo dados na tabela general_notification
INSERT INTO general_notification (id, message, target_user)
VALUES
    (1, 'Você tem uma nova mensagem', 'user123'),
    (2, 'Seu pedido foi atualizado', 'user456');

-- Inserindo dados na tabela roles
INSERT INTO roles (id, name)
VALUES
    (1, 'Admin'),
    (2, 'User');

-- Inserindo dados na tabela users
INSERT INTO users (id, average_rating, county, created_at, district, email, name, password, phone_number, resume_url, review_count, updated_at)
VALUES
    (1, 4.5, 'São Paulo', '2024-01-01 00:00:00', 'SP', 'email@example.com', 'João Silva', 'senha123', '11987654321', 'http://resumeurl.com/resume.pdf', 10, '2024-06-01 00:00:00'),
    (2, 3.8, 'Braga', '2024-01-01 00:00:00', 'Braga', 'email2@example.com', 'Gui Silva', 'senha123', '999999999999', 'http://resumeurl.com/resume2.pdf', 8, '2024-06-01 00:00:00');

-- Inserindo dados na tabela favorite_users
INSERT INTO favorite_users (user_id, favorite_user_id)
VALUES
    (1, 2),
    (2, 1);

-- Inserindo dados na tabela offers
INSERT INTO offers (id, description, image_url, negotiable, title, user_id)
VALUES
    (1, 'Excelente oportunidade de emprego.', 'http://imageurl.com/image.jpg', TRUE, 'Oferta de Trabalho', 1),
    (2, 'Serviço', 'http://imageurl.com/image.jpg', TRUE, 'Oferta de Serviço', 2);

-- Inserindo dados na tabela applications
INSERT INTO applications (id, email, first_name, last_name, message, resume_url, status, offer_id, user_id)
VALUES
    (1, 'email@example.com', 'João', 'Silva', 'Estou muito interessado na vaga.', 'http://resumeurl.com/resume.pdf', 'Pending', 1, 1),
    (2, 'email2@example.com', 'Gui', 'Silva', 'Interesse na vaga de TI.', 'http://resumeurl.com/resume2.pdf', 'Pending', 2, 2);

-- Inserindo dados na tabela documents
INSERT INTO documents (id, url, application_id)
VALUES
    (1, 'http://documenturl.com/document.pdf', 1),
    (2, 'http://documenturl.com/document2.pdf', 2);

-- Inserindo dados na tabela favorites_offers
INSERT INTO favorites_offers (user_id, offer_id)
VALUES
    (1, 1),
    (2, 2);

-- Inserindo dados na tabela job
INSERT INTO job (localization, salary, working_model, working_regime, id)
VALUES
    ('Remote', 5000.00, 'Full-Time', 'Flexible Hours', 1),
    ('Office', 3500.00, 'Part-Time', 'Fixed Hours', 2);

-- Inserindo dados na tabela reviews
INSERT INTO reviews (rating, reviewed_id, reviewer_id)
VALUES
    (5, 1, 2),
    (4, 2, 1);

-- Inserindo dados na tabela service
INSERT INTO service (price, id)
VALUES
    (100.00, 1),
    (250.00, 2);

-- Inserindo dados na tabela users_roles
INSERT INTO users_roles (user_id, role_id)
VALUES
    (1, 1),
    (2, 2);