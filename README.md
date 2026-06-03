# System Oceniania Studentów 

## Instrukcja Uruchomienia

### Krok 1. Uruchomienie kontenerów
Należy otworzyć terminal w głównym katalogu projektu (tam, gdzie znajduje się plik `docker-compose.yml`) i wykonać polecenie:
```bash
docker compose up -d

### Krok 2 Dane do połączenia z bazą danych
W dowolnym kliencie SQL należy utworzyć nowe połączenie typu Oracle i wprowadzić następujące dane konfiguracyjne:

Host / Serwer: localhost

Port: 1521

SID / Service Name: FREE

Użytkownik: system

Hasło: Projekt2026Haslo