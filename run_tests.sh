#!/bin/bash
# Script de lancement rapide des tests
# Parc Attractions - Suite de tests complète

echo ""
echo "╔════════════════════════════════════════════════╗"
echo "║   LANCEMENT DES TESTS - PARC ATTRACTIONS      ║"
echo "║   25 Tests Unitaires + Fonctionnels           ║"
echo "╚════════════════════════════════════════════════╝"
echo ""

cd "$(dirname "$0")"

# Compiler les sources
echo "[1/3] Compilation des classes source..."
javac -encoding UTF-8 src/Main.java src/main/java/com/parcattractions/**/*.java
if [ $? -ne 0 ]; then
    echo "Erreur lors de la compilation des sources"
    exit 1
fi
echo "✓ Sources compilées"

# Compiler les tests
echo "[2/3] Compilation des tests..."
javac -encoding UTF-8 -cp "./src/main/java" src/test/java/com/parcattractions/**/*.java
if [ $? -ne 0 ]; then
    echo "Erreur lors de la compilation des tests"
    exit 1
fi
echo "✓ Tests compilés"

# Exécuter les tests
echo "[3/3] Exécution de la suite de tests..."
echo ""
java -cp "./src:./src/main/java:./src/test/java" test.java.com.parcattractions.TestRunner

if [ $? -ne 0 ]; then
    echo ""
    echo "✗ Des tests ont échoué"
    exit 1
else
    echo ""
    echo "✓ Tous les tests sont passés avec succès!"
    echo "  Rapport sauvegardé en: logs/RapportTests_*.txt"
    exit 0
fi
