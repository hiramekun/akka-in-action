package com.goticks.a4_become1;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

class Main {
    public static void main(String args[]) throws Exception {

        final ActorSystem system = ActorSystem.create("main", ConfigFactory.load("goticks"));
        final ActorRef boxOffice = system.actorOf(BoxOffice.props(), "boxOffice");

        boxOffice.tell(new BoxOffice.Initialize(), ActorRef.noSender());


        // イベントは「音楽」でオープン
        boxOffice.tell(new BoxOffice.Open(new BoxOffice.EventType("Music")), ActorRef.noSender());

        // 5秒後にクローズ
        system.scheduler().scheduleOnce(Duration.create(5, "seconds"), boxOffice, new BoxOffice.Close(), system.dispatcher(), ActorRef.noSender());
        // 10秒後にイベントを「スポーツ」でオープン
        system.scheduler().scheduleOnce(Duration.create(10, "seconds"), boxOffice, new BoxOffice.Open(new BoxOffice.EventType("Sports")), system.dispatcher(), ActorRef.noSender());

        // 15秒後にシャットダウン
        system.scheduler().scheduleOnce(Duration.create(15, "seconds"), boxOffice, new BoxOffice.Shutdown(), system.dispatcher(), ActorRef.noSender());

        // 3秒毎にチケットを注文
        boxOffice.tell(new BoxOffice.Order("RHCP", 4), ActorRef.noSender());
        Thread.sleep(3000);
        boxOffice.tell(new BoxOffice.Order("RHCP", 1), ActorRef.noSender());
        Thread.sleep(3000);
        boxOffice.tell(new BoxOffice.Order("RHCP", 2), ActorRef.noSender());
        Thread.sleep(3000);
        boxOffice.tell(new BoxOffice.Order("RHCP", 3), ActorRef.noSender());
        Thread.sleep(3000);
        boxOffice.tell(new BoxOffice.Order("RHCP", 5), ActorRef.noSender());

        Await.result(system.whenTerminated(), Duration.Inf());

    }
}
