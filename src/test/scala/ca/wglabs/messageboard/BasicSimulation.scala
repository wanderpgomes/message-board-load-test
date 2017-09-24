package ca.wglabs.messageboard

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val httpConf = http
    .baseURL("https://localhost:8443") // Here is the root for all relative URLs

 val getUsers = scenario("Get Users Scenario")
   .exec(http("get users").get("/users").check(status.is(200)))

 val sendMessages = scenario("Send Messages Scenario")
   .exec(http("create messages")
     .post("/messages").check(status.is(200))
     .body(StringBody(
       """{ "text": "Hello Gatling!", "userId": "1" ,
           | "city":"Toronto", "temperature": "20.0",
           | "latitude": "43.7", "longitude": "-79.42"}""".stripMargin)).asJSON)

  val getMessages = scenario("Get Messages Scenario")
    .exec(http("get all messages").get("/messages").check(status.is(200)))


 setUp(getUsers.inject(rampUsers(100) over (10 seconds)).protocols(httpConf),
       sendMessages.inject(rampUsers(100) over (10 seconds)).protocols(httpConf),
       getMessages.inject(rampUsers(10) over (10 seconds)).protocols(httpConf))
  .assertions(global.responseTime.percentile1.lt(400))
}
