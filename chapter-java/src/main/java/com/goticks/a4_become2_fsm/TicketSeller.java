package com.goticks.a4_become2_fsm;


import akka.actor.AbstractFSMWithStash;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import static com.goticks.a4_become2_fsm.State.*;


/**
 * 状態
 */
enum State {
    Close, Open, Break
}

/**
 * データ
 */
interface Data {
}

final class StateData implements Data {
    /**
     * チケット残数
     */
    private final int rest;

    public StateData(int rest) {
        this.rest = rest;
    }

    public int getRest() {
        return rest;
    }
}

/**
 * チケット販売員
 */
public class TicketSeller extends AbstractFSMWithStash<State, Data> {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    {
        // オープン状態でスタート
        startWith(Open, new StateData(10));

        // オープン状態のときの振る舞い
        when(Open,
                matchEvent(Order.class, StateData.class,
                        (order, state) -> {
                            final int rest = state.getRest() - order.getNrTickets();
                            log.info("order: {}/{}, rest: {}",
                                    order.getEvent(), order.getNrTickets(), rest);
                            if (rest >= 0) {
                                getSender().tell(new BoxOffice.OrderCompleted("received your order."),
                                        getSelf());
                                // 状態はそのまま、残数更新
                                return stay().using(new StateData(rest));
                            } else {
                                getSender().tell(new BoxOffice.OrderCompleted("no tickets."),
                                        getSelf());
                                // 状態はそのまま、残数そのまま
                                return stay();
                            }
                        }
                ).event(Close.class, StateData.class, (breaking, state) -> {
                            if (state.getRest() <= 0) return goTo(Close);
                            else return goTo(Break);
                        }
                )
        );

        // 中断状態のときの振る舞い
        when(Break,
                matchEvent(Order.class, StateData.class,
                        (order, state) -> {
                            log.info("I'm breaking.");
                            // 受信したメッセージを蓄えておく
                            stash();
                            return stay();
                        }).event(Open.class, StateData.class, (order, state) -> goTo(Open)));

        // クローズ状態のときの振る舞い
        when(Close,
                matchEvent(Order.class, StateData.class,
                        (order, state) -> {
                            log.info("I'm closed.");
                            return stay();
                        }));

        // 想定外のメッセージが届いたときの振る舞い
        whenUnhandled(
                matchEvent(Order.class, StateData.class,
                        (order, state) -> {
                            // 受信したメッセージを蓄えておく
                            stash();
                            log.info("unhandled order: [{}, {}]", order, state);
                            return stay();
                        }).
                        anyEvent((event, state) -> {
                            log.info("receive unhandled message.");
                            return stay();
                        }));

        // 状態を遷移するときの振る舞い
        onTransition(
                matchState(Open, Break, () ->
                        log.info("status: Open -> Break")
                ).state(Break, Open, () -> {
                    log.info("status: Open -> Break");
                    // 蓄えたメッセージを開放
                    unstashAll();
                }).state(Open, Close, () ->
                        log.info("status: Open -> Close")
                ));

        initialize();
    }

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

    /**
     * オープンメッセージ
     */
    public static class Open {
        public Open() {
        }
    }

    /**
     * 中断メッセージ
     */
    public static class Break {
        public Break() {
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
     * イベントの種類
     */
    public static class EventType {
        private final String name;

        public EventType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public TicketSeller() {
    }
}
