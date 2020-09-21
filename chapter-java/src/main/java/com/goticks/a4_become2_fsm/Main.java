package com.goticks.a4_become2_fsm;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.goticks.a4_become2_fsm.BoxOffice.Shutdown;
import com.goticks.a4_become2_fsm.BoxOffice.*;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

class Main {
    public static void main(String args[]) throws Exception {

        final ActorSystem system = ActorSystem.create("main", ConfigFactory.load("goticks"));
        final ActorRef boxOffice = system.actorOf(BoxOffice.props(), "boxOffice");

        boxOffice.tell(new Initialize(), ActorRef.noSender());

        // 5秒後にクローズ
        system.scheduler().scheduleOnce(Duration.create(5, "seconds"), boxOffice, new Close(), system.dispatcher(), ActorRef.noSender());
        // 10秒後にイベントをオープン
        system.scheduler().scheduleOnce(Duration.create(10, "seconds"), boxOffice, new Open(), system.dispatcher(), ActorRef.noSender());

        // 15秒後にクローズ
        system.scheduler().scheduleOnce(Duration.create(15, "seconds"), boxOffice, new Close(), system.dispatcher(), ActorRef.noSender());

        // 20秒後にシャットダウン
        system.scheduler().scheduleOnce(Duration.create(20, "seconds"), boxOffice, new Shutdown(), system.dispatcher(), ActorRef.noSender());

        // 3秒毎にチケットを注文
        boxOffice.tell(new Order("RHCP", 4), ActorRef.noSender());
        Thread.sleep(3000);
        boxOffice.tell(new Order("RHCP", 1), ActorRef.noSender());
        Thread.sleep(3000);
        boxOffice.tell(new Order("RHCP", 2), ActorRef.noSender());
        Thread.sleep(3000);
        boxOffice.tell(new Order("RHCP", 3), ActorRef.noSender());
        Thread.sleep(3000);
        boxOffice.tell(new Order("RHCP", 5), ActorRef.noSender());

        Await.result(system.whenTerminated(), Duration.Inf());

    }
}
