# Systems And Distributed Applications - 2024

## Project Setup

### Prérequis
- Java 21
#### Usage de Gradle
Le projet utilise Gradle comme gestionnaire de dépendances. Il ne sera pas nécessaire de l'installer car un wrapper est inclus dans le projet.

Cependant, il y a un rapide setup à faire sur certains IDEs pour que le projet soit correctement interprété.

Voici donc les setups pour les IDEs les plus courants :

### Eclipse Setup
1. Cloner ou copier/coller le repo dans un workspace de sorte que tout le contenu soit dans un même dossier à l'intérieur du workspace.
2. Sur Eclipse, sélectionnez "Import Projects".
3. Cliquez successivement sur "Gradle" > "Existing Gradle project..." puis cliquez sur Next.
4. Dans l'entrée "Project root directory", sélectionnez le dossier issu du clone ou du copier/coller de l'étape 1.
5. Cliquez sur "Finish".
6. Terminé ! Tout le projet ainsi que ses sous-projets (task1, task2...) devraient apparaître dans les projets Eclipse.

### Visual Studio Code Setup
1. Cloner ou copier le repo.
2. Installer l'extension "Java Extension Pack" pour que le projet soit correctement reconnu.
3. (Optionnel mais recommandé) Installer l'extension "Gradle for Java" pour faciliter le lancement des commandes Gradle.
4. Ouvrir le projet dans Visual Studio Code.
5. Terminé ! Le projet devrait être correctement configuré.

### IntelliJ IDEA Setup
1. New > Project from Version Control > Git > Copier l'URL du repo.
2. Ouvrir le projet.
3. Terminé ! Le projet devrait être correctement configuré.

## Structure du Projet
Le projet est divisé en plusieurs sous-projets, chacun correspondant à une tâche du projet (appelées taskX).
Dans chacune de ces tâches, il y a un dossier `src/main` avec le code et un dossier `src/test` avec les tests.

Les tests sont écrits en JUnit 5.

Lancer tous les tests :
```bash
./gradlew test
```

Lancer les tests d'une tâche spécifique :
```bash
./gradlew :taskX:test
```
(où X est le numéro de la tâche)

## Design
Design du projet:
- en noir pour la task4
- en violet la task5

![design](/design.jpg)

## Documentation
Une [API Reference](https://noahkohrs.github.io/Systems-Distributed-Applications-INFO5/) est disponible pour la tâche 5 (soit le projet de groupe) et est générée avec Javadoc.
Elle est accessible à ce lien : https://noahkohrs.github.io/Systems-Distributed-Applications-INFO5/

## Auteurs
- [Noah Kohrs](https://github.com/noahkohrs) **Toute la codebase**
- [Anastasios Tsiompanidis](https://github.com/AnastasiosTsio) **Travail de groupe sur la tâche 4 et 5**
- [Thibaut Haberer](https://github.com/ThibHab) **Travail de groupe sur la tâche 4 et 5**
- [Noé Fléchon](https://github.com/flechonn) **Travail de groupe sur la tâche 4 et 5**
