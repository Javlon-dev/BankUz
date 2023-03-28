
-- admin profile
DO
$do$
    DECLARE
    BEGIN
        IF (select count(*)
            from profile
            where phone_number = '+998903266526'
              and deleted_date is null) > 0
        THEN

        ELSE
            insert into profile (id, created_date, name, phone_number, role, status, surname)
            values (gen_random_uuid(), now(), 'Javlon', '+998903266526', 'ROLE_ADMIN', 'ACTIVE', 'Dev');
        END IF;
    END;
$do$;

-- 1. uzcard
DO
$do$
    DECLARE
    BEGIN
        IF (select count(*)
            from card
            where pan = '8600631928374650'
              and deleted_date is null) > 0
        THEN

        ELSE
            insert into bank (id, created_date, mfo_code, profile_id)
            values (gen_random_uuid(), now(), '860063', '+998903266526');
            insert into card (id, created_date, balance, masked_pan, pan, status, type, bank_id, profile_id)
            values (gen_random_uuid(), now(), 0, '860063******4650', '8600631928374650', 'NATIVE', 'SUM', '860063',
                    '+998903266526');
        END IF;
    END;
$do$;

-- 2. humo_card
DO
$do$
    DECLARE
    BEGIN
        IF (select count(*)
            from card
            where pan = '9860241928374650'
              and deleted_date is null) > 0
        THEN

        ELSE
            insert into bank (id, created_date, mfo_code, profile_id)
            values (gen_random_uuid(), now(), '986024', '+998903266526');
            insert into card (id, created_date, balance, masked_pan, pan, status, type, bank_id, profile_id)
            values (gen_random_uuid(), now(), 0, '986024******4650', '9860241928374650', 'NATIVE', 'SUM', '986024',
                    '+998903266526');
        END IF;
    END;
$do$;

-- 3. visa_card
DO
$do$
    DECLARE
    BEGIN
        IF (select count(*)
            from card
            where pan = '4604721928374650'
              and deleted_date is null) > 0
        THEN

        ELSE
            insert into bank (id, created_date, mfo_code, profile_id)
            values (gen_random_uuid(), now(), '460472', '+998903266526');
            insert into card (id, created_date, balance, masked_pan, pan, cvv_code, status, type, bank_id, profile_id)
            values (gen_random_uuid(), now(), 0, '460472******4650', '4604721928374650', '007', 'NATIVE', 'USD',
                    '460472', '+998903266526');
        END IF;
    END;
$do$;
