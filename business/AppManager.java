package business;

import data.FileAttente;
import data.Guichet;
import data.SystemStatusListener;
import data.Ticket;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

public class AppManager {
    private static final int CAPACITE_MAX_FILE = 5;
    private static final AppManager INSTANCE = new AppManager();

    private final FileAttente fileAttente = new FileAttente(CAPACITE_MAX_FILE);
    private final Map<Guichet, Ticket> ticketsEnService = new LinkedHashMap<>();
    private final Map<Integer, Guichet> guichetsActifs = new LinkedHashMap<>();
    private final List<SystemStatusListener> listeners = new CopyOnWriteArrayList<>();

    private AppManager() {}

    public static AppManager getInstance() { return INSTANCE; }

    public void addListener(SystemStatusListener listener) {
        listeners.add(listener);
        listener.onSystemSnapshot(getTicketsEnAttente(), getTicketsEnService(), getTousLesGuichets());
    }

    public void removeListener(SystemStatusListener listener) {
        listeners.remove(listener);
    }

    public Ticket creerNouveauTicket() {
        if (fileAttente.estPleine()) {
            listeners.forEach(SystemStatusListener::onQueueFull);
            return null;
        }
        Ticket ticket = new Ticket();
        fileAttente.ajouterTicket(ticket);
        notifierFile();
        return ticket;
    }

    public Ticket appelerProchainClient(Guichet guichet) {
        if (!estActif(guichet) || !"LIBRE".equals(guichet.getEtat())) {
            return null;
        }
        Ticket ticket = fileAttente.retirerProchainTicket();
        if (ticket == null) {
            return null;
        }
        guichet.setTicketEnCours(ticket);
        guichet.setEtat("OCCUPE");
        guichet.setTempsDebutService(Instant.now());
        ticketsEnService.put(guichet, ticket);
        notifierFile();
        notifierGuichet(guichet);
        listeners.forEach(listener -> listener.onTicketCalled(guichet, ticket));
        return ticket;
    }

    public void terminerService(Guichet guichet) {
        Ticket ticket = guichet.getTicketEnCours();
        if (estActif(guichet) && ticket != null) {
            libererGuichet(guichet, "LIBRE");
            notifierGuichet(guichet);
            listeners.forEach(listener -> listener.onTicketServed(guichet, ticket));
        }
    }

    public void setGuichetEnPause(Guichet guichet, boolean enPause) {
        String etatAttendu = enPause ? "LIBRE" : "EN_PAUSE";
        if (estActif(guichet) && etatAttendu.equals(guichet.getEtat())) {
            guichet.setEtat(enPause ? "EN_PAUSE" : "LIBRE");
            guichet.setTempsDebutPause(enPause ? Instant.now() : null);
            notifierGuichet(guichet);
        }
    }

    public boolean rappelerClient(Guichet guichet) {
        Ticket ticket = guichet.getTicketEnCours();
        if (!estActif(guichet) || ticket == null || !fileAttente.ajouterTicket(ticket)) {
            return false;
        }
        libererGuichet(guichet, "LIBRE");
        notifierFile();
        notifierGuichet(guichet);
        return true;
    }

    public void clientLeftQueue(Ticket ticket) {
        if (fileAttente.removeTicket(ticket)) {
            notifierFile();
        }
    }

    public Guichet creerGuichet(String nom) {
        Guichet guichet = new Guichet(nom);
        guichetsActifs.put(guichet.getId(), guichet);
        listeners.forEach(listener -> listener.onNewGuichetOpened(guichet));
        return guichet;
    }

    public boolean handleGuichetClosing(Guichet guichet) {
        if (!estActif(guichet)) {
            return true;
        }
        Ticket ticket = guichet.getTicketEnCours();
        if (ticket != null && !fileAttente.ajouterTicket(ticket)) {
            return false;
        }
        guichetsActifs.remove(guichet.getId());
        libererGuichet(guichet, "FERME");
        notifierGuichet(guichet);
        if (ticket != null) {
            notifierFile();
        }
        return true;
    }

    private boolean estActif(Guichet guichet) {
        return guichetsActifs.get(guichet.getId()) == guichet;
    }

    private void libererGuichet(Guichet guichet, String etat) {
        guichet.setTicketEnCours(null);
        ticketsEnService.remove(guichet);
        guichet.setEtat(etat);
        guichet.setTempsDebutService(null);
        guichet.setTempsDebutPause(null);
    }

    private void notifierGuichet(Guichet guichet) {
        listeners.forEach(listener -> listener.onGuichetStateUpdated(guichet));
    }

    private void notifierFile() {
        listeners.forEach(listener -> listener.onTicketQueueUpdated(getTicketsEnAttente()));
    }

    public Map<Integer, Guichet> getTousLesGuichets() { return new LinkedHashMap<>(guichetsActifs); }
    public Queue<Ticket> getTicketsEnAttente() { return fileAttente.getTicketsEnAttente(); }
    public Map<Guichet, Ticket> getTicketsEnService() { return new LinkedHashMap<>(ticketsEnService); }
    public int getCapaciteMaximaleFile() { return CAPACITE_MAX_FILE; }
    public int getTailleFileActuelle() { return fileAttente.getTailleActuelle(); }
}
