package com.goticks.a1_create;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// 振る舞いを別メソッドに定義
public class TicketSeller3 extends AbstractActor {
    static public Props props() {
        return Props.create(TicketSeller3.class, () -> new TicketSeller3());
    }

    /**
     * 注文メッセージ
     */
    public static class Order {
        public Order() {
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public TicketSeller3() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::receiveString)
                .match(Integer.class, this::receiveInteger)
                .matchAny(this::receiveAny)
                .build();
    }

    /**
     * String 型の場合
     */
    private void receiveString(String msg) {
        log.info("received String message: {}", msg);
    }

    /**
     * Integer 型の場合
     */
    private void receiveInteger(Integer msg) {
        log.info("received Integer message: {}", msg);
    }

    /**
     * それ以外の場合
     */
    private void receiveAny(Object msg) {
        log.info("received unknown message");
    }
}
