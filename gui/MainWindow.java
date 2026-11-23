package gui;

import business.AppManager;
import business.DistributeurController;
import business.GuichetController;
import data.Guichet;
import java.awt.*;
import javax.swing.*;

public class MainWindow extends JFrame {
    private final AppManager appManager;

    public MainWindow() {
        this.appManager = AppManager.getInstance();

        setTitle("Gestionnaire de Tickets et Guichets");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new GridLayout(3, 1, 10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton btnDistributeur = new JButton("Ouvrir un Distributeur");
        btnDistributeur.setBackground(new Color(60, 179, 113));
        btnDistributeur.setForeground(Color.WHITE);
        btnDistributeur.setFont(new Font("Arial", Font.BOLD, 16));
        btnDistributeur.addActionListener(e -> {
            DistributeurController dController = new DistributeurController(appManager);
            DistributeurWindow dWindow = new DistributeurWindow(dController);
            dController.setView(dWindow);
            dWindow.setVisible(true);
        });
        add(btnDistributeur);

        JButton btnGuichet = new JButton("Ouvrir Nouveau Guichet");
        btnGuichet.setFont(new Font("Arial", Font.BOLD, 16));
        btnGuichet.setBackground(new Color(70, 130, 180));
        btnGuichet.setForeground(Color.WHITE);
        btnGuichet.addActionListener(e -> {
            String guichetName = JOptionPane.showInputDialog(this, "Nom du nouveau guichet:", "Nouveau Guichet", JOptionPane.PLAIN_MESSAGE);
            if (guichetName == null) {
                return;
            }
            if (!guichetName.isBlank()) {
                Guichet newGuichet = appManager.creerGuichet(guichetName.strip());

                GuichetController gController = new GuichetController(appManager, newGuichet);
                GuichetWindow gWindow = new GuichetWindow(gController, newGuichet);
                gController.setView(gWindow);
                gWindow.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this, "Le nom du guichet ne peut pas être vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(btnGuichet);

        JButton btnAfficheur = new JButton("Ouvrir Tableau d'Affichage");
        btnAfficheur.setFont(new Font("Arial", Font.BOLD, 16));
        btnAfficheur.setBackground(new Color(105, 105, 105));
        btnAfficheur.setForeground(Color.WHITE);
        btnAfficheur.addActionListener(e -> {
            new Afficheur(appManager);
        });
        add(btnAfficheur);

        setVisible(true);
    }

}
