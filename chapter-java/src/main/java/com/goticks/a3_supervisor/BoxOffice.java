package com.goticks.a3_supervisor;


import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class BoxOffice extends AbstractActor {
    static public Props props(ActorRef boxOffice) {
        return Props.create(BoxOffice.class, () -> new BoxOffice(boxOffice));
    }

    /**
     * 注文完了メッセージ
     */
    public static class OrderCompleted {
        private final String message;

        public OrderCompleted(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * 注文
     */
    public interface Order {
    }

    /**
     * スポーツチケットの注文メッセージ
     */
    public static class OrderSports implements Order {
        private final int nrTickets;

        public OrderSports(int nrTickets) {
            this.nrTickets = nrTickets;
        }

        public int getNrTickets() {
            return nrTickets;
        }
    }

    /**
     * 音楽チケットの注文メッセージ
     */
    public static class OrderMusic implements Order {
        private final int nrTickets;

        public OrderMusic(int nrTickets) {
            this.nrTickets = nrTickets;
        }

        public int getNrTickets() {
            return nrTickets;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private static ActorRef ticketSeller;

    public BoxOffice(ActorRef ticketSeller) {
        this.ticketSeller = ticketSeller;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderSports.class, order -> {
                    ticketSeller.tell(new TicketSeller.RequestSportsTicket(order.getNrTickets()), getSelf());
                })
                .match(OrderMusic.class, order -> {
                    ticketSeller.tell(new TicketSeller.RequestMusicTicket(order.getNrTickets()), getSelf());
                })
                .match(OrderCompleted.class, result -> log.info("result: {}", result.getMessage()))
                .matchEquals("killSports", msg -> ticketSeller.tell("killSports", getSelf()))
                .build();
    }
}
