# Guichet Ticket

Guichet Ticket est un **ancien projet académique** réalisé dans le cadre d’un cours de programmation orientée objet. Cette application de bureau simule l’accueil de clients dans une administration équipée de plusieurs guichets.

Elle utilise **Java et Swing** et se compile avec les outils du JDK, sans dépendance externe.

## Fonctionnalités

- Prendre un ticket numéroté et consulter son temps d’attente dans une fenêtre client. Le client peut quitter la file via le bouton « Je pars » ou en fermant sa fenêtre.
- Gérer une file FIFO de cinq tickets en attente. Le distributeur signale quand la file est pleine.
- Ouvrir plusieurs guichets, appeler le prochain client, terminer son service ou mettre un guichet libre en pause.
- Remettre un ticket en fin de file avec « Passer le client » ou en fermant le guichet occupé. Si la file est pleine, le ticket reste au guichet et l’action est refusée.
- Consulter sur le tableau central les tickets en attente, ceux en service et l’état des guichets.

L’application fonctionne en local. Les tickets et les guichets sont conservés en mémoire pendant la session.

## Capture d’écran

Capture originale du projet académique.

![Distributeurs, guichets, fenêtres client et tableau d’affichage de Guichet Ticket](./capture%20de%20%20fonctionemment.png)

## Structure du projet

| Emplacement | Rôle |
| --- | --- |
| `App.java` | Point d’entrée de l’application |
| `data/` | Tickets, file d’attente, états des guichets et interface de notification |
| `business/` | Règles métier et contrôleurs |
| `gui/` | Fenêtres Swing, tableau central et gestion des fenêtres client |
| `tests/` | Tests autonomes du fonctionnement métier |

## Compiler et lancer

Prérequis : JDK 17 ou version ultérieure, et un environnement graphique pour lancer Swing. Depuis la racine du projet, exécuter les commandes suivantes.

```text
javac --release 17 -encoding UTF-8 -d out App.java data/*.java business/*.java gui/*.java
java -cp out App
```

Sous PowerShell, si les jokers ne sont pas résolus par `javac`, utiliser cette commande de compilation.

```powershell
$sources = (Get-ChildItem App.java, data/*.java, business/*.java, gui/*.java).FullName
javac --release 17 -encoding UTF-8 -d out $sources
java -cp out App
```

Ouvrir un distributeur, un guichet et le tableau d’affichage depuis le menu principal. Prendre quelques tickets, puis cliquer sur « Appeler Suivant » pour servir le premier client.

## Exécuter les tests

Depuis la racine du projet, compiler les sources et le test, puis exécuter ce dernier.

```text
javac --release 17 -encoding UTF-8 -d out App.java data/*.java business/*.java gui/*.java tests/GuichetTicketTest.java
java -Djava.awt.headless=true -cp out tests.GuichetTicketTest
```

Sous PowerShell, reprendre la commande de compilation ci-dessus en ajoutant `tests/GuichetTicketTest.java` à la liste passée à `Get-ChildItem`.

Les tests couvrent l’ordre FIFO, la capacité de la file, le départ d’un client, les transitions des guichets et les notifications utilisées par le tableau d’affichage.
