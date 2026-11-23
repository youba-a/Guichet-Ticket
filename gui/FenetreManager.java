package gui;

import business.AppManager;
import business.ClientController;
import data.Ticket;
import java.util.HashMap;
import java.util.Map;

public class FenetreManager {
    private static final Map<Integer, ClientWindow> clientWindows = new HashMap<>();

    public static void ouvrirClientWindow(AppManager appManager, Ticket ticket) {
        ClientController controller = new ClientController(appManager, ticket);
        ClientWindow window = new ClientWindow(controller, ticket);
        clientWindows.put(ticket.getNumero(), window);
        window.setVisible(true);
    }

    public static void retirerClientWindow(Ticket ticket) {
        ClientWindow window = clientWindows.remove(ticket.getNumero());
        if (window != null) {
            window.dispose();
        }
    }
}
