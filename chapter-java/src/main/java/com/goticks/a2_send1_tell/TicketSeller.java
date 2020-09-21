package com.goticks.a2_send1_tell;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// チケット販売員
class TicketSeller extends AbstractActor {
    static public Props props() {
        return Props.create(TicketSeller.class, () -> new TicketSeller());
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

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public TicketSeller() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Order.class, order -> {
                    // 注文を受けたときの振る舞い
                    log.info("your order has been completed. (product: {}, nrTickets: {})", order.getEvent(), order.getNrTickets());
                    // 送信元に注文処理が完了したことを返す
                    getSender().tell(new BoxOffice.OrderCompleted("OK"), getSender());
                })
                .matchAny(c -> log.info("received unknown message."))  // 想定外のメッセージを受信した場合
                .build();
    }
}
