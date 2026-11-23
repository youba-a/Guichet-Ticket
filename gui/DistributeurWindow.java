package gui;

import business.DistributeurController;
import java.awt.*;
import javax.swing.*;

public class DistributeurWindow extends JFrame {
    public enum MessageType { INFO, WARNING, ERROR }

    public DistributeurWindow(DistributeurController controller) {
        setTitle("Distributeur de Tickets");
        setBounds(20, 0, 250, 130);
        setLocation(this.getX() + 400, this.getY() + 0);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new FlowLayout(FlowLayout.CENTER,20,15));

        JButton btnPrendreTicket = new JButton("Prendre un Ticket");
        btnPrendreTicket.setFont(new Font("Arial", Font.BOLD, 20));
        btnPrendreTicket.setPreferredSize(new Dimension(220, 70));
        btnPrendreTicket.setBackground(new Color(46, 204, 113));
        btnPrendreTicket.setForeground(Color.WHITE);
        btnPrendreTicket.setFocusPainted(false);

        btnPrendreTicket.addActionListener(e -> {
            controller.prendreTicket();
        });

        add(btnPrendreTicket);
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
}
