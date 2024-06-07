-- Populating roles table
INSERT INTO roles (id, name) VALUES 
(1, 'Admin'),
(2, 'User');
﻿
-- Populating users table
INSERT INTO users (id, created_at, updated_at, county, district, email, name, password, phone_number, resume_url) VALUES 
(1, NOW(), NOW(), 'County1', 'District1', 'user1@example.com', 'User One', 'password1', '1234567890', 'http://example.com/resume1'),
(2, NOW(), NOW(), 'County2', 'District2', 'user2@example.com', 'User Two', 'password2', '0987654321', 'http://example.com/resume2');
﻿
-- Populating users_roles table
INSERT INTO users_roles (role_id, user_id) VALUES 
(1, 1),
(2, 2);
﻿
-- Populating offers table
INSERT INTO offers (id, negotiable, description, image_url, title) VALUES 
(1, TRUE, 'Job offer 1 description', 'http://example.com/image1', 'Job Offer 1'),
(2, FALSE, 'Service offer 1 description', 'http://example.com/image2', 'Service Offer 1');
﻿
-- Populating job table
INSERT INTO job (id, salary, localization) VALUES 
(1, 50000, 'Location1');
﻿
-- Populating service table
INSERT INTO service (id, price) VALUES 
(2, 100);
﻿
-- Populating applications table
INSERT INTO applications (id, offer_id, user_id) VALUES 
(1, 1, 1),
(2, 2, 2);
﻿
-- Populating favorite_users table
INSERT INTO favorite_users (user_id, favorite_user_id) VALUES 
(1, 2),
(2, 1);
﻿
-- Populating favorites_offers table
INSERT INTO favorites_offers (user_id, offer_id) VALUES 
(1, 2),
(2, 1);
﻿
-- Populating chat_message table
INSERT INTO chat_message (id, status, timestamp, content, receiver, sender) VALUES 
(1, 0, NOW(), 'Hello, User Two!', 'user2@example.com', 'user1@example.com'),
(2, 1, NOW(), 'Hello, User One!', 'user1@example.com', 'user2@example.com');
﻿
-- Ensure that all sequences (if any) are set to correct values
SELECT setval(pg_get_serial_sequence('roles', 'id'), max(id)) FROM roles;
SELECT setval(pg_get_serial_sequence('users', 'id'), max(id)) FROM users;
SELECT setval(pg_get_serial_sequence('offers', 'id'), max(id)) FROM offers;
SELECT setval(pg_get_serial_sequence('job', 'id'), max(id)) FROM job;
SELECT setval(pg_get_serial_sequence('service', 'id'), max(id)) FROM service;
SELECT setval(pg_get_serial_sequence('applications', 'id'), max(id)) FROM applications;
SELECT setval(pg_get_serial_sequence('favorite_users', 'user_id'), max(user_id)) FROM favorite_users;
SELECT setval(pg_get_serial_sequence('favorite_users', 'favorite_user_id'), max(favorite_user_id)) FROM favorite_users;
SELECT setval(pg_get_serial_sequence('favorites_offers', 'user_id'), max(user_id)) FROM favorites_offers;
SELECT setval(pg_get_serial_sequence('favorites_offers', 'offer_id'), max(offer_id)) FROM favorites_offers;
SELECT setval(pg_get_serial_sequence('chat_message', 'id'), max(id)) FROM chat_message;