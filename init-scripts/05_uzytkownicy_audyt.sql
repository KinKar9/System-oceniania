
-- 1. Utworzenie roli z uprawnieniem EXECUTE
DECLARE
role_exists NUMBER;
BEGIN
SELECT COUNT(*) INTO role_exists FROM dba_roles WHERE role = 'DB_PROCEXECUTOR';
IF role_exists = 0 THEN
      EXECUTE IMMEDIATE 'CREATE ROLE db_procexecutor';
EXECUTE IMMEDIATE 'GRANT EXECUTE ANY PROCEDURE TO db_procexecutor';
DBMS_OUTPUT.PUT_LINE('Rola db_procexecutor utworzona.');
ELSE
      DBMS_OUTPUT.PUT_LINE('Rola db_procexecutor już istnieje.');
END IF;
END;
/

-- 2. Tworzenie użytkowników (jeśli nie istnieją)
DECLARE
user_exists NUMBER;
BEGIN
   -- Administrator
SELECT COUNT(*) INTO user_exists FROM dba_users WHERE username = 'ADMINISTRATOR';
IF user_exists = 0 THEN
      EXECUTE IMMEDIATE 'CREATE USER administrator IDENTIFIED BY "Admin123!"';
EXECUTE IMMEDIATE 'GRANT DBA TO administrator';
DBMS_OUTPUT.PUT_LINE('Użytkownik administrator utworzony.');
ELSE
      DBMS_OUTPUT.PUT_LINE('Użytkownik administrator już istnieje.');
END IF;

   -- ApplicationIdentity
SELECT COUNT(*) INTO user_exists FROM dba_users WHERE username = 'APP_IDENTITY';
IF user_exists = 0 THEN
      EXECUTE IMMEDIATE 'CREATE USER app_identity IDENTIFIED BY "AppPass456!"';
EXECUTE IMMEDIATE 'GRANT CONNECT, RESOURCE TO app_identity';
EXECUTE IMMEDIATE 'GRANT CREATE SESSION TO app_identity';
DBMS_OUTPUT.PUT_LINE('Użytkownik app_identity utworzony.');
ELSE
      DBMS_OUTPUT.PUT_LINE('Użytkownik app_identity już istnieje.');
END IF;

   -- Developer 1
SELECT COUNT(*) INTO user_exists FROM dba_users WHERE username = 'DEVELOPER1';
IF user_exists = 0 THEN
      EXECUTE IMMEDIATE 'CREATE USER developer1 IDENTIFIED BY "Dev1Pass!"';
EXECUTE IMMEDIATE 'GRANT CONNECT TO developer1';
DBMS_OUTPUT.PUT_LINE('Użytkownik developer1 utworzony.');
ELSE
      DBMS_OUTPUT.PUT_LINE('Użytkownik developer1 już istnieje.');
END IF;

   -- Developer 2
SELECT COUNT(*) INTO user_exists FROM dba_users WHERE username = 'DEVELOPER2';
IF user_exists = 0 THEN
      EXECUTE IMMEDIATE 'CREATE USER developer2 IDENTIFIED BY "Dev2Pass!"';
EXECUTE IMMEDIATE 'GRANT CONNECT TO developer2';
DBMS_OUTPUT.PUT_LINE('Użytkownik developer2 utworzony.');
ELSE
      DBMS_OUTPUT.PUT_LINE('Użytkownik developer2 już istnieje.');
END IF;
END;
/

-- 3. Nadanie uprawnień CRUD dla app_identity na tabelach schematu SYSTEM
BEGIN
FOR t IN (SELECT table_name FROM all_tables WHERE owner = 'SYSTEM') LOOP
      EXECUTE IMMEDIATE 'GRANT SELECT, INSERT, UPDATE, DELETE ON SYSTEM.' || t.table_name || ' TO app_identity';
END LOOP;
   DBMS_OUTPUT.PUT_LINE('Uprawnienia CRUD dla app_identity nadane na tabelach SYSTEM.');
END;
/

-- Nadanie roli db_procexecutor dla app_identity
GRANT db_procexecutor TO app_identity;

-- 4. Nadanie prawa SELECT dla developerów na tabelach SYSTEM
BEGIN
FOR t IN (SELECT table_name FROM all_tables WHERE owner = 'SYSTEM') LOOP
      EXECUTE IMMEDIATE 'GRANT SELECT ON SYSTEM.' || t.table_name || ' TO developer1, developer2';
END LOOP;
   DBMS_OUTPUT.PUT_LINE('Uprawnienia SELECT dla developerów nadane.');
END;
/

-- 5. Konfiguracja Unified Auditing (polityka app_full_audit)
-- Usunięcie starej polityki (jeśli istnieje)
BEGIN
EXECUTE IMMEDIATE 'NOAUDIT POLICY app_full_audit';
EXECUTE IMMEDIATE 'DROP AUDIT POLICY app_full_audit';
DBMS_OUTPUT.PUT_LINE('Stara polityka audytu usunięta.');
EXCEPTION
   WHEN OTHERS THEN
      DBMS_OUTPUT.PUT_LINE('Brak starej polityki lub błąd przy usuwaniu.');
END;
/

-- Tworzenie nowej polityki dla kluczowych tabel w SYSTEM
CREATE AUDIT POLICY app_full_audit
  ACTIONS INSERT, UPDATE, DELETE, SELECT ON SYSTEM.STUDENCI,
                  INSERT, UPDATE, DELETE, SELECT ON SYSTEM.PRACOWNICY,
                          INSERT, UPDATE, DELETE, SELECT ON SYSTEM.SALE,
                                  INSERT, UPDATE, DELETE, SELECT ON SYSTEM.PRZEDMIOTY,
                                          INSERT, UPDATE, DELETE, SELECT ON SYSTEM.OCENY,
                                                  INSERT, UPDATE, DELETE, SELECT ON SYSTEM.GRUPY,
                                                          INSERT, UPDATE, DELETE, SELECT ON SYSTEM.ZAPISY,
                                                                  INSERT, UPDATE, DELETE, SELECT ON SYSTEM.SLOWNIK_OCEN,
                                                                          INSERT, UPDATE, DELETE, SELECT ON SYSTEM.WARUNKI_ZAL,
                                                                                  INSERT, UPDATE, DELETE, SELECT ON SYSTEM.UZYTKOWNICY,
                                                                                          INSERT, UPDATE, DELETE, SELECT ON SYSTEM.ROLE,
                                                                                                  INSERT, UPDATE, DELETE, SELECT ON SYSTEM.UZYTKOWNICY_ROLE,
                                                                                                          INSERT, UPDATE, DELETE, SELECT ON SYSTEM.HISTORIA_OCEN,
                                                                                                                  INSERT, UPDATE, DELETE, SELECT ON SYSTEM.LOGI_SYSTEMU,
                                                                                                                          INSERT, UPDATE, DELETE, SELECT ON SYSTEM.RANKINGI;

-- Włączenie polityki
AUDIT POLICY app_full_audit;

-- Potwierdzenie
SELECT policy_name, enabled_option FROM audit_unified_enabled_policies WHERE policy_name = 'APP_FULL_AUDIT';
