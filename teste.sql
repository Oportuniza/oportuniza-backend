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

-- Inserindo dados na tabela offers
INSERT INTO offers (id, description, image_url, negotiable, title, user_id)
VALUES
    (1, 'Excelente oportunidade de emprego.', 'http://imageurl.com/image.jpg', TRUE, 'Oferta de Trabalho', 1),
    (2, 'Serviço', 'http://imageurl.com/image.jpg', TRUE, 'Oferta de Serviço', 1),
    (3, 'Vaga de desenvolvedor Java.', 'http://imageurl.com/image2.jpg', TRUE, 'Oferta de Trabalho', 1),
    (4, 'Aula de matemática particular.', 'http://imageurl.com/image2.jpg', FALSE, 'Oferta de Serviço', 1),
    (5, 'Consultoria financeira.', 'http://imageurl.com/image3.jpg', TRUE, 'Oferta de Serviço', 1),
    (6, 'Designer gráfico.', 'http://imageurl.com/image4.jpg', FALSE, 'Oferta de Trabalho', 1),
    (7, 'Tradução de documentos.', 'http://imageurl.com/image5.jpg', TRUE, 'Oferta de Serviço', 1),
    (8, 'Professor de inglês.', 'http://imageurl.com/image6.jpg', TRUE, 'Oferta de Trabalho', 1),
    (9, 'Assistência técnica.', 'http://imageurl.com/image7.jpg', TRUE, 'Oferta de Serviço', 1),
    (10, 'Engenheiro de software.', 'http://imageurl.com/image8.jpg', TRUE, 'Oferta de Trabalho', 1),
    (11, 'Treinador pessoal.', 'http://imageurl.com/image9.jpg', FALSE, 'Oferta de Serviço', 1),
    (12, 'Gerente de projetos.', 'http://imageurl.com/image10.jpg', TRUE, 'Oferta de Trabalho', 1),
    (13, 'Aulas de violão.', 'http://imageurl.com/image11.jpg', TRUE, 'Oferta de Serviço', 1),
    (14, 'Analista de marketing.', 'http://imageurl.com/image12.jpg', TRUE, 'Oferta de Trabalho', 1),
    (15, 'Desenvolvimento de websites.', 'http://imageurl.com/image13.jpg', FALSE, 'Oferta de Serviço', 1),
    (16, 'Gestor de redes sociais.', 'http://imageurl.com/image14.jpg', TRUE, 'Oferta de Trabalho', 1),
    (17, 'Fotógrafo.', 'http://imageurl.com/image15.jpg', FALSE, 'Oferta de Serviço', 1),
    (18, 'Advogado.', 'http://imageurl.com/image16.jpg', TRUE, 'Oferta de Trabalho', 1),
    (19, 'Consultoria de TI.', 'http://imageurl.com/image17.jpg', TRUE, 'Oferta de Serviço', 1),
    (20, 'Arquiteto.', 'http://imageurl.com/image18.jpg', TRUE, 'Oferta de Trabalho', 1),
    (21, 'Massoterapeuta.', 'http://imageurl.com/image19.jpg', FALSE, 'Oferta de Serviço', 1),
    (22, 'Enfermeiro.', 'http://imageurl.com/image20.jpg', TRUE, 'Oferta de Trabalho', 1);

-- Inserindo dados na tabela job
INSERT INTO job (localization, salary, working_model, working_regime, id)
VALUES
    ('Ponte da Barca', 5000.00, 'Full-Time', 'Flexible Hours', 1),
    ('Lisboa', 4500.00, 'Full-Time', 'Regular Hours', 3),
    ('Porto', 4200.00, 'Part-Time', 'Flexible Hours', 6),
    ('Coimbra', 3800.00, 'Full-Time', 'Regular Hours', 8),
    ('Braga', 5000.00, 'Remote', 'Flexible Hours', 10),
    ('Aveiro', 4600.00, 'Full-Time', 'Regular Hours', 12),
    ('Faro', 4300.00, 'Part-Time', 'Flexible Hours', 14),
    ('Évora', 3900.00, 'Full-Time', 'Regular Hours', 16),
    ('Leiria', 4800.00, 'Remote', 'Flexible Hours', 18),
    ('Beja', 4400.00, 'Full-Time', 'Regular Hours', 20),
    ('Viana do Castelo', 4100.00, 'Part-Time', 'Flexible Hours', 22);

-- Inserindo dados na tabela service
INSERT INTO service (price, id)
VALUES
    (250.00, 2),
    (300.00, 4),
    (350.00, 5),
    (280.00, 7),
    (400.00, 9),
    (320.00, 11),
    (270.00, 13),
    (450.00, 15),
    (330.00, 17),
    (290.00, 19),
    (370.00, 21);
