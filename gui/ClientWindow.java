package gui;

import business.ClientController;
import data.Ticket;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Duration;
import java.time.Instant;
import javax.swing.*;

public class ClientWindow extends JFrame {
    private final JLabel lblTicketNumber = new JLabel();
    private final JLabel lblStatus = new JLabel("En attente...");
    private final JLabel lblWaitTime = new JLabel("Temps d'attente: 00:00:00");
    private final JButton btnLeaveQueue = new JButton("Je pars");

    private Timer waitTimeTimer;
    private final Instant creationTime;

    public ClientWindow(ClientController controller, Ticket ticket) {
        this.creationTime = ticket.getHeureCreation();

        setTitle("Mon Ticket - " + ticket.getNumero());
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.clientLeavesQueue();
            }
        });

        setLayout(new BorderLayout(10, 10));
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblTicketNumber.setText("Votre ticket : " + ticket.getNumero());
        lblTicketNumber.setFont(new Font("Arial", Font.BOLD, 28));
        lblTicketNumber.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblStatus.setFont(new Font("Arial", Font.PLAIN, 18));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatus.setForeground(new Color(46, 204, 113));

        lblWaitTime.setFont(new Font("Arial", Font.ITALIC, 14));
        lblWaitTime.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblWaitTime.setForeground(Color.GRAY);

        btnLeaveQueue.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLeaveQueue.setFont(new Font("Arial", Font.BOLD, 14));
        btnLeaveQueue.setBackground(new Color(231, 76, 60));
        btnLeaveQueue.setForeground(Color.WHITE);
        btnLeaveQueue.setFocusPainted(false);
        btnLeaveQueue.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLeaveQueue.addActionListener(e -> controller.clientLeavesQueue());

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(lblTicketNumber);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(lblStatus);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(lblWaitTime);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(btnLeaveQueue);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, BorderLayout.CENTER);

        startWaitTimer();
    }

    private void startWaitTimer() {
        waitTimeTimer = new Timer(1000, e -> {
            Duration duration = Duration.between(creationTime, Instant.now());
            long seconds = duration.getSeconds();
            long minutes = seconds / 60;
            long hours = minutes / 60;
            seconds %= 60;
            minutes %= 60;

            String timeString = String.format("Temps d'attente: %02d:%02d:%02d", hours, minutes, seconds);
            lblWaitTime.setText(timeString);
        });
        waitTimeTimer.start();
    }
    @Override
    public void dispose() {
        if (waitTimeTimer != null) {
            waitTimeTimer.stop();
        }
        super.dispose();
    }
}
