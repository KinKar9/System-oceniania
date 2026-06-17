-- Funkcja średnia studenta
CREATE OR REPLACE FUNCTION srednia_studenta(p_id_studenta INTEGER)
RETURNS NUMERIC AS $$
DECLARE
v_srednia NUMERIC;
BEGIN
    IF p_id_studenta IS NULL OR p_id_studenta <= 0 THEN
        RETURN NULL;
END IF;

SELECT ROUND(SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga), 0), 2)
INTO v_srednia
FROM oceny o
         JOIN zapisy z ON o.id_zapisu = z.id_zapisu
         JOIN slownik_ocen sw ON o.id_typu = sw.id_typu
WHERE z.id_studenta = p_id_studenta;

RETURN v_srednia;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Procedura ranking studentów
CREATE OR REPLACE PROCEDURE ranking_studentow(p_semestr VARCHAR DEFAULT NULL)
LANGUAGE plpgsql AS $$
DECLARE
rec RECORD;
BEGIN
FOR rec IN
SELECT
    s.imie, s.nazwisko,
    ROUND(SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga), 0), 2) AS srednia,
    RANK() OVER (ORDER BY SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga), 0) DESC) AS pozycja
FROM studenci s
         LEFT JOIN zapisy z ON s.id_studenta = z.id_studenta
         LEFT JOIN oceny o ON z.id_zapisu = o.id_zapisu
         LEFT JOIN slownik_ocen sw ON o.id_typu = sw.id_typu
WHERE (p_semestr IS NULL OR z.semestr = p_semestr)
GROUP BY s.id_studenta, s.imie, s.nazwisko
HAVING SUM(o.wartosc * sw.waga) IS NOT NULL
ORDER BY srednia DESC
    LIMIT 100
    LOOP
        RAISE NOTICE '%. % % : %', rec.pozycja, rec.imie, rec.nazwisko, rec.srednia;
END LOOP;
END;
$$;

-- Procedura sprawdzania zaliczenia (z CTE)
CREATE OR REPLACE PROCEDURE sprawdz_zaliczenie(p_id_studenta INTEGER)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_id_studenta IS NULL OR p_id_studenta <= 0 THEN
        RAISE EXCEPTION 'Nieprawidłowy identyfikator studenta: %', p_id_studenta;
END IF;

WITH srednie AS (
    SELECT z2.id_zapisu,
           ROUND(SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga), 0), 2) AS srednia
    FROM zapisy z2
             LEFT JOIN oceny o ON z2.id_zapisu = o.id_zapisu
             LEFT JOIN slownik_ocen sw ON o.id_typu = sw.id_typu
    WHERE z2.id_studenta = p_id_studenta
    GROUP BY z2.id_zapisu
)
UPDATE zapisy z
SET
    czy_zaliczono = (s.srednia >= 3.0),
    data_zakonczenia = CASE
                           WHEN s.srednia >= 3.0 AND z.data_zakonczenia IS NULL THEN CURRENT_DATE
                           ELSE z.data_zakonczenia
        END
    FROM srednie s
WHERE z.id_zapisu = s.id_zapisu;

IF NOT FOUND THEN
        RAISE NOTICE 'Nie znaleziono zapisów dla studenta o ID: %', p_id_studenta;
END IF;
EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE 'Wystąpił błąd: %', SQLERRM;
        RAISE;
END;
$$;