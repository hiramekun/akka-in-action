package com.goticks.a2_send2_ask;


import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 音楽チケット担当
 */
class MusicSeller extends AbstractActor {
    static public Props props(int offset) {
        return Props.create(MusicSeller.class, () -> new MusicSeller(offset));
    }

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

    public MusicSeller(int offset) {
        this.rest = offset;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RequestTicket.class, order -> {
                    rest -= order.getNrTickets();  // 受信した注文数をマイナス
                    log.info("order:{}, rest:{}", order.getNrTickets(), rest);
                    getSender().tell(new TicketSeller.OrderCompleted(
                            "I'm a charge of Music events. received your order!"), getSelf());
                })
                .build();
    }
}
