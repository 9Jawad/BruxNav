# BruxNav

![Java](https://img.shields.io/badge/Java-11%2B-blue?style=flat-square) ![License](https://img.shields.io/badge/license-Academic-lightgrey?style=flat-square)

> **BruxNav** est un calculateur d’itinéraires multimodal couvrant l’ensemble des transports publics belges (STIB, TEC, DeLijn, SNCB). Donnez un arrêt de départ, un arrêt d’arrivée, une heure: il vous renvoie la combinaison optimale de bus, tram, métro, train et marche à pied.

![alt text](https://upload.wikimedia.org/wikipedia/commons/1/17/Belgium_road_map.png)

---

## Sommaire

* [Fonctionnalités](#fonctionnalités)
* [Prérequis](#prérequis)
* [Installation &amp; Compilation](#installation--compilation)
* [Utilisation](#utilisation)
* [Options avancées](#options-avancées)
* [Organisation du projet](#organisation-du-projet)
* [Algorithme](#architecture-et-algorithme)
* [Données](#données)
* [Auteurs](#auteurs)

## Fonctionnalités

* **Multimodal complet**: intègre les jeux de données GTFS des 4 opérateurs belges.
* **Optimisation temporelle**: variante de Dijkstra (A*) tenant compte des horaires et des temps d’attente.
* **Correspondances à pied** avec pénalités configurables.
* **Modes de coût**: minimisation du temps, du nombre de changements, pénalisation sélective par mode, etc.
* **Interface CLI**

## Prérequis

* **JDK11+** (OpenJDK ou Oracle).
* ⚠ Jeux de données CSV structurés dans `BruxNav/GTSF/<Agence>/{routes,stops,trips,stop_times}.csv`.

## Installation & Compilation

```bash
git clone https://github.com/9Jawad/BruxNav.git
cd BruxNav

# clean
mvn clean

# test
mvn test

# Compilation
mvn package
```

## Utilisation

```bash
java -jar stibpath-1.0-SNAPSHOT.jar <gtfs-root> "<srcName>" "<dstName>" "<HH:mm:ss>"

```

### Exemples

```bash
java -jar target\stibpath-1.0-SNAPSHOT.jar .\GTFS "TRONE" "BRUSSELS AIRPORT" "04:30:00"
```

### Options avancées

| Option                | Description                              | Valeur par défaut  |
| --------------------- | ---------------------------------------- | ------------------ |
| `--min-changes`       | Minimise le nombre de correspondances    | *désactivé*        |
| `--avoid=<MODE>`      | Évite un mode (TRAIN, TRAM, BUS, METRO)  | *désactivé*        |
| `--walk-factor=<k>`   | Multiplie le temps de marche par*k*      | *désactivé*        |

Pas eu le temps d'implémenter malheureusement...

## Organisation du projet

```txt
BruxNav/
├── GTFS/
│   ├── DELIJN/
│   │   └── {routes.csv, stops.csv, trips.csv, stop_times.csv}
│   ├── SNCB/
│   ├── STIB/
│   └── TEC/
│
├── src/
│   └── main/
│       └── java/
│           └── be/
│               └── ulb/
│                   └── stib/
│                       ├── Main.java
│                       ├── algo/
│                       │   └── AStarTD.java                      # Algorithme A* time-dependent
│                       ├── core/
│                       │   ├── Edge.java
│                       │   ├── Route.java
│                       │   ├── Stop.java
│                       │   ├── StopTime.java
│                       │   ├── TransitEdge.java
│                       │   ├── Trip.java
│                       │   └── WalkEdge.java
│                       ├── data/
│                       │   ├── AgencyModel.java
│                       │   ├── GlobalModel.java
│                       │   └── StringPool.java
│                       ├── graph/
│                       │   └── MultiModalGraph.java
│                       ├── output/
│                       │   └── ItineraryFormatter.java
│                       ├── parsing/
│                       │   ├── RouteLoader.java
│                       │   ├── StopLoader.java
│                       │   ├── StopTimesLoader.java
│                       │   └── TripLoader.java
│                       ├── spatial/
│                       │   ├── KDTree.java
│                       │   ├── Node.java
│                       │   ├── TransitEdgeGenerator.java
│                       │   └── WalkEdgeGenerator.java
│                       └── tools/
│                           ├── CsvReader.java
│                           └── Utils.java
│
├── pom.xml
└── README.md

```

## Architecture et algorithme

1. **Chargement GTFS**: via `AgencyModel` (maps).
2. **Fusion**: `GlobalModel` (union des maps + pools de chaînes unifiés).
3. **KD-Tree**: recherche binaire pour trouver le voisinage spatial
4. **Génération d’arcs** :
   piétons (`WalkEdgeGenerator`, rayon r)

   (arcs bidirectionnels ajoutés en fonction d'un rayon <1km.)

   transit (`TransitEdgeGenerator`, séquence de Trip)
5. **Graphe multimodal**: `HashMap<StopId, List<Edge>>`.
6. **A***: dépendant du temps, pénalités correspondance.
7. **ItineraryFormatter**: sortie lisible (format présent dans le PDF du projet).

Complexité: **O(k logN)**.

## Données

Chaque agence fournit:

| Fichier            | Contenu                   |
| ------------------ | ------------------------- |
| `routes.csv`     | Métadonnées des lignes  |
| `stops.csv`      | Coordonnées des arrêts  |
| `trips.csv`      | Identifiants des trajets  |
| `stop_times.csv` | Horaires au pas d’arrêt |

Placez les CSV dans `GTFS/<Agence>/`.

## Auteurs

* *Cherkaoui Jawad (576517)*

---

© 2025 — Projet académique INFO‑F203, Université libre de Bruxelles.
