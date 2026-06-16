-- Funkcja średnia studenta
CREATE OR REPLACE FUNCTION srednia_studenta(p_id_studenta BIGINT)
RETURNS NUMERIC AS $$
DECLARE
v_srednia NUMERIC;
BEGIN
    IF p_id_studenta IS NULL OR p_id_studenta <= 0 THEN
        RETURN NULL;
END IF;

SELECT SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga), 0)
INTO v_srednia
FROM oceny o
         JOIN zapisy z ON o.id_zapisu = z.id_zapisu
         JOIN slownik_ocen sw ON o.id_typu = sw.id_typu
WHERE z.id_studenta = p_id_studenta;

RETURN ROUND(v_srednia, 2);
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
    COALESCE(SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga), 0), NULL) AS srednia,
    RANK() OVER (ORDER BY COALESCE(SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga), 0), NULL) DESC NULLS LAST) AS pozycja
FROM studenci s
         LEFT JOIN zapisy z ON s.id_studenta = z.id_studenta
         LEFT JOIN oceny o ON z.id_zapisu = o.id_zapisu
         LEFT JOIN slownik_ocen sw ON o.id_typu = sw.id_typu
WHERE (p_semestr IS NULL OR z.semestr = p_semestr)
GROUP BY s.id_studenta, s.imie, s.nazwisko
HAVING COALESCE(SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga), 0), NULL) IS NOT NULL
ORDER BY srednia DESC NULLS LAST
    LIMIT 100
    LOOP
        RAISE NOTICE '%. % % : %', rec.pozycja, rec.imie, rec.nazwisko, rec.srednia;
END LOOP;
END;
$$;

-- Procedura sprawdzania zaliczenia
CREATE OR REPLACE PROCEDURE sprawdz_zaliczenie(p_id_studenta INTEGER)
LANGUAGE plpgsql AS $$
BEGIN
    IF p_id_studenta IS NULL OR p_id_studenta <= 0 THEN
        RETURN;
END IF;

UPDATE zapisy z
SET
    czy_zaliczono = (src.srednia >= 3.0),
    data_zakonczenia = CASE
                           WHEN src.srednia >= 3.0 AND z.data_zakonczenia IS NULL THEN CURRENT_DATE
                           ELSE z.data_zakonczenia
        END
    FROM (
        SELECT z2.id_zapisu,
               SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga), 0) AS srednia
        FROM zapisy z2
        LEFT JOIN oceny o ON z2.id_zapisu = o.id_zapisu
        LEFT JOIN slownik_ocen sw ON o.id_typu = sw.id_typu
        WHERE z2.id_studenta = p_id_studenta
        GROUP BY z2.id_zapisu
    ) src
WHERE z.id_zapisu = src.id_zapisu;
END;
$$;