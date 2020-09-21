package com.goticks.a1_create;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// メッセージを受信したときの振る舞いを定義
public class TicketSeller1 extends AbstractActor {
    static public Props props() {
        return Props.create(TicketSeller1.class, () -> new TicketSeller1());
    }

    /**
     * 注文メッセージ
     */
    public static class Order {
        public Order() {
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public TicketSeller1() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // String 型のメッセージを受信した場合
                .match(String.class, msg -> log.info("received String message: {}", msg))
                // Int 型のメッセージを受信した場合
                .match(Integer.class, msg -> log.info("received Integer message: {}", msg))
                // String 型、Integer 型以外のメッセージを受信した場合
                .matchAny(msg -> log.info("received unknown message."))
                .build();
    }
}
