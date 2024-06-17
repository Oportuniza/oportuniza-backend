-- Limpeza dos dados das tabelas
DELETE FROM users_roles;
DELETE FROM service;
DELETE FROM job;
DELETE FROM favorites_offers;
DELETE FROM documents;
DELETE FROM applications;
DELETE FROM offers;
DELETE FROM favorite_users;
DELETE FROM reviews;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM chat_message;
DELETE FROM chat_notification;
DELETE FROM general_notification;

INSERT INTO users (id, average_rating, county, created_at, district, email, name, password, phone_number, resume_url, review_count, updated_at)
VALUES
    (1, 4.5, 'São Paulo', '2024-01-01 00:00:00', 'SP', 'email@example.com', 'João Silva', 'senha123', '11987654321', 'http://resumeurl.com/resume.pdf', 10, '2024-06-01 00:00:00'),
    (2, 3.8, 'Braga', '2024-01-01 00:00:00', 'Braga', 'email2@example.com', 'Gui Silva', 'senha123', '999999999999', 'http://resumeurl.com/resume2.pdf', 8, '2024-06-01 00:00:00');

-- Generate 50000 chat messages with random sender, receiver and timestamps
DO
$$
    DECLARE
        counter INTEGER := 1;
        sender_id INTEGER;
        receiver_id INTEGER;
        message_time TIMESTAMP;
    BEGIN
        WHILE counter <= 50000 LOOP
                -- Generate random sender and receiver id between 1 and 100
                sender_id := floor(random() * 100 + 1)::INTEGER;
                receiver_id := floor(random() * 100 + 1)::INTEGER;

                -- Ensure sender and receiver are not the same
                WHILE sender_id = receiver_id LOOP
                        receiver_id := floor(random() * 100 + 1)::INTEGER;
                    END LOOP;

                -- Generate random timestamp within the last 30 days
                message_time := NOW() - INTERVAL '1 day' * floor(random() * 30)::INTEGER;

                INSERT INTO chat_message (id, content, receiver, sender, status, timestamp)
                VALUES (counter, 'Message ' || counter, 'user' || receiver_id, 'user' || sender_id, 1, message_time);

                counter := counter + 1;
            END LOOP;
    END;
$$;

-- Generate 500 job offers
DO
$$
    DECLARE
        counter INTEGER := 1;
    BEGIN
        WHILE counter <= 500 LOOP
                INSERT INTO offers (id, description, image_url, negotiable, title, user_id)
                VALUES (counter, 'Job offer ' || counter, 'http://imageurl.com/image' || counter || '.jpg', TRUE, 'Unique Job Offer ' || counter, 1);

                INSERT INTO job (localization, salary, working_model, working_regime, id)
                VALUES ('Unique Location ' || counter, 5000.00, 'Full-Time', 'Flexible Hours', counter);

                counter := counter + 1;
            END LOOP;
    END;
$$;

-- Generate 500 service offers
DO
$$
    DECLARE
        counter INTEGER := 501;
    BEGIN
        WHILE counter <= 1000 LOOP
                INSERT INTO offers (id, description, image_url, negotiable, title, user_id)
                VALUES (counter, 'Service offer ' || (counter - 500), 'http://imageurl.com/image' || (counter - 500) || '.jpg', TRUE, 'Unique Service Offer ' || (counter - 500), 1);

                INSERT INTO service (price, id)
                VALUES (250.00, counter);

                counter := counter + 1;
            END LOOP;
    END;
$$;
