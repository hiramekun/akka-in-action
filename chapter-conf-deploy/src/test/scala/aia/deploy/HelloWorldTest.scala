package aia.deploy

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class HelloWorldTest extends TestKit(ActorSystem("HelloWorldTest"))
  with ImplicitSender
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll {

  val actor = TestActorRef[HelloWorld]

  override def afterAll(): Unit = {
    system.terminate()
  }

  "HelloWorld" must {
    "reply when sending a string" in {
      actor ! "everybody"
      expectMsg("Hello everybody")
    }
  }
}
