CREATE OR REPLACE TRIGGER trg_audyt_ocen
AFTER INSERT OR UPDATE OR DELETE ON Oceny_Czastkowe
FOR EACH ROW
DECLARE
    v_operacja VARCHAR2(20);
BEGIN
    --rozpoznajemy, jaka operacja została wykonana
    IF INSERTING THEN
        v_operacja := 'INSERT';
        
        INSERT INTO Logi_Ocen (id_oceny_czastkowej, stara_ocena, nowa_ocena, operacja)
        VALUES (:NEW.id_oceny_czastkowej, NULL, :NEW.ocena, v_operacja);
        
    ELSIF UPDATING THEN
        v_operacja := 'UPDATE';
        
        --zapisujemy tylko wtedy, kiedy ocena faktycznie się zmieniła
        IF :OLD.ocena != :NEW.ocena THEN
            INSERT INTO Logi_Ocen (id_oceny_czastkowej, stara_ocena, nowa_ocena, operacja)
            VALUES (:NEW.id_oceny_czastkowej, :OLD.ocena, :NEW.ocena, v_operacja);
        END IF;
        
    ELSIF DELETING THEN
        v_operacja := 'DELETE';
        
        INSERT INTO Logi_Ocen (id_oceny_czastkowej, stara_ocena, nowa_ocena, operacja)
        VALUES (:OLD.id_oceny_czastkowej, :OLD.ocena, NULL, v_operacja);
    END IF;
END;
/