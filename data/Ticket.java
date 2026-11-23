package data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private static int nextTicketNumber = 1;
    private final int numero;
    private final Instant heureCreation;

    public Ticket() {
        this.numero = nextTicketNumber++;
        this.heureCreation = Instant.now();
    }

    public int getNumero() { return numero; }
    public Instant getHeureCreation() { return heureCreation; }

    @Override
    public String toString() {
        return "T" + String.format("%03d", numero) + " (" + DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault()).format(heureCreation) + ")";
    }
}
