CREATE OR REPLACE PACKAGE PKG_OCENY AS
    FUNCTION SREDNIA_STUDENTA(p_id_studenta NUMBER) RETURN NUMBER;
    PROCEDURE RANKING_STUDENTOW(p_semestr VARCHAR2 DEFAULT NULL);
    PROCEDURE SPRAWDZ_ZALICZENIE(p_id_studenta NUMBER);
END PKG_OCENY;
/

CREATE OR REPLACE PACKAGE BODY PKG_OCENY AS

    FUNCTION SREDNIA_STUDENTA(p_id_studenta NUMBER) RETURN NUMBER IS
        v_srednia NUMBER;
BEGIN
        IF p_id_studenta IS NULL OR p_id_studenta <= 0 THEN
            RETURN NULL;
END IF;

SELECT SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga), 0)
INTO v_srednia
FROM OCENY o
         JOIN ZAPISY z ON o.id_zapisu = z.id_zapisu
         JOIN SLOWNIK_OCEN sw ON o.id_typu = sw.id_typu
WHERE z.id_studenta = p_id_studenta;

RETURN ROUND(v_srednia, 2);
EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Błąd w SREDNIA_STUDENTA: ' || SQLERRM);
RETURN NULL;
END;

    PROCEDURE RANKING_STUDENTOW(p_semestr VARCHAR2 DEFAULT NULL) IS
        CURSOR c_ranking IS
SELECT s.imie, s.nazwisko,
       NVL(SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga),0), NULL) AS srednia,
       RANK() OVER (ORDER BY NVL(SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga),0), NULL) DESC NULLS LAST) AS pozycja
FROM STUDENCI s
         LEFT JOIN ZAPISY z ON s.id_studenta = z.id_studenta
         LEFT JOIN OCENY o ON z.id_zapisu = o.id_zapisu
         LEFT JOIN SLOWNIK_OCEN sw ON o.id_typu = sw.id_typu
WHERE (p_semestr IS NULL OR z.semestr = p_semestr)
GROUP BY s.id_studenta, s.imie, s.nazwisko
HAVING NVL(SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga),0), NULL) IS NOT NULL   -- pomijamy bez ocen
ORDER BY NVL(SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga),0), NULL) DESC NULLS LAST
    FETCH FIRST 100 ROWS ONLY;
BEGIN
FOR rec IN c_ranking LOOP
            DBMS_OUTPUT.PUT_LINE(rec.pozycja || '. ' || rec.imie || ' ' || rec.nazwisko || ' : ' || rec.srednia);
END LOOP;
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Błąd w RANKING_STUDENTOW: ' || SQLERRM);
            RAISE;
END;

    PROCEDURE SPRAWDZ_ZALICZENIE(p_id_studenta NUMBER) IS
BEGIN
        IF p_id_studenta IS NULL OR p_id_studenta <= 0 THEN
            RETURN;
END IF;

MERGE INTO ZAPISY z
    USING (
        SELECT z2.id_zapisu,
               SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga), 0) AS srednia
        FROM ZAPISY z2
                 LEFT JOIN OCENY o ON z2.id_zapisu = o.id_zapisu
                 LEFT JOIN SLOWNIK_OCEN sw ON o.id_typu = sw.id_typu
        WHERE z2.id_studenta = p_id_studenta
        GROUP BY z2.id_zapisu
    ) src ON (z.id_zapisu = src.id_zapisu)
    WHEN MATCHED THEN
        UPDATE SET
            czy_zaliczono = CASE WHEN src.srednia >= 3.0 THEN 'T' ELSE 'N' END,
            data_zakonczenia = CASE
                                   WHEN src.srednia >= 3.0 AND z.data_zakonczenia IS NULL
                                       THEN SYSDATE
                                   ELSE z.data_zakonczenia
                END;
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Błąd w SPRAWDZ_ZALICZENIE: ' || SQLERRM);
            RAISE;
END;

END PKG_OCENY;
/