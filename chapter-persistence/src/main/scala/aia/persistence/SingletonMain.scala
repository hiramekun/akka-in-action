package aia.persistence

import aia.persistence.rest.ShoppersServiceSupport
import akka.actor._

object SingletonMain extends App with ShoppersServiceSupport {
  implicit val system = ActorSystem("shoppers")
  val shoppers = system.actorOf(ShoppersSingleton.props,
    ShoppersSingleton.name)
  startService(shoppers)
}
