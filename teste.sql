-- Limpeza dos dados das tabelas
DELETE FROM users_roles;
DELETE FROM service;
DELETE FROM job;
DELETE FROM favorites_offers;
DELETE FROM documents;
DELETE FROM applications;
DELETE FROM offers;
DELETE FROM favorite_users;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM chat_message;

INSERT INTO chat_message (status, id, timestamp, content, receiver, sender)
VALUES
    (1, 1, '2024-06-08 12:00:00', 'Olá! Como posso ajudar?', 'user123', 'user456'),
    (0, 2, '2024-06-08 12:01:00', 'Preciso de ajuda com um pedido.', 'user456', 'user123');

INSERT INTO roles (id, name)
VALUES
    (1, 'Admin'),
    (2, 'User');

INSERT INTO users (created_at, updated_at, id, county, district, email, name, password, phone_number, resume_url)
VALUES
    ('2024-01-01 00:00:00', '2024-06-01 00:00:00', 1, 'São Paulo', 'SP', 'email@example.com', 'João Silva', 'senha123', '11987654321', 'http://resumeurl.com/resume.pdf'),
    ('2024-01-01 00:00:00', '2024-06-01 00:00:00', 2, 'Braga', 'Braga', 'email2@example.com', 'Gui Silva', 'senha123', '999999999999', 'http://resumeurl.com/resume2.pdf'),
    ('2024-06-07 00:00:00', '2024-06-07 12:00:00', 3, 'Porto', 'Porto', 'email3@example.com', 'Ana Pereira', 'senha456', '22123456789', 'http://resumeurl.com/resume3.pdf'),
    ('2024-06-07 00:00:00', '2024-06-07 12:00:00', 4, 'Lisboa', 'Lisboa', 'email4@example.com', 'Miguel Costa', 'senha789', '21198765432', 'http://resumeurl.com/resume4.pdf');

INSERT INTO favorite_users (favorite_user_id, user_id)
VALUES
    (1, 1),
    (3, 2),
    (4, 1);

INSERT INTO offers (negotiable, id, user_id, description, image_url, title)
VALUES
    (TRUE, 1, 1, 'Excelente oportunidade de emprego.', 'http://imageurl.com/image.jpg', 'Oferta de Trabalho'),
    (TRUE, 2, 1, 'Serviço', 'http://imageurl.com/image.jpg', 'Oferta de Serviço'),
    (FALSE, 3, 2, 'Oportunidade de emprego em TI.', 'http://imageurl.com/image2.jpg', 'Vaga TI Senior'),
    (FALSE, 4, 3, 'Consultoria Financeira.', 'http://imageurl.com/image3.jpg', 'Consultor Financeiro');


INSERT INTO applications (id, offer_id, user_id, email, first_name, last_name, message, resume_url, status)
VALUES
    (1, 1, 1, 'email@example.com', 'João', 'Silva', 'Estou muito interessado na vaga.', 'http://resumeurl.com/resume.pdf', 'Pending'),
    (2, 3, 4, 'email4@example.com', 'Miguel', 'Costa', 'Interesse na vaga de TI.', 'http://resumeurl.com/resume4.pdf', 'Pending'),
    (3, 4, 3, 'email3@example.com', 'Ana', 'Pereira', 'Gostaria de aplicar para consultor.', 'http://resumeurl.com/resume3.pdf', 'Pending');

INSERT INTO documents (application_id, id, url)
VALUES
    (1, 1, 'http://documenturl.com/document.pdf'),
    (2, 2, 'http://documenturl.com/document2.pdf'),
    (3, 3, 'http://documenturl.com/document3.pdf');

INSERT INTO favorites_offers (offer_id, user_id)
VALUES
    (1, 1),
    (3, 1),
    (4, 2);

INSERT INTO job (salary, id, localization, working_model, working_regime)
VALUES
    (5000.00, 1, 'Remote', 'Full-Time', 'Flexible Hours'),
    (3500.00, 3, 'Office', 'Part-Time', 'Fixed Hours');

INSERT INTO service (price, id)
VALUES
    (100.00, 2),
    (250.00, 4);

INSERT INTO users_roles (role_id, user_id)
VALUES
    (1, 1),
    (2, 2),
    (1, 3),
    (2, 4);
