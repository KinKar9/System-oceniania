-- =====================================================================
-- USTAWIENIE UŻYTKOWNIKÓW / RÓL / UPRAWNIEŃ
-- Wymaga uruchomienia w SQL*Plus / SQLcl, bo używa ACCEPT i UNDEFINE
-- =====================================================================

SET SERVEROUTPUT ON

-- =====================================================================
-- 1. Pobranie haseł
-- =====================================================================
ACCEPT app_owner_pass     CHAR PROMPT 'Podaj hasło dla APP_OWNER: ' HIDE
ACCEPT admin_pass         CHAR PROMPT 'Podaj hasło dla ADMINISTRATOR: ' HIDE
ACCEPT app_identity_pass  CHAR PROMPT 'Podaj hasło dla APP_IDENTITY: ' HIDE
ACCEPT dev1_pass          CHAR PROMPT 'Podaj hasło dla DEVELOPER1: ' HIDE
ACCEPT dev2_pass          CHAR PROMPT 'Podaj hasło dla DEVELOPER2: ' HIDE

-- =====================================================================
-- 2. Tworzenie użytkowników i nadawanie uprawnień
-- =====================================================================
DECLARE
PROCEDURE create_and_grant(
        p_user   VARCHAR2,
        p_pass   VARCHAR2,
        p_grants VARCHAR2
    ) IS
        l_sql         VARCHAR2(4000);
        l_user        VARCHAR2(128);
        l_pass        VARCHAR2(500);
        user_exists   NUMBER;
BEGIN
        -- Bezpieczna nazwa użytkownika
        l_user := DBMS_ASSERT.SIMPLE_SQL_NAME(UPPER(p_user));

        -- Oracle akceptuje hasło w podwójnych cudzysłowach.
        -- Escaping podwójnych cudzysłowów wewnątrz hasła.
        l_pass := REPLACE(p_pass, '"', '""');

SELECT COUNT(*)
INTO user_exists
FROM dba_users
WHERE username = l_user;

IF user_exists = 0 THEN
            l_sql := 'CREATE USER ' || l_user ||
                     ' IDENTIFIED BY "' || l_pass || '"';
EXECUTE IMMEDIATE l_sql;

IF p_grants IS NOT NULL THEN
                EXECUTE IMMEDIATE 'GRANT ' || p_grants || ' TO ' || l_user;
END IF;

            DBMS_OUTPUT.PUT_LINE('Utworzono: ' || l_user);
ELSE
            DBMS_OUTPUT.PUT_LINE('Istnieje: ' || l_user);
END IF;

EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Błąd przy tworzeniu ' || p_user || ': ' || SQLERRM);
            RAISE;
END create_and_grant;
BEGIN
    -- APP_OWNER
    create_and_grant(
        'APP_OWNER',
        '&&app_owner_pass',
        'CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE PROCEDURE, CREATE SEQUENCE, UNLIMITED TABLESPACE'
    );

    -- Rola wykonawcza
BEGIN
EXECUTE IMMEDIATE 'CREATE ROLE db_procexecutor';
EXCEPTION
        WHEN OTHERS THEN
            -- ORA-01921: role name conflicts with another user or role name
            IF SQLCODE != -1921 THEN
                RAISE;
END IF;
END;

    -- Nadanie EXECUTE dla procedur/funkcji APP_OWNER
FOR rec IN (
        SELECT object_name
        FROM dba_objects
        WHERE owner = 'APP_OWNER'
          AND object_type IN ('PROCEDURE', 'FUNCTION')
    ) LOOP
BEGIN
EXECUTE IMMEDIATE
    'GRANT EXECUTE ON APP_OWNER.' ||
    DBMS_ASSERT.SIMPLE_SQL_NAME(rec.object_name) ||
    ' TO db_procexecutor';
EXCEPTION
            WHEN OTHERS THEN
                NULL;
END;
END LOOP;

    -- ADMINISTRATOR
    create_and_grant(
        'ADMINISTRATOR',
        '&&admin_pass',
        'CREATE SESSION, CREATE TABLE, CREATE VIEW, CREATE PROCEDURE, CREATE SEQUENCE, UNLIMITED TABLESPACE'
    );

EXECUTE IMMEDIATE 'GRANT db_procexecutor TO ADMINISTRATOR';

-- APP_IDENTITY
create_and_grant(
        'APP_IDENTITY',
        '&&app_identity_pass',
        'CONNECT, RESOURCE'
    );

    -- DEVELOPER1 / DEVELOPER2
    create_and_grant('DEVELOPER1', '&&dev1_pass', 'CONNECT');
    create_and_grant('DEVELOPER2', '&&dev2_pass', 'CONNECT');

    -- CRUD dla APP_IDENTITY na wszystkich tabelach APP_OWNER
FOR t IN (
        SELECT table_name
        FROM dba_tables
        WHERE owner = 'APP_OWNER'
    ) LOOP
BEGIN
EXECUTE IMMEDIATE
    'GRANT SELECT, INSERT, UPDATE, DELETE ON APP_OWNER.' ||
    DBMS_ASSERT.SIMPLE_SQL_NAME(t.table_name) ||
    ' TO APP_IDENTITY';
EXCEPTION
            WHEN OTHERS THEN
                NULL;
END;
END LOOP;

EXECUTE IMMEDIATE 'GRANT db_procexecutor TO APP_IDENTITY';

-- SELECT dla developerów
FOR t IN (
        SELECT table_name
        FROM dba_tables
        WHERE owner = 'APP_OWNER'
    ) LOOP
BEGIN
EXECUTE IMMEDIATE
    'GRANT SELECT ON APP_OWNER.' ||
    DBMS_ASSERT.SIMPLE_SQL_NAME(t.table_name) ||
    ' TO DEVELOPER1';
EXCEPTION
            WHEN OTHERS THEN
                NULL;
END;

BEGIN
EXECUTE IMMEDIATE
    'GRANT SELECT ON APP_OWNER.' ||
    DBMS_ASSERT.SIMPLE_SQL_NAME(t.table_name) ||
    ' TO DEVELOPER2';
EXCEPTION
            WHEN OTHERS THEN
                NULL;
END;
END LOOP;

    -- =================================================================
    -- 3. Audyt (Unified Auditing)
    -- =================================================================
BEGIN
EXECUTE IMMEDIATE 'NOAUDIT POLICY app_full_audit';
EXCEPTION
        WHEN OTHERS THEN
            NULL;
END;

BEGIN
EXECUTE IMMEDIATE 'DROP AUDIT POLICY app_full_audit';
EXCEPTION
        WHEN OTHERS THEN
            NULL;
END;

BEGIN
EXECUTE IMMEDIATE q'[
            CREATE AUDIT POLICY app_full_audit ACTIONS
                INSERT ON APP_OWNER.STUDENCI,
                UPDATE ON APP_OWNER.STUDENCI,
                DELETE ON APP_OWNER.STUDENCI,
                SELECT ON APP_OWNER.STUDENCI,

                INSERT ON APP_OWNER.PRACOWNICY,
                UPDATE ON APP_OWNER.PRACOWNICY,
                DELETE ON APP_OWNER.PRACOWNICY,
                SELECT ON APP_OWNER.PRACOWNICY,

                INSERT ON APP_OWNER.SALE,
                UPDATE ON APP_OWNER.SALE,
                DELETE ON APP_OWNER.SALE,
                SELECT ON APP_OWNER.SALE,

                INSERT ON APP_OWNER.PRZEDMIOTY,
                UPDATE ON APP_OWNER.PRZEDMIOTY,
                DELETE ON APP_OWNER.PRZEDMIOTY,
                SELECT ON APP_OWNER.PRZEDMIOTY,

                INSERT ON APP_OWNER.OCENY,
                UPDATE ON APP_OWNER.OCENY,
                DELETE ON APP_OWNER.OCENY,
                SELECT ON APP_OWNER.OCENY,

                INSERT ON APP_OWNER.GRUPY,
                UPDATE ON APP_OWNER.GRUPY,
                DELETE ON APP_OWNER.GRUPY,
                SELECT ON APP_OWNER.GRUPY,

                INSERT ON APP_OWNER.ZAPISY,
                UPDATE ON APP_OWNER.ZAPISY,
                DELETE ON APP_OWNER.ZAPISY,
                SELECT ON APP_OWNER.ZAPISY,

                INSERT ON APP_OWNER.SLOWNIK_OCEN,
                UPDATE ON APP_OWNER.SLOWNIK_OCEN,
                DELETE ON APP_OWNER.SLOWNIK_OCEN,
                SELECT ON APP_OWNER.SLOWNIK_OCEN,

                INSERT ON APP_OWNER.WARUNKI_ZAL,
                UPDATE ON APP_OWNER.WARUNKI_ZAL,
                DELETE ON APP_OWNER.WARUNKI_ZAL,
                SELECT ON APP_OWNER.WARUNKI_ZAL,

                INSERT ON APP_OWNER.UZYTKOWNICY,
                UPDATE ON APP_OWNER.UZYTKOWNICY,
                DELETE ON APP_OWNER.UZYTKOWNICY,
                SELECT ON APP_OWNER.UZYTKOWNICY,

                INSERT ON APP_OWNER.ROLE,
                UPDATE ON APP_OWNER.ROLE,
                DELETE ON APP_OWNER.ROLE,
                SELECT ON APP_OWNER.ROLE,

                INSERT ON APP_OWNER.UZYTKOWNICY_ROLE,
                UPDATE ON APP_OWNER.UZYTKOWNICY_ROLE,
                DELETE ON APP_OWNER.UZYTKOWNICY_ROLE,
                SELECT ON APP_OWNER.UZYTKOWNICY_ROLE,

                INSERT ON APP_OWNER.HISTORIA_OCEN,
                UPDATE ON APP_OWNER.HISTORIA_OCEN,
                DELETE ON APP_OWNER.HISTORIA_OCEN,
                SELECT ON APP_OWNER.HISTORIA_OCEN,

                INSERT ON APP_OWNER.LOGI_SYSTEMU,
                UPDATE ON APP_OWNER.LOGI_SYSTEMU,
                DELETE ON APP_OWNER.LOGI_SYSTEMU,
                SELECT ON APP_OWNER.LOGI_SYSTEMU,

                INSERT ON APP_OWNER.RANKINGI,
                UPDATE ON APP_OWNER.RANKINGI,
                DELETE ON APP_OWNER.RANKINGI,
                SELECT ON APP_OWNER.RANKINGI,

                INSERT ON APP_OWNER.KIERUNKI,
                UPDATE ON APP_OWNER.KIERUNKI,
                DELETE ON APP_OWNER.KIERUNKI,
                SELECT ON APP_OWNER.KIERUNKI
        ]';
EXECUTE IMMEDIATE 'AUDIT POLICY app_full_audit';
DBMS_OUTPUT.PUT_LINE('Polityka audytu włączona.');
EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Błąd przy polityce audytu: ' || SQLERRM);
END;

END;
/

-- =====================================================================
-- 4. Czyszczenie zmiennych substitution
-- =====================================================================
UNDEFINE app_owner_pass
UNDEFINE admin_pass
UNDEFINE app_identity_pass
UNDEFINE dev1_pass
UNDEFINE dev2_pass