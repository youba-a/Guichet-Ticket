package business;

import data.Ticket;
import gui.FenetreManager;

public class ClientController {
    private final AppManager appManager;
    private final Ticket ticket;

    public ClientController(AppManager appManager, Ticket ticket) {
        this.appManager = appManager;
        this.ticket = ticket;
    }

    public void clientLeavesQueue() {
        appManager.clientLeftQueue(ticket);
        FenetreManager.retirerClientWindow(ticket);
    }
}
