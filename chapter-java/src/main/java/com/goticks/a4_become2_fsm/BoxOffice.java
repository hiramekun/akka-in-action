package com.goticks.a4_become2_fsm;


import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class BoxOffice extends AbstractActor {
    static public Props props() {
        return Props.create(BoxOffice.class, () -> new BoxOffice());
    }

    /**
     * 初期化メッセージ
     */
    public static class Initialize {
        public Initialize() {
        }
    }

    /**
     * シャットダウンメッセージ
     */
    public static class Shutdown {
        public Shutdown() {
        }
    }

    /**
     * オープンメッセージ
     */
    public static class Open {
        public Open() {
        }
    }

    /**
     * クローズメッセージ
     */
    public static class Close {
        public Close() {
        }
    }

    /**
     * 注文メッセージ
     */
    public static class Order {
        private final String event;
        private final int nrTickets;

        public Order(String event, int nrTickets) {
            this.event = event;
            this.nrTickets = nrTickets;
        }

        public String getEvent() {
            return event;
        }

        public int getNrTickets() {
            return nrTickets;
        }
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

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public BoxOffice() {
    }

    private ActorRef ticketSeller = getContext().actorOf(TicketSeller.props(), "ticketSeller");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, initialize -> log.info("starting go ticks"))
                .match(Open.class, open -> {
                    ticketSeller.tell(new TicketSeller.Open(), getSelf());
                })
                .match(Close.class, close -> {
                    ticketSeller.tell(new TicketSeller.Close(), getSelf());
                })
                .match(Order.class, order -> {
                    ticketSeller.tell(new TicketSeller.Order(order.getEvent(), order.getNrTickets()), getSelf());
                })
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating go ticks");
                    getContext().getSystem().terminate();
                })
                .match(OrderCompleted.class, result -> log.info("result: {}", result.getMessage()))
                .build();
    }
}
