CREATE TABLE kierunki (
                          id_kierunku INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          nazwa VARCHAR(150) NOT NULL UNIQUE,
                          kod_kierunku VARCHAR(10) NOT NULL UNIQUE,
                          stopien VARCHAR(30) NOT NULL,
                          deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE przedmioty (
                            id_przedmiotu INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            kod_przedmiotu VARCHAR(15) NOT NULL UNIQUE,
                            nazwa VARCHAR(150) NOT NULL,
                            ects INTEGER NOT NULL,
                            kierunek_id INTEGER NOT NULL REFERENCES kierunki(id_kierunku),
                            wersja INTEGER DEFAULT 0
);

CREATE TABLE pracownicy (
                            id_pracownika INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            imie VARCHAR(50) NOT NULL,
                            nazwisko VARCHAR(100) NOT NULL,
                            tytul_naukowy VARCHAR(50),
                            email VARCHAR(100) NOT NULL UNIQUE,
                            wersja INTEGER DEFAULT 0
);


CREATE TABLE grupy (
                       id_grupy INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       nazwa_grupy VARCHAR(50) NOT NULL,
                       limit_miejsc INTEGER DEFAULT 30,
                       id_przedmiotu INTEGER NOT NULL REFERENCES przedmioty(id_przedmiotu),
                       id_pracownika INTEGER NOT NULL REFERENCES pracownicy(id_pracownika)
);

CREATE TABLE sale (
                      id_sali INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      numer_sali VARCHAR(20) NOT NULL UNIQUE,
                      pojemnosc INTEGER NOT NULL,
                      typ_sali VARCHAR(30) DEFAULT 'wykładowa'
);

CREATE TABLE studenci (
                          id_studenta INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          imie VARCHAR(50) NOT NULL,
                          nazwisko VARCHAR(100) NOT NULL,
                          nr_indeksu VARCHAR(10) NOT NULL UNIQUE,
                          email VARCHAR(100) NOT NULL UNIQUE,
                          data_urodzenia DATE NOT NULL,
                          pesel VARCHAR(11) UNIQUE,
                          secure_token VARCHAR(255) UNIQUE,
                          data_utworzenia TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          data_aktualizacji TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          czy_aktywny BOOLEAN DEFAULT TRUE NOT NULL
);

CREATE TABLE slownik_ocen (
                              id_typu INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              nazwa VARCHAR(50) NOT NULL,
                              waga NUMERIC(3,2) NOT NULL,
                              domyslny_zakres_min NUMERIC(2,1) DEFAULT 2.0,
                              domyslny_zakres_max NUMERIC(2,1) DEFAULT 5.0
);

CREATE TABLE warunki_zal (
                             id_warunku INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             id_przedmiotu INTEGER NOT NULL REFERENCES przedmioty(id_przedmiotu),
                             wymagana_procent INTEGER NOT NULL,
                             minimalna_srednia NUMERIC(2,1) DEFAULT 3.0,
                             czy_wymagany_egzamin BOOLEAN DEFAULT false,
                             wersja INTEGER DEFAULT 0
);

CREATE TABLE zapisy (
                        id_zapisu INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        id_studenta INTEGER NOT NULL REFERENCES studenci(id_studenta),
                        id_grupy INTEGER NOT NULL REFERENCES grupy(id_grupy),
                        data_zapisu DATE DEFAULT CURRENT_DATE,
                        status VARCHAR(20) DEFAULT 'AKTYWNY',
                        czy_zaliczono BOOLEAN DEFAULT false,
                        data_zakonczenia DATE,
                        semestr VARCHAR(20),
                        wersja INTEGER DEFAULT 0,
                        UNIQUE (id_studenta, id_grupy),
                        CHECK (status IN ('AKTYWNY', 'ZAKONCZONY', 'ANULOWANY'))
);

CREATE TABLE oceny (
                       id_oceny INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       id_zapisu INTEGER NOT NULL REFERENCES zapisy(id_zapisu),
                       id_typu INTEGER NOT NULL REFERENCES slownik_ocen(id_typu),
                       wartosc NUMERIC(2,1) NOT NULL CHECK (wartosc BETWEEN 2.0 AND 5.0),
                       data_wystawienia DATE DEFAULT CURRENT_DATE,
                       komentarz VARCHAR(200),
                       UNIQUE (id_zapisu, id_typu)
);

CREATE TABLE rola (
                      id_roli INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      nazwa_roli VARCHAR(30) NOT NULL UNIQUE,
                      data_utworzenia TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      data_aktualizacji TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE uzytkownicy (
                             id_uzytkownika INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             username VARCHAR(50) NOT NULL UNIQUE,
                             password VARCHAR(255) NOT NULL,
                             email VARCHAR(100) NOT NULL UNIQUE,
                             czy_aktywny BOOLEAN DEFAULT true,
                             id_studenta INTEGER NULL REFERENCES studenci(id_studenta),
                             data_utworzenia TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             data_aktualizacji TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE uzytkownicy_role (
                                  id_uzytkownika INTEGER NOT NULL REFERENCES uzytkownicy(id_uzytkownika),
                                  id_roli INTEGER NOT NULL REFERENCES rola(id_roli),
                                  PRIMARY KEY (id_uzytkownika, id_roli)
);

CREATE TABLE historia_ocen (
                               id_historii INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               id_oceny INTEGER NOT NULL REFERENCES oceny(id_oceny) ON DELETE CASCADE,
                               stara_wartosc NUMERIC(2,1),
                               nowa_wartosc NUMERIC(2,1),
                               data_modyfikacji DATE DEFAULT CURRENT_DATE,
                               uzytkownik VARCHAR(50) DEFAULT CURRENT_USER,
                               operacja VARCHAR(20) NOT NULL
);

CREATE TABLE logi_systemu (
                              id_logu INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              username VARCHAR(50) NOT NULL,
                              akcja VARCHAR(100) NOT NULL,
                              data_akcji DATE DEFAULT CURRENT_DATE,
                              ip_adres VARCHAR(45)
);

CREATE TABLE rankingi (
                          id_rankingu INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          id_semestru VARCHAR(20),
                          data_generowania DATE DEFAULT CURRENT_DATE,
                          dane_rankingu TEXT
);

CREATE TABLE tokeny (
                        id_tokenu INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        id_studenta INTEGER NOT NULL REFERENCES studenci(id_studenta),
                        token VARCHAR(255) NOT NULL UNIQUE,
                        data_waznosci DATE NOT NULL,
                        typ_tokenu VARCHAR(20) DEFAULT 'RESET',
                        CHECK (typ_tokenu IN ('RESET', 'PUBLICZNY', 'VERIFY'))
);
CREATE INDEX idx_kierunki_deleted ON kierunki(deleted);

-- Indeksy
CREATE INDEX idx_zapisy_student ON zapisy(id_studenta);
CREATE INDEX idx_zapisy_grupa ON zapisy(id_grupy);
CREATE INDEX idx_zapisy_status ON zapisy(status);
CREATE INDEX idx_oceny_zapis ON oceny(id_zapisu);
CREATE INDEX idx_oceny_typ ON oceny(id_typu);
CREATE INDEX idx_oceny_data ON oceny(data_wystawienia);
CREATE INDEX idx_grupy_przedmiot ON grupy(id_przedmiotu);
CREATE INDEX idx_grupy_pracownik ON grupy(id_pracownika);
CREATE INDEX idx_studenci_email ON studenci(email);
CREATE INDEX idx_studenci_nr_indeksu ON studenci(nr_indeksu);
CREATE INDEX idx_uzytkownicy_username ON uzytkownicy(username);
CREATE INDEX idx_uzytkownicy_email ON uzytkownicy(email);
CREATE INDEX idx_tokeny_token ON tokeny(token);
CREATE INDEX idx_historia_oceny ON historia_ocen(id_oceny);
CREATE INDEX idx_historia_uzytkownik ON historia_ocen(uzytkownik);

-- Widok
CREATE OR REPLACE VIEW widok_plan_zajec AS
SELECT
    g.nazwa_grupy,
    p.nazwa AS przedmiot,
    pr.imie || ' ' || pr.nazwisko AS prowadzacy
FROM grupy g
         JOIN przedmioty p ON g.id_przedmiotu = p.id_przedmiotu
         JOIN pracownicy pr ON g.id_pracownika = pr.id_pracownika;

COMMENT ON VIEW widok_plan_zajec IS 'Widok planu zajęć – grupa, przedmiot, prowadzący (bez sal)';

COMMENT ON TABLE tokeny IS 'Przechowuje tokeny bezpieczeństwa dla studentów';
COMMENT ON COLUMN uzytkownicy.id_studenta IS 'Opcjonalne powiązanie użytkownika ze studentem – relacja 1:1';
COMMENT ON TABLE historia_ocen IS 'Rejestr zmian w tabeli oceny – automatycznie wypełniany przez trigger';