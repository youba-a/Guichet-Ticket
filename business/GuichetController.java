package business;

import data.Guichet;
import data.SystemStatusListener;
import data.Ticket;
import gui.FenetreManager;
import gui.GuichetWindow;

public class GuichetController implements SystemStatusListener {
    private final AppManager appManager;
    private final Guichet guichet;
    private GuichetWindow view;

    public GuichetController(AppManager appManager, Guichet guichet) {
        this.appManager = appManager;
        this.guichet = guichet;
        appManager.addListener(this);
    }

    public void setView(GuichetWindow view) {
        this.view = view;
    }

    public void appelerProchainClient() {
        Ticket ticket = appManager.appelerProchainClient(guichet);
        if (ticket != null) {
            FenetreManager.retirerClientWindow(ticket);
        } else if (view != null && "LIBRE".equals(guichet.getEtat())) {
            avertir("La file d'attente est vide.", "Appel client");
        }
    }

    public void terminerService() {
        appManager.terminerService(guichet);
    }

    public void rappelerClient() {
        Ticket ticket = guichet.getTicketEnCours();
        if (appManager.rappelerClient(guichet)) {
            FenetreManager.ouvrirClientWindow(appManager, ticket);
        } else if (view != null && ticket != null) {
            avertir("La file est pleine. Le ticket reste en service.", "Rappel client");
        }
    }

    public void togglePause(boolean enPause) {
        if (enPause && guichet.getTicketEnCours() != null) {
            if (view != null) {
                avertir("Un client est en cours de service.", "Pause indisponible");
            }
            return;
        }
        appManager.setGuichetEnPause(guichet, enPause);
    }

    public boolean handleGuichetWindowClosing() {
        Ticket ticket = guichet.getTicketEnCours();
        if (!appManager.handleGuichetClosing(guichet)) {
            if (view != null) {
                avertir("La file est pleine. Libérez une place avant de fermer ce guichet.",
                        "Fermeture indisponible");
            }
            return false;
        }
        if (ticket != null) {
            FenetreManager.ouvrirClientWindow(appManager, ticket);
        }
        return true;
    }

    public void detachView() {
        appManager.removeListener(this);
        view = null;
    }

    private void avertir(String message, String titre) {
        view.showMessage(message, titre, GuichetWindow.MessageType.WARNING);
    }

    @Override
    public void onGuichetStateUpdated(Guichet updatedGuichet) {
        if (guichet == updatedGuichet && view != null) {
            view.updateDisplay();
        }
    }
}
