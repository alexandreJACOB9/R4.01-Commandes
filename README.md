# Microservice Commandes

Cette API REST permet de gérer des commandes de repas, en appliquant les principes de la **Clean Architecture** (séparation stricte entre le Domaine, l'Application et l'Infrastructure).

## Sécurité
Pour des raisons de sécurité, les identifiants de la base de données ne sont pas versionnés sur GitHub. 

## Lancement
1. Compilez le projet avec Maven (`mvn clean install`).
2. Déployez le `.war` généré sur un serveur d'application
3. L'API sera accessible à l'adresse (par défaut) : `http://localhost:8080/R4.01-Commandes-1.0-SNAPSHOT/api/orders`

## Endpoints de l'API
* **GET** `/api/orders` : Liste toutes les commandes (avec leurs lignes).
* **GET** `/api/orders/{id}` : Récupère une commande spécifique.
* **POST** `/api/orders` : Crée une nouvelle commande (calcule le total via l'API Menus).
* **PUT** `/api/orders/{id}` : Met à jour la date et l'adresse de livraison.
* **DELETE** `/api/orders/{id}` : Supprime une commande et ses lignes associées.


La documentaion du code ainsi que la rédaction du read me a été faites en assistance avec l'ia 
