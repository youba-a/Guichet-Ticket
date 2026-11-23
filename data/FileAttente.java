package data;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class FileAttente {
    private final ArrayBlockingQueue<Ticket> ticketsEnAttente;

    public FileAttente(int capaciteMaximale) {
        ticketsEnAttente = new ArrayBlockingQueue<>(capaciteMaximale);
    }

    public boolean ajouterTicket(Ticket ticket) { return ticketsEnAttente.offer(ticket); }
    public Ticket retirerProchainTicket() { return ticketsEnAttente.poll(); }
    public boolean estVide() { return ticketsEnAttente.isEmpty(); }
    public Queue<Ticket> getTicketsEnAttente() { return new ArrayDeque<>(ticketsEnAttente); }
    public boolean removeTicket(Ticket ticket) { return ticketsEnAttente.remove(ticket); }
    public int getTailleActuelle() { return ticketsEnAttente.size(); }
    public boolean estPleine() { return ticketsEnAttente.remainingCapacity() == 0; }
}
