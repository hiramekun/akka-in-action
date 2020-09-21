package aia.state

import akka.actor.ActorSystem
import akka.agent.Agent
import akka.testkit.{TestKit, TestProbe}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.concurrent.duration._
import scala.concurrent.stm._
import scala.concurrent.{Await, Future}

class AgentTest extends TestKit(ActorSystem("AgentTest"))
  with WordSpecLike with BeforeAndAfterAll with MustMatchers {
  implicit val ec = system.dispatcher
  implicit val timeout = Timeout(3 seconds)

  override def afterAll(): Unit = {
    system.terminate()
  }

  "Agent" must {
    "test1" in {
      val agent = Agent(new StateBookStatistics(0, Map()))

      Future {
        Thread.sleep(2000)
        agent send (new StateBookStatistics(22, Map()))
      }
      println("1: " + agent())
      atomic {
        txn ⇒
          val value = agent.get
          println("2: " + agent())
          Thread.sleep(5000)
          agent send (new StateBookStatistics(value.sequence + 1, Map()))
      }
      println("3: " + agent())
      println("4: " + Await.result(agent.future(), 1 second))
    }
    "test2" in {
      val agent = Agent(new StateBookStatistics(0, Map()))

      Future {
        Thread.sleep(2000)
        agent send (new StateBookStatistics(22, Map()))
      }
      println("1: " + agent())
      atomic {
        txn ⇒
          println("2: " + agent())
          Thread.sleep(5000)
          agent.send((oldState) => oldState.copy(sequence = oldState.sequence + 1))
      }
      println("3: " + agent())
      println("4: " + Await.result(agent.future(), 1 second))
    }
    "test3" in {
      val agent = Agent(new StateBookStatistics(0, Map()))
      val func = (oldState: StateBookStatistics) => {
        oldState.copy(sequence = oldState.sequence + 1)
      }
      agent.send(func)
      println("3: " + agent())
      println("4: " + Await.result(agent.future(), 1 second))

    }
    "test4" in {
      val probe = TestProbe()
      val agent = Agent(new StateBookStatistics(0, Map()))
      val func = (oldState: StateBookStatistics) => {
        if (oldState.sequence == 0)
          probe.ref ! "test"
        oldState.copy(sequence = oldState.sequence + 1)
      }
      agent.send(func)
      println("3: " + agent())
      probe.expectMsg("test")
      println("4: " + agent())

    }
    "test5" in {
      val agent1 = Agent(3)

      val agent4 = agent1 map (_ + 1)
      println("1: " + agent4())

      agent1 send (_ + 2)
      Await.result(agent1.future(), 1 second)

      println("2: " + Await.result(agent4.future(), 1 second))
    }
  }
  "AgentMgr" must {
    "test" in {
      val bookName = "Akka in Action"
      val mgr = new BookStatisticsMgr(system)
      mgr.addBooksSold(bookName, 1)
      mgr.addBooksSold(bookName, 1)
      Await.result(mgr.stateAgent.future(), 1 second)
      val book = new BookStatistics(bookName, 2)
      mgr.getStateBookStatistics() must be(new StateBookStatistics(2, Map(bookName -> book)))
    }
    "test alter" in {
      val bookName = "Akka in Action"
      val mgr = new BookStatisticsMgr(system)
      mgr.addBooksSold(bookName, 1)
      val state = mgr.addBooksSoldAndReturnNewState(bookName, 1)
      val book = new BookStatistics(bookName, 2)
      state must be(new StateBookStatistics(2, Map(bookName -> book)))
    }
  }
}
