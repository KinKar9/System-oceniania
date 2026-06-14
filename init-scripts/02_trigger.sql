CREATE OR REPLACE TRIGGER trg_audyt_ocen
AFTER INSERT OR UPDATE OR DELETE ON OCENY
    FOR EACH ROW
DECLARE
v_operacja VARCHAR2(20);
    v_user VARCHAR2(50) := USER;
BEGIN
    IF INSERTING THEN
        v_operacja := 'INSERT';
INSERT INTO HISTORIA_OCEN (id_oceny, stara_wartosc, nowa_wartosc, operacja, uzytkownik)
VALUES (:NEW.id_oceny, NULL, :NEW.wartosc, v_operacja, v_user);
ELSIF UPDATING THEN
        IF UPDATING('wartosc') AND (:OLD.wartosc IS DISTINCT FROM :NEW.wartosc) THEN
            v_operacja := 'UPDATE';
INSERT INTO HISTORIA_OCEN (id_oceny, stara_wartosc, nowa_wartosc, operacja, uzytkownik)
VALUES (:NEW.id_oceny, :OLD.wartosc, :NEW.wartosc, v_operacja, v_user);
END IF;
    ELSIF DELETING THEN
        v_operacja := 'DELETE';
INSERT INTO HISTORIA_OCEN (id_oceny, stara_wartosc, nowa_wartosc, operacja, uzytkownik)
VALUES (:OLD.id_oceny, :OLD.wartosc, NULL, v_operacja, v_user);
END IF;
END;
/