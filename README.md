# 🎓 NovaLearn - Plateforme de Gestion Éducative Intelligente

**NovaLearn** est une application desktop JavaFX robuste et moderne conçue pour la gestion complète d'une institution éducative. Elle intègre des fonctionnalités avancées comme la génération de contenu par IA, des prévisions météo en temps réel et des outils analytiques puissants.

---

## 🚀 Fonctionnalités Clés

### 📅 Gestion des Événements
- **Catalogue d'Événements** : Liste complète avec filtrage par type.
- **Participation Intelligente** : 
  - ✨ **Motivation par IA** : Génération automatique de lettre de motivation via l'API Groq (Llama 3).
  - 🌡️ **Prévisions Météo** : Affichage en temps réel des conditions météo sur le lieu de l'événement pour conseiller les participants.
- **Export PDF** : Génération de rapports professionnels pour les listes d'événements et de participations.

### 📝 Gestion des Quiz & Cours
- **Quiz Interactifs** : Création et passage de quiz avec différents types de questions.
- **Résultats & Feedback** : Suivi des notes et correction automatique.

### 👥 Gestion des Utilisateurs
- **Multi-Rôles** : Tableaux de bord dédiés pour Administrateurs, Enseignants et Étudiants.
- **Sécurité** : Authentification sécurisée, hachage des mots de passe et gestion des sessions.
- **Profils** : Gestion complète des données personnelles (matricule, niveau d'étude, etc.).

### 📊 Statistiques & Analytics
- **Dashboards Visuels** : Graphiques (PieChart, BarChart) pour analyser la répartition des événements et le taux de participation.

---

## 🛠️ Stack Technique

- **Langage** : Java 17+
- **Interface Graphique** : JavaFX 21
- **Base de Données** : MySQL (JDBC)
- **IA** : API Groq (Modèle Llama 3.1)
- **APIs Externes** : OpenWeatherMap (Météo)
- **Rapports** : iTextPDF
- **Build Tool** : Maven

---

## ⚙️ Installation & Configuration

### 1. Prérequis
- Java JDK 17 ou supérieur.
- Maven.
- MySQL Server.

### 2. Base de Données
Importez le schéma SQL dans votre base de données locale (nom par défaut : `gestion_utilisateur`).
Configurez l'accès dans `src/main/java/utils/DBConnection.java`.

### 3. Secrets & Clés API
Copiez le fichier d'exemple et renommez-le :
```bash
cp src/main/resources/app-secrets.properties.example src/main/resources/app-secrets.properties
```
Remplissez les informations suivantes dans `app-secrets.properties` :
- `groq.apiKey` : Votre clé API Groq pour l'IA.
- `mail.appPassword` : Mot de passe d'application pour l'envoi d'emails.
- Les accès DB si différents du défaut.

---

## 🏃 Lancement

Exécutez la commande suivante à la racine du projet :
```bash
mvn javafx:run
```

---

## 📁 Structure du Projet

- `src/main/java/entities` : Modèles de données (Utilisateur, Evenement, Participation, Quiz...).
- `src/main/java/services` : Logique métier et accès DB (EvenementService, AuthService...).
- `src/main/java/gui` : Contrôleurs JavaFX pour les vues.
- `src/main/java/utils` : Utilitaires (IA, Météo, PDF, Session).
- `src/main/resources/gui` : Fichiers FXML et CSS.

---

## 🤝 Contribution
Ce projet a été réalisé dans le cadre du module PIDEV. Toute contribution est la bienvenue via Pull Requests.

---
**NovaLearn** - *Apprendre plus intelligemment.*
