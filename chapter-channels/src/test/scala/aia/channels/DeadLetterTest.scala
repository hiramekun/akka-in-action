package aia.channels

import java.util.Date

import akka.actor.{ActorSystem, DeadLetter, PoisonPill, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class DeadLetterTest extends TestKit(ActorSystem("DeadLetterTest"))
  with WordSpecLike with BeforeAndAfterAll with MustMatchers
  with ImplicitSender {

  override def afterAll() {
    system.terminate()
  }

  "DeadLetter" must {
    "catch messages send to deadLetters" in {
      val deadLetterMonitor = TestProbe()

      system.eventStream.subscribe(
        deadLetterMonitor.ref,
        classOf[DeadLetter])

      val msg = new StateEvent(new Date(), "Connected")
      system.deadLetters ! msg

      val dead = deadLetterMonitor.expectMsgType[DeadLetter]
      dead.message must be(msg)
      dead.sender must be(testActor)
      dead.recipient must be(system.deadLetters)
    }
    "catch deadLetter messages send to deadLetters" in {

      val deadLetterMonitor = TestProbe()
      val actor = system.actorOf(Props[EchoActor])

      system.eventStream.subscribe(
        deadLetterMonitor.ref,
        classOf[DeadLetter])

      val msg = new Order("me", "Akka in Action", 1)
      val dead = DeadLetter(msg, testActor, actor)
      system.deadLetters ! dead

      deadLetterMonitor.expectMsg(dead)

      system.stop(actor)

    }

    "catch messages send to terminated Actor" in {

      val deadLetterMonitor = TestProbe()

      system.eventStream.subscribe(
        deadLetterMonitor.ref,
        classOf[DeadLetter])

      val actor = system.actorOf(Props[EchoActor])
      actor ! PoisonPill
      val msg = new Order("me", "Akka in Action", 1)
      actor ! msg

      val dead = deadLetterMonitor.expectMsgType[DeadLetter]
      dead.message must be(msg)
      dead.sender must be(testActor)
      dead.recipient must be(actor)

    }

  }
}
