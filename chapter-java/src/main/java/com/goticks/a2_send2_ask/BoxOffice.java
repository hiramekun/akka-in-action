package com.goticks.a2_send2_ask;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.UnitMatch;
import akka.japi.pf.UnitPFBuilder;
import akka.pattern.AskTimeoutException;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static akka.pattern.PatternsCS.ask;

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
     * 複数注文メッセージ
     */
    public static class Orders {
        public Orders() {
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

    /**
     * 複数注文の完了メッセージ
     */
    public static class Result {
        private final OrderCompleted sports;
        private final OrderCompleted music;

        public Result(OrderCompleted sports, OrderCompleted music) {

            this.sports = sports;
            this.music = music;
        }

        public String getResult() {
            return sports.getMessage() + ":" + music.getMessage();
        }
    }

    ActorRef ticketSeller = getContext().actorOf(TicketSeller.props(), "ticketSeller");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, initialize -> log.info("starting go ticks"))
                .match(Order.class, order -> {
                    // タイムアウトの設定
                    Timeout timeout = new Timeout(Duration.create(2, TimeUnit.SECONDS));
                    // askでメッセージを送信
                    CompletionStage<Object> orderCompleted =
                            ask(ticketSeller, new TicketSeller.Order("RHCP", 2), timeout);
                    // UnKnown Messageを送信（タイムアウト発生）
//                    CompletionStage<Object> orderCompleted =
//                            ask(ticketSeller, "Unknown Message", timeout);
                    orderCompleted
                            .thenAccept(msg -> {
                                OrderCompleted oc = (OrderCompleted) msg;
                                log.info("result1: {}", oc.getMessage());
                            })
                            .exceptionally(ex -> {
                                UnitPFBuilder builder = UnitMatch
                                        .match(AskTimeoutException.class, e -> log.warning(e.getMessage()))
                                        .match(Throwable.class, t -> log.error(t, "予期せぬ例外が発生しました:"));
                                UnitMatch.create(builder).match(ex.getCause());
                                return null;
                            });

                })
                .match(Orders.class, order -> {
                    // タイムアウトの設定
                    Timeout timeout = new Timeout(Duration.create(2, TimeUnit.SECONDS));
                    // askでメッセージを送信
                    CompletionStage<Object> result =
                            ask(ticketSeller, new TicketSeller.RequestMultiTickets(3, 4), timeout);
                    result.thenAccept(msg -> {
                        Result r = (Result) msg;
                        log.info("result: {}", r.getResult());
                    }).exceptionally(ex -> {
                        UnitPFBuilder builder = UnitMatch
                                .match(AskTimeoutException.class, e -> log.warning(e.getMessage()))
                                .match(Throwable.class, t -> log.error(t, "予期せぬ例外が発生しました:"));
                        UnitMatch.create(builder).match(ex.getCause());
                        return null;
                    });

                }).match(Shutdown.class, shutdown -> {
                    log.info("terminating go ticks");
                    getContext().getSystem().terminate();
                })
                .build();

    }
}

