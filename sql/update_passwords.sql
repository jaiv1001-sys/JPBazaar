UPDATE users 
SET password = '$2a$12$euLrrWN50hqYjD7Qblib5uuIo4GiGwfQQcbjr6xdnKlFanfmzgXWy', 
    role = 'ADMIN', 
    enabled = true 
WHERE email = 'admin@jpbazaar.com';

UPDATE users 
SET password = '$2a$12$1jo5SwPAVuDBoXJkXnuGGeNqTRIehfdAObZBbUhFyfwsUn4cH7c2i', 
    role = 'CUSTOMER', 
    enabled = true 
WHERE email = 'customer@jpbazaar.com';
