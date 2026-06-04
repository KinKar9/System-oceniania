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

-- =====================================================
-- 6. STUDENCI (TYLKO JEDEN wpis dla studenta 'student')
-- =====================================================
-- Usuń ewentualnego istniejącego studenta (żeby uniknąć błędu)
DELETE FROM STUDENCI WHERE email = 'student';
-- Dodaj prawidłowego studenta (bez duplikacji)
INSERT INTO STUDENCI (imie, nazwisko, nr_indeksu, email, data_urodzenia, secure_token)
VALUES ('Jan', 'Kowalski', 's12345', 'student', DATE '2000-01-01', 'temp123');

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
