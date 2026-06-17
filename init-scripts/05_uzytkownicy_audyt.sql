-- Rola wykonawcza
CREATE ROLE db_procexecutor;

-- APP_OWNER
CREATE USER app_owner WITH PASSWORD 'strong_password';
GRANT CONNECT ON DATABASE system_oceniania TO app_owner;
GRANT CREATE, USAGE ON SCHEMA public TO app_owner;
ALTER DEFAULT PRIVILEGES FOR USER app_owner IN SCHEMA public GRANT ALL ON TABLES TO app_owner;

-- ADMINISTRATOR
CREATE USER administrator WITH PASSWORD 'admin_password';
GRANT CONNECT ON DATABASE system_oceniania TO administrator;
GRANT CREATE, USAGE ON SCHEMA public TO administrator;
GRANT db_procexecutor TO administrator;

-- APP_IDENTITY
CREATE USER app_identity WITH PASSWORD 'app_password';
GRANT CONNECT ON DATABASE system_oceniania TO app_identity;
GRANT USAGE ON SCHEMA public TO app_identity;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_identity;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO app_identity;
GRANT db_procexecutor TO app_identity;

-- DEVELOPER1, DEVELOPER2
CREATE USER developer1 WITH PASSWORD 'dev1_pass';
CREATE USER developer2 WITH PASSWORD 'dev2_pass';
GRANT CONNECT ON DATABASE system_oceniania TO developer1;
GRANT CONNECT ON DATABASE system_oceniania TO developer2;
GRANT USAGE ON SCHEMA public TO developer1;
GRANT USAGE ON SCHEMA public TO developer2;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO developer1;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO developer2;

GRANT SELECT, INSERT, UPDATE, DELETE ON plany_zajec TO app_identity;
GRANT SELECT, INSERT, UPDATE, DELETE ON pozycje_planu TO app_identity;
GRANT USAGE ON SEQUENCE plany_zajec_id_seq TO app_identity;
GRANT USAGE ON SEQUENCE pozycje_planu_id_seq TO app_identity;

GRANT SELECT ON plany_zajec TO developer1, developer2;
GRANT SELECT ON pozycje_planu TO developer1, developer2;