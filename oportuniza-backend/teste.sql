DELETE FROM chat_message;
DELETE FROM applications;
DELETE FROM favorites_offers;
DELETE FROM favorite_users;
DELETE FROM users_roles;
DELETE FROM service;
DELETE FROM job;
DELETE FROM offers;
DELETE FROM users;
DELETE FROM roles;

-- Populating roles table
INSERT INTO roles (id, name) VALUES
                                 (1, 'Admin'),
                                 (2, 'User');

-- Populating users table with 1000 users
DO $$
    DECLARE
        i INT;
    BEGIN
        FOR i IN 1..1000 LOOP
                INSERT INTO users (id, created_at, updated_at, county, district, email, name, password, phone_number, resume_url)
                VALUES
                    (i, NOW(), NOW(), 'County' || i, 'District' || i, 'user' || i || '@example.com', 'User ' || i, 'password' || i, '123456' || i, 'http://example.com/resume' || i);
            END LOOP;
    END $$;

-- Populating users_roles table
DO $$
    DECLARE
        i INT;
    BEGIN
        FOR i IN 1..1000 LOOP
                INSERT INTO users_roles (role_id, user_id)
                VALUES
                    (CASE WHEN i % 2 = 0 THEN 1 ELSE 2 END, i);
            END LOOP;
    END $$;

-- Populating offers table with 5000 offers
DO $$
    DECLARE
        i INT;
    BEGIN
        FOR i IN 1..5000 LOOP
                INSERT INTO offers (id, negotiable, description, image_url, title)
                VALUES
                    (i, (i % 2 = 0), 'Description of offer ' || i, 'http://example.com/image' || i, 'Offer ' || i);
            END LOOP;
    END $$;

-- Populating job table with 2000 jobs
DO $$
    DECLARE
        i INT;
    BEGIN
        FOR i IN 1..2000 LOOP
                INSERT INTO job (id, salary, localization)
                VALUES
                    (i, 30000 + i * 10, 'Location' || i);
            END LOOP;
    END $$;

-- Populating service table with 3000 services
DO $$
    DECLARE
        i INT;
    BEGIN
        FOR i IN 1..3000 LOOP
                INSERT INTO service (id, price)
                VALUES
                    (2000 + i, 50 + i * 5);
            END LOOP;
    END $$;

-- Populating applications table with 10000 applications
DO $$
    DECLARE
        i INT;
    BEGIN
        FOR i IN 1..10000 LOOP
                INSERT INTO applications (id, offer_id, user_id)
                VALUES
                    (i, (i % 5000) + 1, (i % 1000) + 1);
            END LOOP;
    END $$;

-- Populating favorite_users table
DO $$
    DECLARE
        i INT;
    BEGIN
        FOR i IN 1..2000 LOOP
                INSERT INTO favorite_users (user_id, favorite_user_id)
                VALUES
                    ((i % 1000) + 1, ((i + 1) % 1000) + 1);
            END LOOP;
    END $$;

-- Populating favorites_offers table
DO $$
    DECLARE
        i INT;
    BEGIN
        FOR i IN 1..5000 LOOP
                INSERT INTO favorites_offers (user_id, offer_id)
                VALUES
                    ((i % 1000) + 1, (i % 5000) + 1);
            END LOOP;
    END $$;

-- Populating chat_message table with 10000 messages
DO $$
    DECLARE
        i INT;
    BEGIN
        FOR i IN 1..10000 LOOP
                INSERT INTO chat_message (id, status, timestamp, content, receiver, sender)
                VALUES
                    (i, i % 2, NOW(), 'Message content ' || i, 'user' || ((i % 1000) + 1) || '@example.com', 'user' || (((i + 1) % 1000) + 1) || '@example.com');
            END LOOP;
    END $$;

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