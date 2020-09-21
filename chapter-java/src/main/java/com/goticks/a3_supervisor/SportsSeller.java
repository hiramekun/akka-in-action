package com.goticks.a3_supervisor;


import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * スポーツチケット担当
 */
class SportsSeller extends AbstractActor {
    static public Props props(int offset) {
        return Props.create(SportsSeller.class, () -> new SportsSeller(offset));
    }

    /**
     * チケットのリクエスト・メッセージ
     */
    public static class RequestTicket {
        private final int nrTickets;

        public RequestTicket(int nrTickets) {
            this.nrTickets = nrTickets;
        }

        public int getNrTickets() {
            return nrTickets;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    /**
     * チケット残数
     */
    private int rest;

    public SportsSeller(int offset) {
        this.rest = offset;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RequestTicket.class, order -> {
                    rest -= order.getNrTickets();  // 受信した注文数をマイナス
                    log.info("order:{}, rest:{}", order.getNrTickets(), rest);
                    getSender().tell(new BoxOffice.OrderCompleted(
                            "I'm a charge of Sports events. received your order!"), getSelf());
                })
                .build();
    }
}
