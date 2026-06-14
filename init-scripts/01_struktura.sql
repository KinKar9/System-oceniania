-- =====================================================================
-- 1. TABELE GŁÓWNE (z poprawkami)
-- =====================================================================

-- OSOBA 1 Adam Boryszewski
CREATE TABLE Kierunki (
                          id_kierunku NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                          nazwa VARCHAR2(150) NOT NULL UNIQUE,
                          kod_kierunku VARCHAR2(10) NOT NULL UNIQUE,
                          stopien NUMBER(1) NOT NULL,
                          CONSTRAINT pk_kierunki PRIMARY KEY (id_kierunku)
);

CREATE TABLE Przedmioty (
                            id_przedmiotu NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                            kod_przedmiotu VARCHAR2(15) NOT NULL UNIQUE,
                            nazwa VARCHAR2(150) NOT NULL,
                            ects NUMBER(2) NOT NULL,
                            kierunek_id NUMBER NOT NULL,
                            CONSTRAINT pk_przedmioty PRIMARY KEY (id_przedmiotu),
                            CONSTRAINT fk_przedmioty_kierunek FOREIGN KEY (kierunek_id) REFERENCES Kierunki(id_kierunku)
);

CREATE TABLE Pracownicy (
                            id_pracownika NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                            imie VARCHAR2(50) NOT NULL,
                            nazwisko VARCHAR2(100) NOT NULL,
                            tytul_naukowy VARCHAR2(50),
                            email VARCHAR2(100) NOT NULL UNIQUE,
                            CONSTRAINT pk_pracownicy PRIMARY KEY (id_pracownika)
);

CREATE TABLE Grupy (
                       id_grupy NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                       nazwa_grupy VARCHAR2(50) NOT NULL,
                       limit_miejsc NUMBER(3) DEFAULT 30,
                       id_przedmiotu NUMBER NOT NULL,
                       id_pracownika NUMBER NOT NULL,
                       CONSTRAINT pk_grupy PRIMARY KEY (id_grupy),
                       CONSTRAINT fk_grupy_przedmiot FOREIGN KEY (id_przedmiotu) REFERENCES Przedmioty(id_przedmiotu),
                       CONSTRAINT fk_grupy_pracownik FOREIGN KEY (id_pracownika) REFERENCES Pracownicy(id_pracownika)
);

CREATE TABLE Sale (
                      id_sali NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                      numer_sali VARCHAR2(20) NOT NULL UNIQUE,
                      pojemnosc NUMBER(3) NOT NULL,
                      typ_sali VARCHAR2(30) DEFAULT 'wykładowa',
                      CONSTRAINT pk_sale PRIMARY KEY (id_sali)
);

-- OSOBA 2 Konrad Sajewicz
CREATE TABLE Studenci (
                          id_studenta NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                          imie VARCHAR2(50) NOT NULL,
                          nazwisko VARCHAR2(100) NOT NULL,
                          nr_indeksu VARCHAR2(10) NOT NULL UNIQUE,
                          email VARCHAR2(100) NOT NULL UNIQUE,
                          data_urodzenia DATE NOT NULL,
                          pesel VARCHAR2(11) UNIQUE,
                          CONSTRAINT pk_studenci PRIMARY KEY (id_studenta)
);

CREATE TABLE Slownik_Ocen (
                              id_typu NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                              nazwa VARCHAR2(50) NOT NULL,
                              waga NUMBER(3,2) NOT NULL,
                              domyslny_zakres_min NUMBER(2,1) DEFAULT 2.0,
                              domyslny_zakres_max NUMBER(2,1) DEFAULT 5.0,
                              CONSTRAINT pk_slownik_ocen PRIMARY KEY (id_typu)
);

CREATE TABLE Warunki_Zal (
                             id_warunku NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                             id_przedmiotu NUMBER NOT NULL,
                             wymagana_procent NUMBER(3) NOT NULL,
                             minimalna_srednia NUMBER(2,1) DEFAULT 3.0,
                             czy_wymagany_egzamin CHAR(1) DEFAULT 'N',
                             CONSTRAINT pk_warunki_zal PRIMARY KEY (id_warunku),
                             CONSTRAINT fk_warunki_przedmiot FOREIGN KEY (id_przedmiotu) REFERENCES Przedmioty(id_przedmiotu),
                             CONSTRAINT chk_warunki_egzamin CHECK (czy_wymagany_egzamin IN ('T', 'N'))
);

CREATE TABLE Zapisy (
                        id_zapisu NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                        id_studenta NUMBER NOT NULL,
                        id_grupy NUMBER NOT NULL,
                        data_zapisu DATE DEFAULT SYSDATE,
                        status VARCHAR2(20) DEFAULT 'Aktywny',
                        czy_zaliczono CHAR(1) DEFAULT 'N',
                        data_zakonczenia DATE,
                        semestr VARCHAR2(20),
                        CONSTRAINT pk_zapisy PRIMARY KEY (id_zapisu),
                        CONSTRAINT fk_zapisy_student FOREIGN KEY (id_studenta) REFERENCES Studenci(id_studenta),
                        CONSTRAINT fk_zapisy_grupa FOREIGN KEY (id_grupy) REFERENCES Grupy(id_grupy),
                        CONSTRAINT uq_student_grupa UNIQUE (id_studenta, id_grupy),
                        CONSTRAINT chk_zapisy_status CHECK (status IN ('Aktywny', 'Zakończony', 'Anulowany')),
                        CONSTRAINT chk_zapisy_zaliczono CHECK (czy_zaliczono IN ('T', 'N'))
);

CREATE TABLE Oceny (
                       id_oceny NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                       id_zapisu NUMBER NOT NULL,
                       id_typu NUMBER NOT NULL,
                       wartosc NUMBER(2,1) NOT NULL CHECK (wartosc BETWEEN 2.0 AND 5.0),
                       data_wystawienia DATE DEFAULT SYSDATE,
                       komentarz VARCHAR2(200),
                       CONSTRAINT pk_oceny PRIMARY KEY (id_oceny),
                       CONSTRAINT fk_oceny_zapis FOREIGN KEY (id_zapisu) REFERENCES Zapisy(id_zapisu),
                       CONSTRAINT fk_oceny_typ FOREIGN KEY (id_typu) REFERENCES Slownik_Ocen(id_typu),
                       CONSTRAINT uq_zapis_typ UNIQUE (id_zapisu, id_typu)
);

-- OSOBA 3: Kinga Kardasz
CREATE TABLE Uzytkownicy (
                             id_uzytkownika NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                             username VARCHAR2(50) NOT NULL UNIQUE,
                             password VARCHAR2(255) NOT NULL,
                             email VARCHAR2(100) NOT NULL UNIQUE,
                             czy_aktywny CHAR(1) DEFAULT 'T',
                             id_studenta NUMBER NULL,
                             CONSTRAINT pk_uzytkownicy PRIMARY KEY (id_uzytkownika),
                             CONSTRAINT fk_uzytkownik_student FOREIGN KEY (id_studenta) REFERENCES Studenci(id_studenta)
);

CREATE TABLE Role (
                      id_roli NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                      nazwa_roli VARCHAR2(30) NOT NULL UNIQUE,
                      CONSTRAINT pk_role PRIMARY KEY (id_roli)
);

CREATE TABLE Uzytkownicy_Role (
                                  id_uzytkownika NUMBER NOT NULL,
                                  id_roli NUMBER NOT NULL,
                                  CONSTRAINT pk_uzytkownicy_role PRIMARY KEY (id_uzytkownika, id_roli),
                                  CONSTRAINT fk_ur_uzytkownik FOREIGN KEY (id_uzytkownika) REFERENCES Uzytkownicy(id_uzytkownika),
                                  CONSTRAINT fk_ur_rola FOREIGN KEY (id_roli) REFERENCES Role(id_roli)
);

CREATE TABLE Historia_Ocen (
                               id_historii NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                               id_oceny NUMBER NOT NULL,
                               stara_wartosc NUMBER(2,1),
                               nowa_wartosc NUMBER(2,1),
                               data_modyfikacji DATE DEFAULT SYSDATE,
                               uzytkownik VARCHAR2(50) DEFAULT USER,
                               operacja VARCHAR2(20) NOT NULL,
                               CONSTRAINT pk_historia_ocen PRIMARY KEY (id_historii),
                               CONSTRAINT fk_historia_ocena FOREIGN KEY (id_oceny) REFERENCES Oceny(id_oceny)
);

CREATE TABLE Logi_Systemu (
                              id_logu NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                              username VARCHAR2(50) NOT NULL,
                              akcja VARCHAR2(100) NOT NULL,
                              data_akcji DATE DEFAULT SYSDATE,
                              ip_adres VARCHAR2(45),
                              CONSTRAINT pk_logi_systemu PRIMARY KEY (id_logu)
);

CREATE TABLE Rankingi (
                          id_rankingu NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                          id_semestru VARCHAR2(20),
                          data_generowania DATE DEFAULT SYSDATE,
                          dane_rankingu CLOB,
                          CONSTRAINT pk_rankingi PRIMARY KEY (id_rankingu)
);

-- =====================================================================
-- 2. NOWA TABELA DLA TOKENÓW
-- =====================================================================
CREATE TABLE Tokeny (
                        id_tokenu NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
                        id_studenta NUMBER NOT NULL,
                        token VARCHAR2(255) NOT NULL UNIQUE,
                        data_waznosci DATE NOT NULL,
                        typ_tokenu VARCHAR2(20) DEFAULT 'RESET',
                        CONSTRAINT pk_tokeny PRIMARY KEY (id_tokenu),
                        CONSTRAINT fk_token_student FOREIGN KEY (id_studenta) REFERENCES Studenci(id_studenta),
                        CONSTRAINT chk_token_typ CHECK (typ_tokenu IN ('RESET', 'PUBLICZNY', 'VERIFY'))
);

-- =====================================================================
-- 3. INDEKSY
-- =====================================================================
CREATE INDEX idx_zapisy_student   ON Zapisy(id_studenta);
CREATE INDEX idx_zapisy_grupa     ON Zapisy(id_grupy);
CREATE INDEX idx_zapisy_status    ON Zapisy(status);
CREATE INDEX idx_oceny_zapis      ON Oceny(id_zapisu);
CREATE INDEX idx_oceny_typ        ON Oceny(id_typu);
CREATE INDEX idx_oceny_data       ON Oceny(data_wystawienia);
CREATE INDEX idx_grupy_przedmiot  ON Grupy(id_przedmiotu);
CREATE INDEX idx_grupy_pracownik  ON Grupy(id_pracownika);
CREATE INDEX idx_studenci_email   ON Studenci(email);
CREATE INDEX idx_studenci_nr_indeksu ON Studenci(nr_indeksu);
CREATE INDEX idx_uzytkownicy_username ON Uzytkownicy(username);
CREATE INDEX idx_uzytkownicy_email ON Uzytkownicy(email);
CREATE INDEX idx_tokeny_token     ON Tokeny(token);
CREATE INDEX idx_historia_oceny ON HISTORIA_OCEN(id_oceny);
CREATE INDEX idx_historia_uzytkownik ON HISTORIA_OCEN(uzytkownik);

-- =====================================================================
-- 4. WIDOK
-- =====================================================================
CREATE OR REPLACE VIEW Widok_Plan_Zajec AS
SELECT
    g.nazwa_grupy,
    p.nazwa AS przedmiot,
    pr.imie || ' ' || pr.nazwisko AS prowadzacy
FROM Grupy g
         JOIN Przedmioty p ON g.id_przedmiotu = p.id_przedmiotu
         JOIN Pracownicy pr ON g.id_pracownika = pr.id_pracownika;

COMMENT ON TABLE Widok_Plan_Zajec IS 'Widok planu zajęć – grupa, przedmiot, prowadzący (bez sal)';

-- =====================================================================
-- 5. KOMENTARZE
-- =====================================================================
COMMENT ON TABLE Tokeny IS 'Przechowuje tokeny bezpieczeństwa dla studentów (oddzielona tabela)';
COMMENT ON COLUMN Uzytkownicy.id_studenta IS 'Opcjonalne powiązanie użytkownika ze studentem – relacja 1:1';
COMMENT ON TABLE Historia_Ocen IS 'Rejestr zmian w tabeli OCENY – automatycznie wypełniany przez trigger trg_audyt_ocen';