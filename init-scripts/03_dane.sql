INSERT INTO Kierunki (nazwa, kod_kierunku, stopien) VALUES ('Informatyka Stosowana', 'INF', 1);
INSERT INTO Kierunki (nazwa, kod_kierunku, stopien) VALUES ('Automatyka i Robotyka', 'AUT', 1);
INSERT INTO Kierunki (nazwa, kod_kierunku, stopien) VALUES ('Inżynieria Danych', 'DAT', 2);
INSERT INTO Kierunki (nazwa, kod_kierunku, stopien) VALUES ('Cyberbezpieczeństwo', 'CYB', 1);

-- 2. PRZEDMIOTY (z wymaganym polem ects)
-- Informatyka Stosowana (kierunek_id = 1)
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('INF-01', 'Programowanie Obiektowe', 5, 1);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('INF-02', 'Bazy Danych', 6, 1);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('INF-03', 'Algorytmy i Struktury Danych', 5, 1);

-- Automatyka i Robotyka (kierunek_id = 2)
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('AUT-01', 'Teoria Sterowania', 5, 2);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('AUT-02', 'Sensoryka i Aktuatory', 4, 2);

-- Inżynieria Danych (kierunek_id = 3)
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('DAT-01', 'Analiza Statystyczna', 5, 3);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('DAT-02', 'Hurtownie Danych', 6, 3);

-- Cyberbezpieczeństwo (kierunek_id = 4)
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('CYB-01', 'Kryptografia', 5, 4);
INSERT INTO Przedmioty (kod_przedmiotu, nazwa, ects, kierunek_id) VALUES ('CYB-02', 'Bezpieczeństwo Sieci', 5, 4);

-- 3. PRACOWNICY (dodano emaile)
INSERT INTO Pracownicy (imie, nazwisko, tytul_naukowy, email) VALUES ('Andrzej', 'Kowalski', 'Dr inż.', 'andrzej.kowalski@uczelnia.pl');
INSERT INTO Pracownicy (imie, nazwisko, tytul_naukowy, email) VALUES ('Maria', 'Zielińska', 'Prof. dr hab.', 'maria.zielinska@uczelnia.pl');
INSERT INTO Pracownicy (imie, nazwisko, tytul_naukowy, email) VALUES ('Jan', 'Nowak', 'Mgr inż.', 'jan.nowak@uczelnia.pl');
INSERT INTO Pracownicy (imie, nazwisko, tytul_naukowy, email) VALUES ('Krzysztof', 'Mazur', 'Dr hab.', 'krzysztof.mazur@uczelnia.pl');
INSERT INTO Pracownicy (imie, nazwisko, tytul_naukowy, email) VALUES ('Barbara', 'Woźniak', 'Dr', 'barbara.wozniak@uczelnia.pl');

-- 4. SALE (pominięto ID, reszta zgodna)
INSERT INTO Sale (numer_sali, pojemnosc) VALUES ('104-A (Laboratorium)', 30);
INSERT INTO Sale (numer_sali, pojemnosc) VALUES ('215-B (Aula)', 120);
INSERT INTO Sale (numer_sali, pojemnosc) VALUES ('03-Centrum', 15);
INSERT INTO Sale (numer_sali, pojemnosc) VALUES ('301-C', 45);
INSERT INTO Sale (numer_sali, pojemnosc) VALUES ('111-A', 25);

-- 5. GRUPY (wymagają id_przedmiotu i id_pracownika – zakładamy, że ID zostały wygenerowane kolejno od 1)
-- Najpierw muszą istnieć przedmioty (ID 1..9) i pracownicy (ID 1..5)
INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika) VALUES ('Grupa IO-11 (Rok 1)', 1, 1);  -- Programowanie Obiektowe, Kowalski
INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika) VALUES ('Grupa IO-12 (Rok 1)', 2, 2);  -- Bazy Danych, Zielińska
INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika) VALUES ('Grupa AR-21 (Rok 2)', 4, 3);  -- Teoria Sterowania, Nowak
INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika) VALUES ('Grupa ID-31 (Rok 3)', 6, 4);  -- Analiza Statystyczna, Mazur
INSERT INTO Grupy (nazwa_grupy, id_przedmiotu, id_pracownika) VALUES ('Grupa CYB-11 (Rok 1)', 8, 5);  -- Kryptografia, Woźniak

COMMIT;