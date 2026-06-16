CREATE OR REPLACE FUNCTION trg_audyt_ocen()
RETURNS TRIGGER AS $$
DECLARE
v_operacja VARCHAR(20);
    v_user VARCHAR(50);
BEGIN
    v_user := CURRENT_USER;

    IF (TG_OP = 'INSERT') THEN
        v_operacja := 'INSERT';
INSERT INTO historia_ocen (id_oceny, stara_wartosc, nowa_wartosc, operacja, uzytkownik)
VALUES (NEW.id_oceny, NULL, NEW.wartosc, v_operacja, v_user);
RETURN NEW;

ELSIF (TG_OP = 'UPDATE') THEN
        IF (NEW.wartosc IS DISTINCT FROM OLD.wartosc) THEN
            v_operacja := 'UPDATE';
INSERT INTO historia_ocen (id_oceny, stara_wartosc, nowa_wartosc, operacja, uzytkownik)
VALUES (NEW.id_oceny, OLD.wartosc, NEW.wartosc, v_operacja, v_user);
END IF;
RETURN NEW;

-- Dla DELETE nie wstawiamy rekordu – historię usunie kaskada
ELSIF (TG_OP = 'DELETE') THEN
        RETURN OLD;
END IF;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;