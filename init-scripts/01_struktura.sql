
-- OBSZAR LOGICZNY 1: STRUKTURA ORGANIZACYJNA

CREATE TABLE Wydzialy (
    id_wydzialu NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    nazwa VARCHAR2(150) NOT NULL,
    kod_wydzialu VARCHAR2(10) NOT NULL UNIQUE,
    CONSTRAINT pk_wydzialy PRIMARY KEY (id_wydzialu)
);

CREATE TABLE Kierunki (
    id_kierunku NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    id_wydzialu NUMBER NOT NULL,
    nazwa VARCHAR2(150) NOT NULL,
    kod_kierunku VARCHAR2(10) NOT NULL UNIQUE,
    stopien NUMBER(1) NOT NULL, -- 1 = Licencjat/Inżynier, 2 = Magister
    CONSTRAINT pk_kierunki PRIMARY KEY (id_kierunku),
    CONSTRAINT fk_kierunki_wydzialy FOREIGN KEY (id_wydzialu) REFERENCES Wydzialy(id_wydzialu)
);

CREATE TABLE Studenci (
    id_studenta NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    id_kierunku NUMBER NOT NULL,
    imie VARCHAR2(50) NOT NULL,
    nazwisko VARCHAR2(100) NOT NULL,
    nr_indeksu VARCHAR2(10) NOT NULL UNIQUE,
    email VARCHAR2(100) NOT NULL UNIQUE,
    data_urodzenia DATE NOT NULL,
    CONSTRAINT pk_studenci PRIMARY KEY (id_studenta),
    CONSTRAINT fk_studenci_kierunki FOREIGN KEY (id_kierunku) REFERENCES Kierunki(id_kierunku)
);

CREATE TABLE Pracownicy (
    id_pracownika NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    id_wydzialu NUMBER NOT NULL,
    imie VARCHAR2(50) NOT NULL,
    nazwisko VARCHAR2(100) NOT NULL,
    tytul_naukowy VARCHAR2(50) NOT NULL,
    email VARCHAR2(100) NOT NULL UNIQUE,
    CONSTRAINT pk_pracownika PRIMARY KEY (id_pracownika),
    CONSTRAINT fk_pracownika_wydzialy FOREIGN KEY (id_wydzialu) REFERENCES Wydzialy(id_wydzialu)
);

-- OBSZAR LOGICZNY 2: KATALOG KURSÓW

CREATE TABLE Przedmioty (
    id_przedmiotu NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    nazwa VARCHAR2(150) NOT NULL,
    kod_przedmiotu VARCHAR2(15) NOT NULL UNIQUE,
    ects NUMBER(2) NOT NULL,
    CONSTRAINT pk_przedmioty PRIMARY KEY (id_przedmiotu)
);

CREATE TABLE Semestry (
    id_semestru NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    kod_semestru VARCHAR2(20) NOT NULL UNIQUE, -- np. "2025/2026_ZIMA"
    data_rozpoczecia DATE NOT NULL,
    data_zakonczenia DATE NOT NULL,
    CONSTRAINT pk_semestry PRIMARY KEY (id_semestru)
);

CREATE TABLE Kursy_Oferta (
    id_oferty NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    id_przedmiotu NUMBER NOT NULL,
    id_semestru NUMBER NOT NULL,
    id_pracownika NUMBER NOT NULL,
    CONSTRAINT pk_kursy_oferta PRIMARY KEY (id_oferty),
    CONSTRAINT fk_oferta_przedmioty FOREIGN KEY (id_przedmiotu) REFERENCES Przedmioty(id_przedmiotu),
    CONSTRAINT fk_oferta_semestry FOREIGN KEY (id_semestru) REFERENCES Semestry(id_semestru),
    CONSTRAINT fk_oferta_pracownicy FOREIGN KEY (id_pracownika) REFERENCES Pracownicy(id_pracownika)
);

CREATE TABLE Grupy_Zajeciowe (
    id_grupy NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    id_oferty NUMBER NOT NULL,
    nazwa_grupy VARCHAR2(50) NOT NULL, -- np. "LAB_01", "WYK_01"
    typ_zajec VARCHAR2(20) NOT NULL, -- np. 'Wyklad', 'Laboratorium', 'Projekt'
    limit_miejsc NUMBER(3) NOT NULL,
    CONSTRAINT pk_grupy PRIMARY KEY (id_grupy),
    CONSTRAINT fk_grupy_oferta FOREIGN KEY (id_oferty) REFERENCES Kursy_Oferta(id_oferty)
);

-- OBSZAR LOGICZNY 3: PROCES OCENIANIA I AUDYT

CREATE TABLE Zapisy (
    id_zapisu NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    id_studenta NUMBER NOT NULL,
    id_grupy NUMBER NOT NULL,
    data_zapisu DATE DEFAULT SYSDATE,
    status VARCHAR2(20) DEFAULT 'Aktywny', -- 'Aktywny', 'Wypisany'
    CONSTRAINT pk_zapisy PRIMARY KEY (id_zapisu),
    CONSTRAINT fk_zapisy_studenci FOREIGN KEY (id_studenta) REFERENCES Studenci(id_studenta),
    CONSTRAINT fk_zapisy_grupy FOREIGN KEY (id_grupy) REFERENCES Grupy_Zajeciowe(id_grupy),
    CONSTRAINT uq_student_grupa UNIQUE (id_studenta, id_grupy)
);

CREATE TABLE Typy_Ocen (
    id_typu NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    nazwa VARCHAR2(50) NOT NULL, -- 'Kolokwium', 'Aktywnosc', 'Projekt', 'Egzamin'
    waga NUMBER(3,2) NOT NULL, -- np. 0.40 (40%)
    CONSTRAINT pk_typy_ocen PRIMARY KEY (id_typu)
);

CREATE TABLE Terminy_Egzaminow (
    id_terminu NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    id_oferty NUMBER NOT NULL,
    nr_terminu NUMBER(1) NOT NULL, -- 1 = Pierwszy, 2 = Poprawkowy
    data_egzaminu DATE NOT NULL,
    sala VARCHAR2(20),
    CONSTRAINT pk_terminy_egzaminow PRIMARY KEY (id_terminu),
    CONSTRAINT fk_terminy_oferta FOREIGN KEY (id_oferty) REFERENCES Kursy_Oferta(id_oferty)
);

CREATE TABLE Oceny_Czastkowe (
    id_oceny_czastkowej NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    id_zapisu NUMBER NOT NULL,
    id_typu NUMBER NOT NULL,
    ocena NUMBER(2,1) NOT NULL, -- np. 3.5, 4.0, 5.0
    data_wystawienia DATE DEFAULT SYSDATE,
    komentarz VARCHAR2(200),
    CONSTRAINT pk_oceny_czastkowe PRIMARY KEY (id_oceny_czastkowej),
    CONSTRAINT fk_czastkowe_zapisy FOREIGN KEY (id_zapisu) REFERENCES Zapisy(id_zapisu),
    CONSTRAINT fk_czastkowe_typy FOREIGN KEY (id_typu) REFERENCES Typy_Ocen(id_typu)
);

CREATE TABLE Oceny_Koncowe (
    id_oceny_koncowej NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    id_studenta NUMBER NOT NULL,
    id_oferty NUMBER NOT NULL,
    ocena_koncowa NUMBER(2,1),
    czy_zaliczono CHAR(1) DEFAULT 'N', -- 'T' lub 'N'
    data_zamkniecia DATE,
    CONSTRAINT pk_oceny_koncowe PRIMARY KEY (id_oceny_koncowej),
    CONSTRAINT fk_koncowe_studenci FOREIGN KEY (id_studenta) REFERENCES Studenci(id_studenta),
    CONSTRAINT fk_koncowe_oferta FOREIGN KEY (id_oferty) REFERENCES Kursy_Oferta(id_oferty)
);

CREATE TABLE Logi_Ocen (
    id_logu NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    id_oceny_czastkowej NUMBER NOT NULL,
    stara_ocena NUMBER(2,1),
    nowa_ocena NUMBER(2,1),
    data_modyfikacji DATE DEFAULT SYSDATE,
    uzytkownik VARCHAR2(50) DEFAULT USER,
    operacja VARCHAR2(20) NOT NULL -- 'INSERT', 'UPDATE', 'DELETE'
);

CREATE TABLE Historia_Logowania (
    id NUMBER GENERATED ALWAYS AS IDENTITY START WITH 1 INCREMENT BY 1,
    username VARCHAR2(100) NOT NULL,
    data_logowania DATE DEFAULT SYSDATE,
    data_wylogowania DATE,
    CONSTRAINT pk_historia_logowania PRIMARY KEY (id)
);