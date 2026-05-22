#!/bin/bash

# Avvia il servizio Docker
docker-compose up -d

# Attendi che il servizio psql sia pronto
echo "Aspettando che PostgreSQL sia pronto..."
sleep 15