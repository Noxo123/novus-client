# Novus Client

Client Fabric léger pour **Minecraft 1.20.1**, avec une interface d'accueil personnalisée pour Novus.

## Objectif

Novus remplace l'écran titre vanilla par une interface simple et lisible :

- accueil Novus ;
- section modpack ;
- accès rapide au multijoueur ;
- accès aux paramètres Minecraft ;
- bouton pour quitter le jeu ;
- interface responsive basée sur les dimensions de l'écran.

## Stack

- Minecraft 1.20.1
- Java 17
- Fabric Loader 0.17.2+
- Fabric API 0.92.2+
- Fabric Loom 1.6
- Gradle 8.8+

## Build

Avec Gradle :

```bash
gradle clean build
```

Le JAR est généré dans `build/libs/`.

## Installation

1. Installer Fabric pour Minecraft 1.20.1.
2. Installer Fabric API.
3. Placer le JAR Novus Client dans le dossier `mods`.
4. Lancer Minecraft avec le profil Fabric.

## Structure

```text
src/main/java/fr/novus/client/
├── NovusClient.java
└── NovusTitleScreen.java

src/main/resources/
└── fabric.mod.json
```

## Notes

Le client ne modifie pas le fonctionnement du monde. Son rôle actuel est principalement de fournir une expérience d'accueil Novus propre et légère.
