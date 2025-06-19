# Projet : PayMyBuddy

Application de paiement entre particuliers (Version prototype)

Permet la gestion des connexions et des virements entre utilisateurs.

---

## 🛠️ Outils & Librairies utilisées

Projet basé sur **Spring Boot** et **Maven** connecté à une base **PostgreSQL** :

- **Spring Boot** : 3.5.0
- **Java** : 21
- **PostgreSQL** : 17
- **Apache Maven** : 3.9.9

### 📦 Starters Spring utilisés

- `spring-boot-starter-web`  
- `spring-boot-starter-data-jpa`  
- `spring-boot-starter-security`  
- `spring-boot-starter-validation`  
- `spring-boot-devtools`  
- `lombok`  
- `postgresql` (driver)
---

## 🗃️ Installation rapide

1. **Récupérez le projet** (clonez le dépôt ou téléchargez les sources).
2. Le fichier `application.properties` est déjà  configuré pour se connecter à la base de données (adapter eventuellement si chemin ou paramétrage specifique desiré).
3. **Initialisez la base de données** avec le fichier `sauvegarde_paymybuddy.sql` 
4. **Lancer** l'application avec "mvn spring-boot:run"

![MPD](MPD.png)