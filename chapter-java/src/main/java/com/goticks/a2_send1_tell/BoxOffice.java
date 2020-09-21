package com.goticks.a2_send1_tell;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// メッセージプロトコルの定義
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
     * 注文メッセージ
     */
    public static class Order {
        public Order() {
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

    ActorRef ticketSeller = getContext().actorOf(TicketSeller.props(), "ticketSeller");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // 初期化メッセージを受信
                .match(Initialize.class, initialize -> log.info("starting go ticks"))
                // 注文メッセージを受信
                .match(Order.class, order -> {
                    ticketSeller.tell(new TicketSeller.Order("RHCP", 2), getSelf());
                })
                // シャットダウンメッセージを受信
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating go ticks");
                    getContext().getSystem().terminate();
                })
                // 注文完了メッセージを受信
                .match(OrderCompleted.class, result -> log.info("result: {}", result.getMessage()))
                .build();
    }
}
