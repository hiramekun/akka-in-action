package aia.persistence.sharded

import aia.persistence.rest.ShoppersServiceSupport
import akka.actor._

object ShardedMain extends App with ShoppersServiceSupport {
  implicit val system = ActorSystem("shoppers")

  val shoppers = system.actorOf(ShardedShoppers.props,
    ShardedShoppers.name)

  startService(shoppers)
}
