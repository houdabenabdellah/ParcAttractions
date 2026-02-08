@echo off
REM Script de lancement rapide des tests
REM Parc Attractions - Suite de tests complète

echo.
echo ╔════════════════════════════════════════════════╗
echo ║   LANCEMENT DES TESTS - PARC ATTRACTIONS      ║
echo ║   25 Tests Unitaires + Fonctionnels           ║
echo ╚════════════════════════════════════════════════╝
echo.

cd /d "%~dp0"

REM Compiler les sources
echo [1/3] Compilation des classes source...
javac -encoding UTF-8 src/Main.java src/main/java/com/parcattractions/**/*.java
if errorlevel 1 (
    echo Erreur lors de la compilation des sources
    exit /b 1
)
echo ✓ Sources compilées

REM Compiler les tests
echo [2/3] Compilation des tests...
javac -encoding UTF-8 -cp "./src/main/java" src/test/java/com/parcattractions/**/*.java
if errorlevel 1 (
    echo Erreur lors de la compilation des tests
    exit /b 1
)
echo ✓ Tests compilés

REM Exécuter les tests
echo [3/3] Exécution de la suite de tests...
echo.
java -cp "./src;./src/main/java;./src/test/java" test.java.com.parcattractions.TestRunner

if errorlevel 1 (
    echo.
    echo ✗ Des tests ont échoué
    pause
    exit /b 1
) else (
    echo.
    echo ✓ Tous les tests sont passés avec succès!
    echo   Rapport sauvegardé en: logs/RapportTests_*.txt
    pause
    exit /b 0
)
