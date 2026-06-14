-- =====================================================
-- SKRYPT DANYCH TESTOWYCH (idempotentny, z transakcją, bez sztywnych ID)
-- =====================================================
-- Uruchomienie w bloku PL/SQL z obsługą błędów i transakcją
DECLARE
v_student_id NUMBER;
    v_grupa_id   NUMBER;
    v_typ_kolokwium NUMBER;
    v_typ_projekt NUMBER;

    -- Stała data dla testów (zamiast SYSDATE)
    v_test_date DATE := DATE '2025-01-15';
BEGIN
    -- Rozpoczęcie transakcji
SAVEPOINT start_data;

-- =====================================================
-- 1. KIERUNKI (wstaw tylko jeśli nie istnieją)
-- =====================================================
MERGE INTO Kierunki k
    USING (SELECT 'Informatyka Stosowana' AS nazwa, 'INF' AS kod, 1 AS stopien FROM DUAL UNION ALL
           SELECT 'Automatyka i Robotyka', 'AUT', 1 FROM DUAL UNION ALL
           SELECT 'Inżynieria Danych', 'DAT', 2 FROM DUAL UNION ALL
           SELECT 'Cyberbezpieczeństwo', 'CYB', 1 FROM DUAL) src
    ON (k.kod_kierunku = src.kod)
    WHEN NOT MATCHED THEN
        INSERT (nazwa, kod_kierunku, stopien) VALUES (src.nazwa, src.kod, src.stopien);

-- =====================================================
-- 2. PRZEDMIOTY (z użyciem podzapytań do pobrania ID kierunku)
-- =====================================================
MERGE INTO Przedmioty p
    USING (SELECT 'INF-01' AS kod, 'Programowanie Obiektowe' AS nazwa, 5 AS ects, 'INF' AS kod_kier FROM DUAL UNION ALL
           SELECT 'INF-02', 'Bazy Danych', 6, 'INF' FROM DUAL UNION ALL
           SELECT 'INF-03', 'Algorytmy i Struktury Danych', 5, 'INF' FROM DUAL UNION ALL
           SELECT 'AUT-01', 'Teoria Sterowania', 5, 'AUT' FROM DUAL UNION ALL
           SELECT 'AUT-02', 'Sensoryka i Aktuatory', 4, 'AUT' FROM DUAL UNION ALL
           SELECT 'DAT-01', 'Analiza Statystyczna', 5, 'DAT' FROM DUAL UNION ALL
           SELECT 'DAT-02', 'Hurtownie Danych', 6, 'DAT' FROM DUAL UNION ALL
           SELECT 'CYB-01', 'Kryptografia', 5, 'CYB' FROM DUAL UNION ALL
           SELECT 'CYB-02', 'Bezpieczeństwo Sieci', 5, 'CYB' FROM DUAL) src
    ON (p.kod_przedmiotu = src.kod)
    WHEN NOT MATCHED THEN
        INSERT (kod_przedmiotu, nazwa, ects, kierunek_id)
            VALUES (src.kod, src.nazwa, src.ects, (SELECT id_kierunku FROM Kierunki WHERE kod_kierunku = src.kod_kier));

-- =====================================================
-- 3. PRACOWNICY (wstaw tylko jeśli brak email)
-- =====================================================
MERGE INTO Pracownicy p
    USING (SELECT 'andrzej.kowalski@uczelnia.pl' AS email, 'Andrzej' AS imie, 'Kowalski' AS nazwisko, 'Dr inż.' AS tytul FROM DUAL UNION ALL
           SELECT 'maria.zielinska@uczelnia.pl', 'Maria', 'Zielińska', 'Prof. dr hab.' FROM DUAL UNION ALL
           SELECT 'jan.nowak@uczelnia.pl', 'Jan', 'Nowak', 'Mgr inż.' FROM DUAL UNION ALL
           SELECT 'krzysztof.mazur@uczelnia.pl', 'Krzysztof', 'Mazur', 'Dr hab.' FROM DUAL UNION ALL
           SELECT 'barbara.wozniak@uczelnia.pl', 'Barbara', 'Woźniak', 'Dr' FROM DUAL) src
    ON (p.email = src.email)
    WHEN NOT MATCHED THEN
        INSERT (imie, nazwisko, tytul_naukowy, email) VALUES (src.imie, src.nazwisko, src.tytul, src.email);

-- =====================================================
-- 4. SALE (wstaw tylko jeśli nie istnieje numer sali)
-- =====================================================
MERGE INTO Sale s
    USING (SELECT '104-A (Laboratorium)' AS numer, 30 AS poj FROM DUAL UNION ALL
           SELECT '215-B (Aula)', 120 FROM DUAL UNION ALL
           SELECT '03-Centrum', 15 FROM DUAL UNION ALL
           SELECT '301-C', 45 FROM DUAL UNION ALL
           SELECT '111-A', 25 FROM DUAL) src
    ON (s.numer_sali = src.numer)
    WHEN NOT MATCHED THEN
        INSERT (numer_sali, pojemnosc) VALUES (src.numer, src.poj);

-- =====================================================
-- 5. GRUPY (użycie podzapytań zamiast sztywnych ID)
-- =====================================================
-- Grupa 1: INF-01, pracownik o emailu andrzej.kowalski@uczelnia.pl
INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika)
SELECT 'Grupa IO-11 (Rok 1)', p.id_przedmiotu, pr.id_pracownika
FROM Przedmioty p, Pracownicy pr
WHERE p.kod_przedmiotu = 'INF-01' AND pr.email = 'andrzej.kowalski@uczelnia.pl'
  AND NOT EXISTS (SELECT 1 FROM Grupy g WHERE g.nazwa_grupy = 'Grupa IO-11 (Rok 1)');

INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika)
SELECT 'Grupa IO-12 (Rok 1)', p.id_przedmiotu, pr.id_pracownika
FROM Przedmioty p, Pracownicy pr
WHERE p.kod_przedmiotu = 'INF-02' AND pr.email = 'maria.zielinska@uczelnia.pl'
  AND NOT EXISTS (SELECT 1 FROM Grupy g WHERE g.nazwa_grupy = 'Grupa IO-12 (Rok 1)');

INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika)
SELECT 'Grupa AR-21 (Rok 2)', p.id_przedmiotu, pr.id_pracownika
FROM Przedmioty p, Pracownicy pr
WHERE p.kod_przedmiotu = 'AUT-01' AND pr.email = 'jan.nowak@uczelnia.pl'
  AND NOT EXISTS (SELECT 1 FROM Grupy g WHERE g.nazwa_grupy = 'Grupa AR-21 (Rok 2)');

INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika)
SELECT 'Grupa ID-31 (Rok 3)', p.id_przedmiotu, pr.id_pracownika
FROM Przedmioty p, Pracownicy pr
WHERE p.kod_przedmiotu = 'DAT-01' AND pr.email = 'krzysztof.mazur@uczelnia.pl'
  AND NOT EXISTS (SELECT 1 FROM Grupy g WHERE g.nazwa_grupy = 'Grupa ID-31 (Rok 3)');

INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika)
SELECT 'Grupa CYB-11 (Rok 1)', p.id_przedmiotu, pr.id_pracownika
FROM Przedmioty p, Pracownicy pr
WHERE p.kod_przedmiotu = 'CYB-01' AND pr.email = 'barbara.wozniak@uczelnia.pl'
  AND NOT EXISTS (SELECT 1 FROM Grupy g WHERE g.nazwa_grupy = 'Grupa CYB-11 (Rok 1)');

-- =====================================================
-- 6. STUDENCI (usuwanie tylko dla zachowania czystości, ale lepiej MERGE)
-- =====================================================
-- Usuwamy starego studenta (jeśli istnieje) aby uniknąć konfliktu email/indeks
DELETE FROM STUDENCI WHERE email = 'student' OR nr_indeksu = 's12345';

INSERT INTO STUDENCI (imie, nazwisko, nr_indeksu, email, data_urodzenia, secure_token)
VALUES ('Jan', 'Kowalski', 's12345', 'student', DATE '2000-01-01', 'temp123');

-- =====================================================
-- 7. UZYTKOWNICY, ROLE, UZYTKOWNICY_ROLE (idempotentnie)
-- =====================================================
    /*
-- Usuwamy stare powiązania
DELETE FROM Uzytkownicy_Role WHERE id_uzytkownika IN (SELECT id_uzytkownika FROM Uzytkownicy WHERE username = 'student');
DELETE FROM Uzytkownicy WHERE username = 'student';
DELETE FROM Role WHERE nazwa_roli = 'STUDENT';

INSERT INTO Uzytkownicy (username, password, email, czy_aktywny)
VALUES ('student', 'student123', 'student', 'T');

INSERT INTO Role (nazwa_roli) VALUES ('STUDENT');

INSERT INTO Uzytkownicy_Role (id_uzytkownika, id_roli)
SELECT u.id_uzytkownika, r.id_roli
FROM Uzytkownicy u, Role r
WHERE u.username = 'student' AND r.nazwa_roli = 'STUDENT';
     */
-- =====================================================
-- 8. SŁOWNIK OCEN (tylko jeśli brak)
-- =====================================================
MERGE INTO Slownik_Ocen s
    USING (SELECT 'Kolokwium' AS nazwa, 0.5 AS waga FROM DUAL UNION ALL
           SELECT 'Egzamin', 0.8 FROM DUAL UNION ALL
           SELECT 'Projekt', 0.4 FROM DUAL) src
    ON (s.nazwa = src.nazwa)
    WHEN NOT MATCHED THEN
        INSERT (nazwa, waga) VALUES (src.nazwa, src.waga);

-- Pobranie ID typów ocen do zmiennych (do późniejszego użycia)
BEGIN
SELECT id_typu INTO v_typ_kolokwium FROM Slownik_Ocen WHERE nazwa = 'Kolokwium';
SELECT id_typu INTO v_typ_projekt FROM Slownik_Ocen WHERE nazwa = 'Projekt';
EXCEPTION WHEN NO_DATA_FOUND THEN
        v_typ_kolokwium := NULL;
        v_typ_projekt := NULL;
END;

    -- =====================================================
    -- 9. ZAPISY i OCENY (tylko jeśli jeszcze nie istnieją)
    -- =====================================================
    -- Zapisz studenta do grupy (jeśli jeszcze nie zapisany)
INSERT INTO Zapisy (id_studenta, id_grupy, data_zapisu, status)
SELECT s.id_studenta, g.id_grupy, v_test_date, 'Aktywny'
FROM STUDENCI s, GRUPY g
WHERE s.email = 'student'
  AND g.nazwa_grupy = 'Grupa IO-12 (Rok 1)'
  AND NOT EXISTS (
    SELECT 1 FROM Zapisy z
    WHERE z.id_studenta = s.id_studenta AND z.id_grupy = g.id_grupy
);

-- Pobranie ID zapisu
SELECT z.id_zapisu INTO v_grupa_id
FROM Zapisy z
         JOIN STUDENCI s ON z.id_studenta = s.id_studenta
         JOIN GRUPY g ON z.id_grupy = g.id_grupy
WHERE s.email = 'student' AND g.nazwa_grupy = 'Grupa IO-12 (Rok 1)'
  AND ROWNUM = 1;

-- Wystaw ocenę (Kolokwium) jeśli jeszcze nie ma
IF v_typ_kolokwium IS NOT NULL THEN
        INSERT INTO Oceny (id_zapisu, id_typu, wartosc, data_wystawienia, komentarz)
SELECT v_grupa_id, v_typ_kolokwium, 4.5, v_test_date, 'Przykładowa ocena z Baz Danych'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM Oceny WHERE id_zapisu = v_grupa_id AND id_typu = v_typ_kolokwium
);
END IF;

    -- Wystaw drugą ocenę (Projekt)
    IF v_typ_projekt IS NOT NULL THEN
        INSERT INTO Oceny (id_zapisu, id_typu, wartosc, data_wystawienia, komentarz)
SELECT v_grupa_id, v_typ_projekt, 5.0, v_test_date, 'Projekt zaliczony'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM Oceny WHERE id_zapisu = v_grupa_id AND id_typu = v_typ_projekt
);
END IF;

    -- Zatwierdzenie wszystkich zmian
COMMIT;
DBMS_OUTPUT.PUT_LINE('Dane testowe zostały pomyślnie wstawione.');

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK TO start_data;
        DBMS_OUTPUT.PUT_LINE('Błąd podczas wstawiania danych: ' || SQLERRM);
        RAISE;
END;
/