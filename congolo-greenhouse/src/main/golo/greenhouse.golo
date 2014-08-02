module congolo.Greenhouse

import java.lang.Thread
import java.util.UUID

import gololang.concurrent.messaging.MessagingEnvironment
import fr.insalyon.telecom.congolo.decisionmaker.DefaultDecisionMaker

function regulate = |greenhouse|@(TemperatureTooHot){
  println("Cooling " + greenhouse)
}

function regulate = |greenhouse|@(TemperatureTooCold){
  println("Warming " + greenhouse)
}

function main = |args| {
  let DELAY = 1000_L
  let STEPS = 10
  let greenHousePoolSize = 1
  let randomGen = java.util.Random()

  let context = context.TemperatureContext()
  let cm = ContextManager.INSTANCE()
  cm: register("Greenhouse", context)  
  let dm = decisionmaker.GreenDecisionMaker()
  DecisionMakerManager.setDecisionMaker(dm)

  var greenHouses = set[]
  for (var i = 0, i < greenHousePoolSize, i = i + 1){
    greenHouses: add(GreenHouse())
  }

  for (var i = 0, i < STEPS, i = i + 1) {
    Thread.sleep(DELAY)
    context: setValue(randomGen: nextInt(40))
    foreach greenHouse in greenHouses {
      regulate(greenHouse)
    }
  }
  EnvironmentProvider.getEnvironment(): shutdown()
}

function GreenHouse = {
  return DynamicObject():
  id(UUID.randomUUID(): toString()):
  temperature(0):
  define("toString", |this| -> "Greenhouse #" + this: id()):
  define("regulate", |this| -> println(this + " regulated"))
}
