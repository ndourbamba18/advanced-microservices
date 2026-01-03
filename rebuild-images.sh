#!/bin/bash

# Couleurs pour la console
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # Pas de couleur

echo -e "${BLUE}--- Début du processus de nettoyage et de build ---${NC}"

# 1. Nettoyage des conteneurs existants (optionnel mais recommandé)
echo -e "${GREEN}Arrêt des conteneurs en cours...${NC}"
docker-compose down

# 2. Nettoyage des images locales sur Docker Hub (ndourbamba18)
# Cette commande supprime les images locales qui correspondent à ton nom d'utilisateur
echo -e "${GREEN}Suppression des anciennes images locales de ndourbamba18...${NC}"
docker rmi $(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'ndourbamba18') --force 2>/dev/null

# 3. Nettoyage Maven
echo -e "${GREEN}Nettoyage des dossiers target...${NC}"
mvn clean

# 4. Build et Push via Jib
# Note: 'jib:build' pousse sur Docker Hub, 'jib:dockerBuild' construit uniquement en local
echo -e "${BLUE}Construction des nouvelles images et Push vers Docker Hub...${NC}"
mvn compile jib:build

echo -e "${BLUE}--- Processus terminé avec succès ! ---${NC}"