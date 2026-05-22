@echo off

:: Avvia il servizio Docker
docker-compose up -d

:: Attendi che il servizio psql sia pronto
echo Aspettando che psql sia pronto...
timeout /t 15