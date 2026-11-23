package business;

import data.Ticket;
import gui.DistributeurWindow;
import gui.FenetreManager;

public class DistributeurController {
    private final AppManager appManager;
    private DistributeurWindow view;

    public DistributeurController(AppManager appManager) {
        this.appManager = appManager;
    }

    public void setView(DistributeurWindow view) {
        this.view = view;
    }

    public void prendreTicket() {
        Ticket ticket = appManager.creerNouveauTicket();
        if (ticket == null) {
            if (view != null) {
                view.showMessage("La file d'attente est complète. Aucun nouveau ticket ne peut être délivré.",
                        "File complète", DistributeurWindow.MessageType.WARNING);
            }
            return;
        }

        FenetreManager.ouvrirClientWindow(appManager, ticket);
    }
}
