package gui;

import business.AppManager;
import data.Guichet;
import data.SystemStatusListener;
import data.Ticket;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Queue;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Afficheur extends JFrame implements SystemStatusListener {
    private final AppManager appManager;
    private final JTextArea displayArea;
    private final JLabel timeLabel;
    private final Timer guiRefreshTimer;

    public Afficheur(AppManager appManager) {
        this.appManager = appManager;
        setTitle("Tableau d'Affichage Central");
        setSize(500, 400);
        setLocation(1000, 50);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        timeLabel = new JLabel("Chargement de l'heure...", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        add(timeLabel, BorderLayout.NORTH);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        displayArea.setBackground(new Color(245, 245, 255));
        displayArea.setBorder(new TitledBorder("État du Système"));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        appManager.addListener(this);

        DateTimeFormatter formatHeure = DateTimeFormatter.ofPattern("HH:mm:ss");
        guiRefreshTimer = new Timer(1000, e -> timeLabel.setText(LocalTime.now().format(formatHeure)));
        guiRefreshTimer.setInitialDelay(0);
        guiRefreshTimer.start();

        setVisible(true);
    }

    private void updateDisplayContent(Queue<Ticket> currentQueue, Map<Integer, Guichet> activeGuichets) {
        StringBuilder sb = new StringBuilder();

        sb.append("--- Guichets ---\n");
        if (activeGuichets.isEmpty()) {
            sb.append("   Aucun guichet ouvert.\n");
        } else {
            for (Guichet guichet : activeGuichets.values()) {
                sb.append(String.format("   Guichet %d (%s): ", guichet.getId(), guichet.getNom()));
                String etat = guichet.getEtat();
                Ticket servedTicket = guichet.getTicketEnCours();

                if (etat.equals("EN_PAUSE")) {
                    sb.append("EN PAUSE");
                } else if (servedTicket != null) {
                    sb.append("sert ").append(servedTicket.toString());
                } else {
                    sb.append("LIBRE");
                }
                sb.append("\n");
            }
        }

        sb.append("\n--- File d'Attente ---\n");
        if (currentQueue.isEmpty()) {
            sb.append("   File vide.\n");
        } else {
            currentQueue.forEach(ticket -> sb.append("   ").append(ticket.toString()).append("\n"));
        }

        displayArea.setText(sb.toString());
    }

    @Override
    public void onTicketQueueUpdated(Queue<Ticket> currentQueue) {
        updateDisplayContent(currentQueue, appManager.getTousLesGuichets());
    }

    @Override
    public void onGuichetStateUpdated(Guichet updatedGuichet) {
        updateDisplayContent(appManager.getTicketsEnAttente(), appManager.getTousLesGuichets());
    }

    @Override
    public void onNewGuichetOpened(Guichet newGuichet) {
        updateDisplayContent(appManager.getTicketsEnAttente(), appManager.getTousLesGuichets());
    }

    @Override
    public void onSystemSnapshot(Queue<Ticket> currentQueue, Map<Guichet, Ticket> ticketsInService, Map<Integer, Guichet> activeGuichets) {
        updateDisplayContent(currentQueue, activeGuichets);
    }

    @Override
    public void dispose() {
        if (guiRefreshTimer != null) {
            guiRefreshTimer.stop();
        }
        appManager.removeListener(this);
        super.dispose();
    }
}
