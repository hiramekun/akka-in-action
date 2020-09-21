package com.goticks.a1_create;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

// ReceiveBuilder の分割
public class TicketSeller2 extends AbstractActor {
    static public Props props() {
        return Props.create(TicketSeller2.class, () -> new TicketSeller2());
    }

    /**
     * 注文メッセージ
     */
    public static class Order {
        public Order() {
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public TicketSeller2() {
    }

    @Override
    public Receive createReceive() {
        ReceiveBuilder builder = ReceiveBuilder.create();

        // String 型の場合
        builder.match(String.class, msg -> {
            log.info("received String message: {}", msg);
        });

        // Integer 型の場合
        builder.match(Integer.class, msg -> {
            log.info("received Integer message: {}", msg);
        });

        // それ以外の場合
        builder.matchAny(msg -> log.info("received unknown message"));

        // build()メソッドによりReceive 型を返す
        return builder.build();
    }
}
