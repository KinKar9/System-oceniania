-- =====================================================
-- 1. KIERUNKI (jeśli nie istnieją – użyj MERGE lub DELETE)
-- =====================================================
INSERT INTO Kierunki (nazwa, kod_kierunku, stopien) VALUES ('Informatyka Stosowana', 'INF', 1);
INSERT INTO Kierunki (nazwa, kod_kierunku, stopien) VALUES ('Automatyka i Robotyka', 'AUT', 1);
INSERT INTO Kierunki (nazwa, kod_kierunku, stopien) VALUES ('Inżynieria Danych', 'DAT', 2);
INSERT INTO Kierunki (nazwa, kod_kierunku, stopien) VALUES ('Cyberbezpieczeństwo', 'CYB', 1);

-- =====================================================
-- 2. PRZEDMIOTY (zakładamy, że kierunek_id = 1..4 zgodnie z powyższym)
-- =====================================================
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('INF-01', 'Programowanie Obiektowe', 5, 1);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('INF-02', 'Bazy Danych', 6, 1);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('INF-03', 'Algorytmy i Struktury Danych', 5, 1);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('AUT-01', 'Teoria Sterowania', 5, 2);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('AUT-02', 'Sensoryka i Aktuatory', 4, 2);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('DAT-01', 'Analiza Statystyczna', 5, 3);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('DAT-02', 'Hurtownie Danych', 6, 3);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('CYB-01', 'Kryptografia', 5, 4);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('CYB-02', 'Bezpieczeństwo Sieci', 5, 4);

-- =====================================================
-- 3. PRACOWNICY
-- =====================================================
INSERT INTO Pracownicy (imie, nazwisko, tytul_naukowy, email) VALUES ('Andrzej', 'Kowalski', 'Dr inż.', 'andrzej.kowalski@uczelnia.pl');
INSERT INTO Pracownicy (imie, nazwisko, tytul_naukowy, email) VALUES ('Maria', 'Zielińska', 'Prof. dr hab.', 'maria.zielinska@uczelnia.pl');
INSERT INTO Pracownicy (imie, nazwisko, tytul_naukowy, email) VALUES ('Jan', 'Nowak', 'Mgr inż.', 'jan.nowak@uczelnia.pl');
INSERT INTO Pracownicy (imie, nazwisko, tytul_naukowy, email) VALUES ('Krzysztof', 'Mazur', 'Dr hab.', 'krzysztof.mazur@uczelnia.pl');
INSERT INTO Pracownicy (imie, nazwisko, tytul_naukowy, email) VALUES ('Barbara', 'Woźniak', 'Dr', 'barbara.wozniak@uczelnia.pl');

DECLARE
v_imiona   VARCHAR2(200) := 'Anna,Jan,Maria,Piotr,Katarzyna,Andrzej,Małgorzata,Tomasz,Paweł,Agnieszka,Krzysztof,Barbara,Michał,Ewa,Adam,Karol,Elżbieta,Zofia';
    v_nazwiska VARCHAR2(300) := 'Kowalski,Wiśniewski,Dąbrowski,Kamińska,Nowak,Zieliński,Wójcik,Kozłowski,Jankowski,Mazur,Krawczyk,Piotrowski,Grabowski,Zawadzka,Szymański';
    v_tytuly   VARCHAR2(100) := 'Dr inż.,Prof. dr hab.,Mgr inż.,Dr hab.,Dr,mgr,lic.';
    v_i        NUMBER;
    v_imie     VARCHAR2(50);
    v_nazwisko VARCHAR2(100);
    v_tytul    VARCHAR2(50);
    v_email    VARCHAR2(100);
    v_licznik  NUMBER := 0;
BEGIN
SELECT COUNT(*) INTO v_licznik FROM Pracownicy;
FOR v_i IN 1..(100 - v_licznik) LOOP
        v_imie := REGEXP_SUBSTR(v_imiona, '[^,]+', 1, MOD(v_i, 15) + 1);
        v_nazwisko := REGEXP_SUBSTR(v_nazwiska, '[^,]+', 1, MOD(v_i, 15) + 1);
        v_tytul := REGEXP_SUBSTR(v_tytuly, '[^,]+', 1, MOD(v_i, 7) + 1);
        v_email := LOWER(v_imie || '.' || v_nazwisko || (v_i+200) || '@uczelnia.pl');
BEGIN
INSERT INTO Pracownicy (imie, nazwisko, tytul_naukowy, email)
VALUES (v_imie, v_nazwisko, v_tytul, v_email);
EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL;
END;
END LOOP;
COMMIT;
DBMS_OUTPUT.PUT_LINE('Liczba pracowników po dodaniu: ' || (SELECT COUNT(*) FROM Pracownicy));
END;
/
-- =====================================================
-- 4. SALE
-- =====================================================
INSERT INTO Sale (numer_sali, pojemnosc) VALUES ('104-A (Laboratorium)', 30);
INSERT INTO Sale (numer_sali, pojemnosc) VALUES ('215-B (Aula)', 120);
INSERT INTO Sale (numer_sali, pojemnosc) VALUES ('03-Centrum', 15);
INSERT INTO Sale (numer_sali, pojemnosc) VALUES ('301-C', 45);
INSERT INTO Sale (numer_sali, pojemnosc) VALUES ('111-A', 25);

-- =====================================================
-- 5. GRUPY (z odpowiednimi ID – sprawdź czy przedmioty i pracownicy mają ID zgodne z tymi wartościami)
-- =====================================================
INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika) VALUES ('Grupa IO-11 (Rok 1)', 1, 1);
INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika) VALUES ('Grupa IO-12 (Rok 1)', 2, 2);
INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika) VALUES ('Grupa AR-21 (Rok 2)', 4, 3);
INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika) VALUES ('Grupa ID-31 (Rok 3)', 6, 4);
INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika) VALUES ('Grupa CYB-11 (Rok 1)', 8, 5);

DECLARE
v_id_przedmiot NUMBER;
    v_id_prac      NUMBER;
    v_nazwa_grupy  VARCHAR2(50);
    v_limit        NUMBER;
    v_grupa_licznik NUMBER;
    v_nowych_grup NUMBER := 0;
BEGIN
FOR v_id_przedmiot IN (SELECT id_przedmiotu FROM Przedmioty ORDER BY id_przedmiotu) LOOP
        FOR v_grupa_licznik IN 1..6 LOOP
            v_nazwa_grupy := 'Grupa_' || v_id_przedmiot || '_' || v_grupa_licznik;
            -- Sprawdź czy grupa już istnieje
BEGIN
SELECT 1 INTO v_nowych_grup FROM Grupy WHERE nazwa_grupy = v_nazwa_grupy AND ROWNUM=1;
EXCEPTION WHEN NO_DATA_FOUND THEN
                -- Losowy pracownik
SELECT id_pracownika INTO v_id_prac
FROM (SELECT id_pracownika FROM Pracownicy ORDER BY DBMS_RANDOM.VALUE)
WHERE ROWNUM = 1;
v_limit := 30 + TRUNC(DBMS_RANDOM.VALUE(0, 51));
INSERT INTO Grupy (nazwa_grupy, limit_miejsc, id_przedmiotu, id_pracownika)
VALUES (v_nazwa_grupy, v_limit, v_id_przedmiot, v_id_prac);
END;
END LOOP;
END LOOP;
COMMIT;
DBMS_OUTPUT.PUT_LINE('Łączna liczba grup: ' || (SELECT COUNT(*) FROM Grupy));
END;
/

-- =====================================================
-- 6. STUDENCI
-- =====================================================
-- Usuń ewentualnego istniejącego studenta (żeby uniknąć błędu)
DELETE FROM STUDENCI WHERE email = 'student';
-- Dodaj prawidłowego studenta (bez duplikacji)
INSERT INTO STUDENCI (imie, nazwisko, nr_indeksu, email, data_urodzenia, secure_token)
VALUES ('Jan', 'Kowalski', 's12345', 'student', DATE '2000-01-01', 'temp123');

DECLARE
v_obecni NUMBER;
    v_i      NUMBER;
    v_imie   VARCHAR2(50);
    v_nazwisko VARCHAR2(100);
    v_indeks VARCHAR2(10);
    v_email  VARCHAR2(100);
    v_data_ur DATE;
    v_token  VARCHAR2(255);
BEGIN
SELECT COUNT(*) INTO v_obecni FROM STUDENCI;
FOR v_i IN 1..(1000 - v_obecni) LOOP
        v_imie := 'Imię' || v_i;
        v_nazwisko := 'Nazwisko' || v_i;
        v_indeks := 's' || TO_CHAR(30000 + v_i);
        v_email := 'student_' || v_i || '_new@example.com';
        v_data_ur := TO_DATE('1995-01-01', 'YYYY-MM-DD') + TRUNC(DBMS_RANDOM.VALUE(0, 3650));
        v_token := DBMS_RANDOM.STRING('X', 20);
BEGIN
INSERT INTO STUDENCI (imie, nazwisko, nr_indeksu, email, data_urodzenia, secure_token)
VALUES (v_imie, v_nazwisko, v_indeks, v_email, v_data_ur, v_token);
EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL;
END;
END LOOP;
COMMIT;
DBMS_OUTPUT.PUT_LINE('Liczba studentów po dodaniu: ' || (SELECT COUNT(*) FROM STUDENCI));
END;
/

-- =====================================================
-- 7. UZYTKOWNICY, ROLE, UZYTKOWNICY_ROLE (dla logowania)
-- =====================================================
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

-- =====================================================
-- 8. DODATKOWE DANE: Typy ocen, Zapisy, Oceny
-- =====================================================
-- Słownik ocen (jeśli puste)
INSERT INTO Slownik_Ocen (nazwa, waga) VALUES ('Kolokwium', 0.5);
INSERT INTO Slownik_Ocen (nazwa, waga) VALUES ('Egzamin', 0.8);
INSERT INTO Slownik_Ocen (nazwa, waga) VALUES ('Projekt', 0.4);

-- Zapisz studenta do istniejącej grupy (np. grupa IO-12 – Bazy Danych, id_grupy = 2)
INSERT INTO Zapisy (id_studenta, id_grupy, data_zapisu, status)
SELECT s.id_studenta, g.id_grupy, SYSDATE, 'Aktywny'
FROM STUDENCI s, GRUPY g
WHERE s.email = 'student' AND g.nazwa_grupy = 'Grupa IO-12 (Rok 1)';

DECLARE
v_id_stud NUMBER;
    v_id_grupy NUMBER;
    v_liczba_grup NUMBER;
    v_semestr VARCHAR2(20) := '2024L';
    v_status VARCHAR2(20) := 'Aktywny';
    TYPE tab_grup IS TABLE OF NUMBER INDEX BY BINARY_INTEGER;
    v_grupy tab_grup;
    v_g_id NUMBER;
    v_counter NUMBER;
BEGIN
SELECT id_grupy BULK COLLECT INTO v_grupy FROM Grupy;

FOR stud_rec IN (SELECT id_studenta FROM STUDENCI) LOOP
        v_id_stud := stud_rec.id_studenta;
        v_liczba_grup := 3 + TRUNC(DBMS_RANDOM.VALUE(0, 3));
        v_counter := 0;

FOR i IN 1..v_liczba_grup LOOP
            LOOP
                v_g_id := v_grupy(TRUNC(DBMS_RANDOM.VALUE(1, v_grupy.COUNT)) + 1);
BEGIN
INSERT INTO Zapisy (id_studenta, id_grupy, data_zapisu, status, semestr)
VALUES (v_id_stud, v_g_id, SYSDATE - DBMS_RANDOM.VALUE(0, 100), v_status, v_semestr);
EXIT;
EXCEPTION
                    WHEN DUP_VAL_ON_INDEX THEN NULL;
END;
END LOOP;
            v_counter := v_counter + 1;
            IF v_counter > 100 THEN EXIT; END IF;
END LOOP;
END LOOP;
COMMIT;
DBMS_OUTPUT.PUT_LINE('Liczba zapisów: ' || (SELECT COUNT(*) FROM Zapisy));
END;
/

-- Wystaw ocenę (przykładowa, typ 'Kolokwium')
INSERT INTO Oceny (id_zapisu, id_typu, wartosc, data_wystawienia, komentarz)
SELECT z.id_zapisu, t.id_typu, 4.5, SYSDATE, 'Przykładowa ocena z Baz Danych'
FROM Zapisy z, Slownik_Ocen t
WHERE z.id_studenta = (SELECT id_studenta FROM STUDENCI WHERE email='student')
  AND t.nazwa = 'Kolokwium';



-- Opcjonalnie: druga ocena (inny typ)
INSERT INTO Oceny (id_zapisu, id_typu, wartosc, data_wystawienia, komentarz)
SELECT z.id_zapisu, t.id_typu, 5.0, SYSDATE, 'Projekt zaliczony'
FROM Zapisy z, Slownik_Ocen t
WHERE z.id_studenta = (SELECT id_studenta FROM STUDENCI WHERE email='student')
  AND t.nazwa = 'Projekt';

COMMIT;


-- Nowe oceny: dla każdego zapisu 2-3 oceny różnych typów
DECLARE
v_id_zapisu NUMBER;
    v_id_typu NUMBER;
    v_wartosc NUMBER(2,1);
    v_komentarz VARCHAR2(200);
    v_liczba_ocen NUMBER;
    v_typ_counter NUMBER;
BEGIN
FOR zap_rec IN (SELECT id_zapisu FROM Zapisy) LOOP
        v_id_zapisu := zap_rec.id_zapisu;
        v_liczba_ocen := 2 + TRUNC(DBMS_RANDOM.VALUE(0, 2));
        v_typ_counter := 0;

FOR typ_rec IN (SELECT id_typu FROM Slownik_Ocen ORDER BY id_typu) LOOP
            IF v_typ_counter < v_liczba_ocen AND DBMS_RANDOM.VALUE(0,1) > 0.3 THEN
                v_id_typu := typ_rec.id_typu;
                v_wartosc := ROUND(DBMS_RANDOM.VALUE(2.0, 5.0), 1);
                v_komentarz := CASE v_id_typu
                                WHEN 1 THEN 'Ocena z kolokwium'
                                WHEN 2 THEN 'Ocena z egzaminu'
                                ELSE 'Ocena z projektu'
END;
BEGIN
INSERT INTO Oceny (id_zapisu, id_typu, wartosc, data_wystawienia, komentarz)
VALUES (v_id_zapisu, v_id_typu, v_wartosc, SYSDATE - DBMS_RANDOM.VALUE(0, 50), v_komentarz);
v_typ_counter := v_typ_counter + 1;
EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL;
END;
END IF;
END LOOP;
END LOOP;
COMMIT;
DBMS_OUTPUT.PUT_LINE('Liczba ocen: ' || (SELECT COUNT(*) FROM Oceny));
END;
/

-- =====================================================================
-- 9. HISTORIA ZMIAN OCEN (dla 2000 losowych ocen)
-- =====================================================================
DECLARE
v_stara_wart NUMBER;
    v_nowa_wart NUMBER;
    v_uzytk VARCHAR2(50);
BEGIN
FOR ocena_rec IN (SELECT id_oceny, wartosc FROM Oceny ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 2000 ROWS ONLY) LOOP
        v_stara_wart := ocena_rec.wartosc;
        v_nowa_wart := ROUND(DBMS_RANDOM.VALUE(2.0, 5.0), 1);
        v_uzytk := 'admin' || TRUNC(DBMS_RANDOM.VALUE(1,10));
INSERT INTO Historia_Ocen (id_oceny, stara_wartosc, nowa_wartosc, data_modyfikacji, uzytkownik, operacja)
VALUES (ocena_rec.id_oceny, v_stara_wart, v_nowa_wart, SYSDATE - DBMS_RANDOM.VALUE(0,10), v_uzytk, 'UPDATE');
END LOOP;
COMMIT;
DBMS_OUTPUT.PUT_LINE('Dodano historię dla 2000 ocen.');
END;
/

-- =====================================================================
-- 10. LOGI SYSTEMOWE (1000 wpisów)
-- =====================================================================
DECLARE
v_username VARCHAR2(50);
    v_akcja VARCHAR2(100);
    v_ip VARCHAR2(45);
BEGIN
FOR i IN 1..1000 LOOP
        v_username := 'user' || TRUNC(DBMS_RANDOM.VALUE(1, 200));
        v_akcja := CASE TRUNC(DBMS_RANDOM.VALUE(1,5))
                    WHEN 1 THEN 'LOGIN'
                    WHEN 2 THEN 'LOGOUT'
                    WHEN 3 THEN 'DODAWANIE_OCENY'
                    ELSE 'PRZEGLADANIE_STUDENTOW'
END;
        v_ip := '192.168.' || TRUNC(DBMS_RANDOM.VALUE(1,255)) || '.' || TRUNC(DBMS_RANDOM.VALUE(1,255));
INSERT INTO Logi_Systemu (username, akcja, data_akcji, ip_adres)
VALUES (v_username, v_akcja, SYSDATE - DBMS_RANDOM.VALUE(0, 30), v_ip);
END LOOP;
COMMIT;
DBMS_OUTPUT.PUT_LINE('Dodano 1000 logów systemowych.');
END;
/