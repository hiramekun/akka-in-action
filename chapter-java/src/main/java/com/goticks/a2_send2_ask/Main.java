package com.goticks.a2_send2_ask;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.goticks.a2_send2_ask.BoxOffice.Initialize;
import com.goticks.a2_send2_ask.BoxOffice.Order;
import com.goticks.a2_send2_ask.BoxOffice.Orders;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

class Main {
    public static void main(String args[]) throws Exception {

        final ActorSystem system = ActorSystem.create("main", ConfigFactory.load("goticks"));
        final ActorRef boxOffice = system.actorOf(BoxOffice.props(), "boxOffice");

        boxOffice.tell(new Initialize(), ActorRef.noSender());
        boxOffice.tell(new Order(), ActorRef.noSender());
        boxOffice.tell(new Orders(), ActorRef.noSender());

        // 20秒後にシャットダウン
        system.scheduler().scheduleOnce(Duration.create(5, "seconds"), boxOffice, new BoxOffice.Shutdown(), system.dispatcher(), null);
        Await.result(system.whenTerminated(), Duration.Inf());

    }
}
