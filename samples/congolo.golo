module congolo

import java.lang.Thread
import java.util.UUID

import gololang.concurrent.messaging.MessagingEnvironment
import fr.insalyon.telecom.congolo.decisionmaker.DefaultDecisionMaker

let env = MessagingEnvironment.builder(): withFixedThreadPool()
let dm = DefaultDecisionMaker(env)
let contexts = ContextManager.register("Temperature")
let DELAY = 1000_L
let greenHousePoolSize = 1

function main = |args| {
  var greenHouses = set[]
  for (var i = 0, i < greenHousePoolSize, i = i + 1){
    greenHouses: add(GreenHouse())
  }

  while(true){
    Thread.sleep(DELAY)
    foreach greenHouse in greenHouses {
      regulate(greenHouse)
    }
    break
  }
}

function GreenHouse = {
  return DynamicObject():
  id(UUID.randomUUID(): toString()):
  temperature(0):
  define("toString", |this| -> "Greenhouse #" + this: id()):
  define("regulate", |this| -> println(this + " regulated"))
}

function regulate = |greenhouse|@(TemperatureTooHot){
  println("Cooling " + greenhouse)
}

function regulate = |greenhouse|@(TemperatureTooCold){
  println("Warming " + greenhouse)
}
