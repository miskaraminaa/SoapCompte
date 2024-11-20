# Gestion des Comptes Bancaires (Épargne et Courant)

Ce projet est une application Android permettant de gérer des comptes bancaires (épargne et courant) en utilisant un service web SOAP implémenté avec Spring. L'application consomme ce service via la bibliothèque **ksoap2**.

## Fonctionnalités

- **Consulter les comptes :** Affiche la liste des comptes disponibles.
- **Créer un compte :** Permet d'ajouter un compte avec un solde initial et un type (épargne ou courant).
- **Modifier un compte :** Met à jour les informations d'un compte existant.
- **Supprimer un compte :** Supprime un compte de la base de données.

## Technologies utilisées

### Frontend
- **Android (Kotlin)** :
  - Interface utilisateur développée avec XML.
  - Utilisation de **ksoap2** pour consommer le service SOAP.

### Backend
- **Spring Boot** :
  - Implémentation du service SOAP avec `Jakarta JAX-WS`.
  - Utilisation de JPA pour gérer les entités `Compte`.

### Outils
- **SOAPUI** : Test des services SOAP.

---

## Installation et Configuration

### 1. Backend Spring
1. Clonez le projet backend.
2. Configurez une base de données (par exemple MySQL ou H2).
3. Lancez l'application Spring Boot.
4. Accédez à l'interface WSDL générée à l'adresse suivante :  
http://localhost:8082/services/ws?wsdl


### 2. Test avec SOAPUI
1. Installez **SOAPUI** sur votre système.
2. Importez le fichier WSDL généré (`wsdl` fourni par le backend).
3. Créez des requêtes pour tester les opérations SOAP : `getComptes`, `createCompte`, `updateCompte`, `deleteCompte`.
4. Vérifiez que les services répondent correctement avant de connecter l'application Android.
![SOAPUI](https://github.com/user-attachments/assets/ee4ecdc6-bc13-4f64-bd3f-d1d9ab9a1e22)


### 3. Application Android
1. Clonez le projet Android.
2. Ajoutez la bibliothèque **ksoap2** au fichier `build.gradle` :
```groovy
implementation 'com.google.code.ksoap2-android:ksoap2-android:3.6.4'

````


### 4. Demo
https://github.com/user-attachments/assets/150d4884-5668-4984-a74f-72b284b72255


