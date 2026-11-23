package tests;

import business.AppManager;
import data.FileAttente;
import data.Guichet;
import data.SystemStatusListener;
import data.Ticket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class GuichetTicketTest {
    public static void main(String[] args) {
        testerFile();
        testerGuichets();
        System.out.println("Tous les tests métier ont réussi.");
    }

    private static void testerFile() {
        FileAttente file = new FileAttente(2);
        Ticket premier = new Ticket();
        Ticket deuxieme = new Ticket();
        Ticket troisieme = new Ticket();

        verifier(file.retirerProchainTicket() == null, "Retrait dans une file vide");
        verifier(file.ajouterTicket(premier) && file.ajouterTicket(deuxieme), "Ajout de deux tickets");
        verifier(file.estPleine() && !file.ajouterTicket(troisieme), "Refus au-delà de la capacité");
        file.getTicketsEnAttente().clear();
        verifier(file.getTailleActuelle() == 2, "La copie de la file est indépendante");
        verifier(file.retirerProchainTicket() == premier, "Le premier arrivé sort en premier");
        verifier(file.ajouterTicket(troisieme), "Réutilisation d'une place libérée");
        verifier(file.removeTicket(deuxieme), "Départ d'un client en attente");
        verifier(!file.removeTicket(deuxieme), "Départ répété sans effet");
        verifier(file.retirerProchainTicket() == troisieme && file.estVide(), "File vidée dans l'ordre");
    }

    private static void testerGuichets() {
        AppManager app = AppManager.getInstance();
        Observateur observateur = new Observateur();
        app.addListener(observateur);

        Guichet accueil = app.creerGuichet("Accueil");
        Guichet information = app.creerGuichet("Information");
        verifier(app.getTousLesGuichets().size() == 2 && observateur.ouvertures == 2, "Ouverture des guichets");
        verifier(app.appelerProchainClient(accueil) == null, "Appel dans une file vide");

        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < app.getCapaciteMaximaleFile(); i++) {
            tickets.add(app.creerNouveauTicket());
        }
        verifier(app.creerNouveauTicket() == null && observateur.refus == 1, "Notification de file pleine");
        verifier(observateur.file.equals(tickets), "Publication de la file dans l'ordre");

        app.setGuichetEnPause(information, true);
        verifier("EN_PAUSE".equals(observateur.dernierEtat) && information.getTempsDebutPause() != null,
                "Mise en pause");
        verifier(app.appelerProchainClient(information) == null, "Appel refusé pendant la pause");
        app.setGuichetEnPause(information, false);
        verifier("LIBRE".equals(information.getEtat()) && information.getTempsDebutPause() == null, "Reprise");

        Ticket premier = tickets.get(0);
        verifier(app.appelerProchainClient(accueil) == premier, "Appel FIFO");
        verifier(observateur.dernierAppele == premier && app.getTicketsEnService().get(accueil) == premier,
                "Notification de l'appel et enregistrement du service");
        verifier(app.appelerProchainClient(accueil) == null, "Appel refusé pendant le service");
        app.setGuichetEnPause(accueil, true);
        verifier("OCCUPE".equals(accueil.getEtat()) && accueil.getTempsDebutService() != null,
                "Pause refusée pendant le service");

        Ticket supplementaire = app.creerNouveauTicket();
        verifier(supplementaire.getNumero() == tickets.get(4).getNumero() + 1, "Numérotation après un refus");
        verifier(!app.rappelerClient(accueil), "Remise en file refusée si la file est pleine");
        verifier(!app.handleGuichetClosing(accueil), "Fermeture refusée si la file est pleine");
        verifier(app.getTicketsEnService().get(accueil) == premier && app.getTousLesGuichets().containsValue(accueil),
                "Ticket et guichet préservés après les refus");

        app.clientLeftQueue(tickets.get(2));
        verifier(app.getTailleFileActuelle() == 4 && !observateur.file.contains(tickets.get(2)),
                "Notification du départ et place libérée");
        verifier(app.rappelerClient(accueil), "Retour du ticket en fin de file");
        verifier(observateur.file.equals(List.of(tickets.get(1), tickets.get(3), tickets.get(4),
                supplementaire, premier)), "Ordre après retour en file");

        verifier(app.appelerProchainClient(information) == tickets.get(1), "Appel par le second guichet");
        verifier(app.handleGuichetClosing(information), "Fermeture d'un guichet occupé avec une place disponible");
        verifier(!app.getTousLesGuichets().containsValue(information) && !app.getTicketsEnService().containsKey(information),
                "Retrait du guichet fermé et nettoyage du service");
        verifier(observateur.file.get(4) == tickets.get(1), "Ticket du guichet fermé en fin de file");
        verifier(app.appelerProchainClient(information) == null, "Appel refusé sur un guichet fermé");

        Ticket servi = app.appelerProchainClient(accueil);
        app.terminerService(accueil);
        verifier(observateur.dernierServi == servi && app.getTicketsEnService().isEmpty()
                && accueil.getTempsDebutService() == null && "LIBRE".equals(accueil.getEtat()), "Fin de service");
        app.removeListener(observateur);
        app.handleGuichetClosing(accueil);
        verifier("LIBRE".equals(observateur.dernierEtat), "Observateur désabonné");
    }

    private static void verifier(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static class Observateur implements SystemStatusListener {
        private List<Ticket> file = List.of();
        private int ouvertures;
        private int refus;
        private String dernierEtat;
        private Ticket dernierAppele;
        private Ticket dernierServi;

        @Override
        public void onTicketQueueUpdated(Queue<Ticket> fileCourante) { file = List.copyOf(fileCourante); }

        @Override
        public void onNewGuichetOpened(Guichet guichet) { ouvertures++; }

        @Override
        public void onQueueFull() { refus++; }

        @Override
        public void onGuichetStateUpdated(Guichet guichet) { dernierEtat = guichet.getEtat(); }

        @Override
        public void onTicketCalled(Guichet guichet, Ticket ticket) { dernierAppele = ticket; }

        @Override
        public void onTicketServed(Guichet guichet, Ticket ticket) { dernierServi = ticket; }
    }
}
