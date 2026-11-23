package gui;

import business.GuichetController;
import data.Guichet;
import data.Ticket;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Duration;
import java.time.Instant;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class GuichetWindow extends JFrame {
    private final GuichetController controller;
    private final Guichet guichet;

    private final JLabel lblTicket = new JLabel("Aucun client");
    private final JLabel lblChrono = new JLabel("Temps de service : 00:00:00");
    private final JButton btnAppel = new JButton("Appeler Suivant");
    private final JButton btnPause = new JButton("Mettre en pause");
    private final JButton btnFin = new JButton("Terminer Service");
    private final JButton btnRappeler = new JButton("Passer le client");

    private Timer chronoUpdateTimer;

    private static final Color BTN_GREEN = new Color(46, 204, 113);
    private static final Color BTN_RED = new Color(231, 76, 60);
    private static final Color BTN_BLUE = new Color(52, 152, 219);
    private static final Color BTN_YELLOW = new Color(241, 196, 15);
    private static final Color BTN_DISABLED_GREY = new Color(189, 195, 199);
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final Color TEXT_BLACK = Color.BLACK;
    private static final Color TEXT_DARK_GREY = Color.DARK_GRAY;

    public enum MessageType { INFO, WARNING, ERROR }

    public GuichetWindow(GuichetController controller, Guichet guichet) {
        this.controller = controller;
        this.guichet = guichet;

        setTitle("Guichet " + guichet.getNom() + " (#" + guichet.getId() + ")");
        setSize(350, 300);
        setLocation(20, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (controller.handleGuichetWindowClosing()) {
                    dispose();
                }
            }
        });

        setLayout(new BorderLayout(8, 8));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        infoPanel.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        infoPanel.setOpaque(false);

        lblTicket.setFont(new Font("Arial", Font.BOLD, 22));
        lblTicket.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(lblTicket);

        lblChrono.setFont(new Font("Arial", Font.PLAIN, 14));
        lblChrono.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(lblChrono);

        add(infoPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        buttonPanel.setOpaque(false);

        Font buttonFont = new Font("Arial", Font.BOLD, 12);

        btnAppel.setFont(buttonFont);
        btnAppel.addActionListener(e -> controller.appelerProchainClient());
        buttonPanel.add(btnAppel);

        btnFin.setFont(buttonFont);
        btnFin.addActionListener(e -> controller.terminerService());
        buttonPanel.add(btnFin);

        btnRappeler.setFont(buttonFont);
        btnRappeler.addActionListener(e -> controller.rappelerClient());
        buttonPanel.add(btnRappeler);

        btnPause.setFont(buttonFont);
        btnPause.addActionListener(e -> {
            boolean currentPauseState = guichet.getEtat().equals("EN_PAUSE");
            controller.togglePause(!currentPauseState);
        });
        buttonPanel.add(btnPause);
        add(buttonPanel, BorderLayout.CENTER);

        chronoUpdateTimer = new Timer(1000, e -> updateChronoLabel());
        chronoUpdateTimer.start();

        updateDisplay();
    }

    /**
     * Cette méthode ne met à jour QUE le label du chronomètre.
     * C'est ce qui sera appelé par le Swing Timer.
     */
    private void updateChronoLabel() {
        String chronoText = "Temps de service : 00:00:00";
        Instant startTime = null;

        String guichetEtat = guichet.getEtat();

        if (guichetEtat.equals("OCCUPE") && guichet.getTempsDebutService() != null) {
            startTime = guichet.getTempsDebutService();
            long seconds = Duration.between(startTime, Instant.now()).getSeconds();
            chronoText = String.format("Temps de service : %02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
        } else if (guichetEtat.equals("EN_PAUSE") && guichet.getTempsDebutPause() != null) {
            startTime = guichet.getTempsDebutPause();
            long seconds = Duration.between(startTime, Instant.now()).getSeconds();
            chronoText = String.format("Temps de pause : %02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, seconds % 60);
        } else {

            chronoText = "Temps de service : 00:00:00";
        }
        lblChrono.setText(chronoText);
    }

    /**
     * Met à jour tout l'affichage de la fenêtre, y compris le chrono
     * et l'état des boutons. Appelé lors des changements d'état du guichet.
     */
    public void updateDisplay() {
        Ticket currentTicket = guichet.getTicketEnCours();
        String guichetEtat = guichet.getEtat();

        getContentPane().setBackground(getGuichetBackgroundColor(guichetEtat));

        if (guichetEtat.equals("OCCUPE")) {
            lblTicket.setText("Ticket : " + currentTicket.toString());
            lblTicket.setForeground(new Color(41, 128, 185));
        } else if (guichetEtat.equals("EN_PAUSE")) {
            lblTicket.setText("Guichet en PAUSE");
            lblTicket.setForeground(new Color(200, 100, 0));
        } else {
            lblTicket.setText("Aucun client");
            lblTicket.setForeground(new Color(41, 128, 185));
        }
        lblChrono.setForeground(new Color(52, 73, 94));

        updateChronoLabel();

        boolean isGuichetOccupied = currentTicket != null;
        boolean isGuichetPaused = guichetEtat.equals("EN_PAUSE");

        btnAppel.setEnabled("LIBRE".equals(guichetEtat));
        applyButtonStyles(btnAppel, btnAppel.isEnabled(), BTN_GREEN, TEXT_WHITE);

        btnFin.setEnabled(isGuichetOccupied && !isGuichetPaused);
        applyButtonStyles(btnFin, btnFin.isEnabled(), BTN_RED, TEXT_WHITE);

        btnRappeler.setEnabled(isGuichetOccupied && !isGuichetPaused);
        applyButtonStyles(btnRappeler, btnRappeler.isEnabled(), BTN_BLUE, TEXT_WHITE);

        btnPause.setEnabled(true);
        if (isGuichetPaused) {
            btnPause.setText("Reprendre service");
            applyButtonStyles(btnPause, btnPause.isEnabled(), new Color(255, 120, 120), TEXT_WHITE);
        } else {
            btnPause.setText("Mettre en pause");
            applyButtonStyles(btnPause, btnPause.isEnabled(), BTN_YELLOW, TEXT_BLACK);
        }
    }

    private void applyButtonStyles(JButton button, boolean enabled, Color activeBg, Color activeFg) {
        if (!enabled) {
            button.setBackground(BTN_DISABLED_GREY);
            button.setForeground(TEXT_DARK_GREY);
        } else {
            button.setBackground(activeBg);
            button.setForeground(activeFg);
        }
    }

    private Color getGuichetBackgroundColor(String status) {
        return switch (status) {
            case "LIBRE" -> new Color(236, 252, 236);
            case "OCCUPE" -> new Color(252, 236, 236);
            case "EN_PAUSE" -> new Color(248, 248, 248);
            default -> Color.WHITE;
        };
    }

    public void showMessage(String message, String title, MessageType type) {
        int messageType;
        switch (type) {
            case INFO: messageType = JOptionPane.INFORMATION_MESSAGE; break;
            case WARNING: messageType = JOptionPane.WARNING_MESSAGE; break;
            case ERROR: messageType = JOptionPane.ERROR_MESSAGE; break;
            default: messageType = JOptionPane.PLAIN_MESSAGE; break;
        }
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    @Override
    public void dispose() {

        if (chronoUpdateTimer != null) {
            chronoUpdateTimer.stop();
        }
        controller.detachView();
        super.dispose();
    }
}
