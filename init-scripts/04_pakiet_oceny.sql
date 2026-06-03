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
        SELECT SUM(o.wartosc * sw.waga) / SUM(sw.waga)
        INTO v_srednia
        FROM OCENY o
        JOIN ZAPISY z ON o.id_zapisu = z.id_zapisu
        JOIN SLOWNIK_OCEN sw ON o.id_typu = sw.id_typu
        WHERE z.id_studenta = p_id_studenta;
        
        RETURN NVL(ROUND(v_srednia, 2), 0);
    END;

    PROCEDURE RANKING_STUDENTOW(p_semestr VARCHAR2 DEFAULT NULL) IS
        CURSOR c_ranking IS
            SELECT s.id_studenta, s.imie, s.nazwisko,
                   NVL(SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga),0), 0) AS srednia
            FROM STUDENCI s
            LEFT JOIN ZAPISY z ON s.id_studenta = z.id_studenta
            LEFT JOIN OCENY o ON z.id_zapisu = o.id_zapisu
            LEFT JOIN SLOWNIK_OCEN sw ON o.id_typu = sw.id_typu
            WHERE (p_semestr IS NULL OR z.semestr = p_semestr)  
            GROUP BY s.id_studenta, s.imie, s.nazwisko
            ORDER BY srednia DESC;
        v_id NUMBER;
        v_imie VARCHAR2(50);
        v_nazwisko VARCHAR2(100);
        v_srednia NUMBER;
    BEGIN
        OPEN c_ranking;
        LOOP
            FETCH c_ranking INTO v_id, v_imie, v_nazwisko, v_srednia;
            EXIT WHEN c_ranking%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE(v_imie || ' ' || v_nazwisko || ' : ' || v_srednia);
        END LOOP;
        CLOSE c_ranking;
    END;

    PROCEDURE SPRAWDZ_ZALICZENIE(p_id_studenta NUMBER) IS
        CURSOR c_zapisy IS
            SELECT z.id_zapisu,
                   SUM(o.wartosc * sw.waga) / NULLIF(SUM(sw.waga),0) AS srednia_przedmiot
            FROM ZAPISY z
            LEFT JOIN OCENY o ON z.id_zapisu = o.id_zapisu
            LEFT JOIN SLOWNIK_OCEN sw ON o.id_typu = sw.id_typu
            WHERE z.id_studenta = p_id_studenta
            GROUP BY z.id_zapisu;
    BEGIN
        FOR rec IN c_zapisy LOOP
            IF rec.srednia_przedmiot >= 3.0 THEN
                UPDATE ZAPISY
                SET czy_zaliczono = 'T',
                    data_zakonczenia = SYSDATE
                WHERE id_zapisu = rec.id_zapisu;
            ELSE
                UPDATE ZAPISY
                SET czy_zaliczono = 'N'
                WHERE id_zapisu = rec.id_zapisu;
            END IF;
        END LOOP;
    END;

END PKG_OCENY;
/