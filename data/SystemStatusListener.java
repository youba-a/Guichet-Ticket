package data;

import java.util.Map;
import java.util.Queue;

public interface SystemStatusListener {
    default void onTicketQueueUpdated(Queue<Ticket> currentQueue) {}
    default void onGuichetStateUpdated(Guichet updatedGuichet) {}
    default void onNewGuichetOpened(Guichet newGuichet) {}
    default void onTicketServed(Guichet guichet, Ticket servedTicket) {}
    default void onTicketCalled(Guichet guichet, Ticket calledTicket) {}
    default void onSystemSnapshot(Queue<Ticket> currentQueue, Map<Guichet, Ticket> ticketsInService,
                                  Map<Integer, Guichet> activeGuichets) {}
    default void onQueueFull() {}
}
