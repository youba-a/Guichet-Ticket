package data;

import java.time.Instant;

public class Guichet {
    private static int nextId = 1;
    private final int id;
    private final String nom;
    private String etat;
    private Ticket ticketEnCours;
    private Instant tempsDebutService;
    private Instant tempsDebutPause;

    public Guichet(String nom) {
        this.id = nextId++;
        this.nom = nom;
        this.etat = "LIBRE";
        this.ticketEnCours = null;
        this.tempsDebutService = null;
        this.tempsDebutPause = null;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public Ticket getTicketEnCours() { return ticketEnCours; }
    public void setTicketEnCours(Ticket ticketEnCours) { this.ticketEnCours = ticketEnCours; }

    public Instant getTempsDebutService() { return tempsDebutService; }
    public void setTempsDebutService(Instant tempsDebutService) { this.tempsDebutService = tempsDebutService; }
    public Instant getTempsDebutPause() { return tempsDebutPause; }
    public void setTempsDebutPause(Instant tempsDebutPause) { this.tempsDebutPause = tempsDebutPause; }

    @Override
    public String toString() {
        return "Guichet " + nom + " (#" + id + ")";
    }
}
