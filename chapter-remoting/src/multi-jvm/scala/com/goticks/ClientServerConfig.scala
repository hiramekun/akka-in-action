package com.goticks

object ClientServerConfig extends MultiNodeConfig {
  val frontend = role("frontend")
  val backend = role("backend")
}
