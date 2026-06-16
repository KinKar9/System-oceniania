BEGIN;

-- 1. Role
INSERT INTO rola (nazwa_roli) VALUES
                                  ('ADMIN'),
                                  ('PRACOWNIK'),
                                  ('STUDENT');

-- 2. Słownik ocen
INSERT INTO slownik_ocen (nazwa, waga) VALUES
                                           ('Kolokwium', 0.5),
                                           ('Egzamin', 0.8),
                                           ('Projekt', 0.4);

-- 3. Pracownicy
INSERT INTO pracownicy (imie, nazwisko, tytul_naukowy, email) VALUES
                                                                  ('Andrzej', 'Kowalski', 'Dr inż.', 'andrzej.kowalski@uczelnia.pl'),
                                                                  ('Maria', 'Zielińska', 'Prof. dr hab.', 'maria.zielinska@uczelnia.pl'),
                                                                  ('Jan', 'Nowak', 'Mgr inż.', 'jan.nowak@uczelnia.pl'),
                                                                  ('Krzysztof', 'Mazur', 'Dr hab.', 'krzysztof.mazur@uczelnia.pl'),
                                                                  ('Barbara', 'Woźniak', 'Dr', 'barbara.wozniak@uczelnia.pl')
    ON CONFLICT (email) DO NOTHING;

INSERT INTO pracownicy (imie, nazwisko, tytul_naukowy, email)
SELECT
    'Prac' || i,
    'Nazwisko' || i,
    CASE (i % 3)
        WHEN 0 THEN 'Dr inż.'
        WHEN 1 THEN 'Mgr'
        ELSE 'Prof. dr hab.'
        END,
    'pracownik' || i || '@uczelnia.pl'
FROM generate_series(6, 20) i
    ON CONFLICT (email) DO NOTHING;

-- 4. Sale
INSERT INTO sale (numer_sali, pojemnosc, typ_sali) VALUES
                                                       ('104-A', 30, 'LABORATORYJNA'),
                                                       ('215-B', 120, 'WYKLADOWA'),
                                                       ('03-Centrum', 15, 'SEMINARYJNA'),
                                                       ('301-C', 45, 'WYKLADOWA'),
                                                       ('111-A', 25, 'KOMPUTEROWA')
    ON CONFLICT (numer_sali) DO NOTHING;


-- 5. Kierunki

INSERT INTO kierunki (nazwa, kod_kierunku, stopien, deleted)
SELECT
    'Kierunek ' || i,
    'K' || LPAD(i::TEXT, 3, '0'),
    CASE (i % 2)
        WHEN 0 THEN 'INZYNIER_LICENCJAT'
        ELSE 'MAGISTER'
        END,
    false
FROM generate_series(1, 50) i
    ON CONFLICT (kod_kierunku) DO NOTHING;

-- 6. Przedmioty

INSERT INTO przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id)
SELECT
    k.kod_kierunku || '-' || LPAD(p.nr::TEXT, 2, '0'),
    'Przedmiot ' || p.nr || ' (' || k.nazwa || ')',
    3 + (random() * 7)::INT,
    k.id_kierunku
FROM kierunki k
         CROSS JOIN LATERAL generate_series(1, floor(3 + random() * 3)::INT) AS p(nr)
ON CONFLICT (kod_przedmiotu) DO NOTHING;


-- 7. Grupy

INSERT INTO grupy (nazwa_grupy, limit_miejsc, id_przedmiotu, id_pracownika)
SELECT
    p.kod_przedmiotu || '-G' || g.nr,
    15 + (random() * 25)::INT,
    p.id_przedmiotu,
    (SELECT id_pracownika FROM pracownicy ORDER BY random() LIMIT 1)
FROM przedmioty p
    CROSS JOIN LATERAL generate_series(1, floor(1 + random() * 2)::INT) AS g(nr);


-- 8. Studenci

INSERT INTO studenci (imie, nazwisko, nr_indeksu, email, data_urodzenia, secure_token, czy_aktywny)
SELECT
    'Imię' || i,
    'Nazwisko' || i,
    's' || i,
    'student' || i || '@student.uczelnia.pl',
    ('1995-01-01'::DATE + (random() * 3650)::INT),
    'token_' || i,
    TRUE   -- ← dodaj aktywny status
FROM generate_series(1, 300) i
    ON CONFLICT (nr_indeksu) DO NOTHING;


-- 9. Zapisy

DO $$
DECLARE
s RECORD;
    how_many INT;
    g_ids INT[];
BEGIN
FOR s IN SELECT id_studenta FROM studenci LOOP
    how_many := 1 + floor(random() * 3);   -- 1, 2 lub 3
SELECT ARRAY_AGG(id_grupy ORDER BY random()) INTO g_ids
FROM grupy
         LIMIT how_many;
FOR i IN 1..how_many LOOP
            INSERT INTO zapisy (id_studenta, id_grupy, data_zapisu, status)
            VALUES (s.id_studenta, g_ids[i], CURRENT_DATE - (random() * 180)::INT, 'AKTYWNY')
            ON CONFLICT (id_studenta, id_grupy) DO NOTHING;
END LOOP;
END LOOP;
END $$;

-- 10. Oceny
DO $$
DECLARE
z RECORD;
    t_id INT;
    grade_val NUMERIC(2,1);
BEGIN
FOR z IN SELECT id_zapisu, data_zapisu FROM zapisy LOOP
SELECT id_typu INTO t_id FROM slownik_ocen ORDER BY random() LIMIT 1;
grade_val := 2.0 + (random() * 3)::NUMERIC(2,1);
INSERT INTO oceny (id_zapisu, id_typu, wartosc, data_wystawienia, komentarz)
VALUES (
           z.id_zapisu,
           t_id,
           grade_val,
           z.data_zapisu + (random() * (CURRENT_DATE - z.data_zapisu))::INT,
           'Wygenerowana ocena'
       ) ON CONFLICT (id_zapisu, id_typu) DO NOTHING;
END LOOP;
END $$;

COMMIT;