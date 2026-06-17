-- ============================================================
-- TRIGGER AUDYTU DLA PLANU ZAJĘĆ
-- ============================================================

CREATE OR REPLACE FUNCTION trg_audyt_plan_zajec()
RETURNS TRIGGER AS $$
DECLARE
v_user VARCHAR(50);
BEGIN
    v_user := CURRENT_USER;

    IF (TG_OP = 'INSERT') THEN
        INSERT INTO logi_systemu (username, akcja, data_akcji, ip_adres)
        VALUES (v_user, 'Dodano plan: ' || COALESCE(NEW.nazwa, 'brak nazwy'), CURRENT_DATE, NULL);
RETURN NEW;

ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO logi_systemu (username, akcja, data_akcji, ip_adres)
        VALUES (v_user, 'Zmieniono plan: ' || COALESCE(NEW.nazwa, 'brak nazwy') || ' (ID: ' || COALESCE(NEW.id::text, '?') || ')', CURRENT_DATE, NULL);
RETURN NEW;

ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO logi_systemu (username, akcja, data_akcji, ip_adres)
        VALUES (v_user, 'Usunięto plan: (ID: ' || COALESCE(OLD.id::text, '?') || ')', CURRENT_DATE, NULL);
RETURN OLD;
END IF;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_audyt_plany
AFTER INSERT OR UPDATE OR DELETE ON plany_zajec
    FOR EACH ROW EXECUTE FUNCTION trg_audyt_plan_zajec();

-- ============================================================
-- TRIGGER WERYFIKACJI KOLIZJI DLA POZYCJI PLANU
-- ============================================================

CREATE OR REPLACE FUNCTION trg_sprawdz_kolizje()
RETURNS TRIGGER AS $$
DECLARE
v_konflikt INT;
BEGIN
    -- Walidacja godzin
    IF NEW.godzina_rozpoczecia >= NEW.godzina_zakonczenia THEN
        RAISE EXCEPTION 'Godzina rozpoczęcia musi być wcześniejsza niż godzina zakończenia!';
END IF;

    -- Sprawdzenie kolizji sali
SELECT COUNT(*) INTO v_konflikt
FROM pozycje_planu
WHERE sala_id = NEW.sala_id
  AND dzien_tygodnia = NEW.dzien_tygodnia
  AND (TG_OP = 'UPDATE' AND id != NEW.id OR TG_OP = 'INSERT')
  AND (
    (NEW.godzina_rozpoczecia < godzina_zakonczenia AND NEW.godzina_zakonczenia > godzina_rozpoczecia)
    );

IF v_konflikt > 0 THEN
        RAISE EXCEPTION 'Sala jest już zajęta w tym terminie!';
END IF;

    -- Sprawdzenie kolizji prowadzącego
SELECT COUNT(*) INTO v_konflikt
FROM pozycje_planu
WHERE prowadzacy_id = NEW.prowadzacy_id
  AND dzien_tygodnia = NEW.dzien_tygodnia
  AND (TG_OP = 'UPDATE' AND id != NEW.id OR TG_OP = 'INSERT')
  AND (
    (NEW.godzina_rozpoczecia < godzina_zakonczenia AND NEW.godzina_zakonczenia > godzina_rozpoczecia)
    );

IF v_konflikt > 0 THEN
        RAISE EXCEPTION 'Prowadzący ma już zajęcia w tym terminie!';
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_sprawdz_kolizje
BEFORE INSERT OR UPDATE ON pozycje_planu
                               FOR EACH ROW EXECUTE FUNCTION trg_sprawdz_kolizje();