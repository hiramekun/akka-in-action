package com.goticks.a1_create;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.goticks.a1_create.BoxOffice.Initialize;
import com.goticks.a1_create.BoxOffice.Order;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

// アクターの生成
class Main {
    public static void main(String args[]) throws Exception {

        final ActorSystem system = ActorSystem.create("main", ConfigFactory.load("goticks"));
        final ActorRef boxOffice = system.actorOf(BoxOffice.props(), "boxOffice");

        boxOffice.tell(new Initialize(), ActorRef.noSender());
        boxOffice.tell(new Order(), ActorRef.noSender());

        // 2秒後にシャットダウン
        system.scheduler().scheduleOnce(Duration.create(2, "seconds"), boxOffice, new BoxOffice.Shutdown(), system.dispatcher(), ActorRef.noSender());
        Await.result(system.whenTerminated(), Duration.Inf());

    }
}
