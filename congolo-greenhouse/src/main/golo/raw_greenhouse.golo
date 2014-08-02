module golo.Greenhouse

import java.lang.Thread
import java.util.UUID

function regulateTooHot = |greenhouse| {
  println("Cooling " + greenhouse)
}

function regulateTooCold = |greenhouse| {
  println("Warming " + greenhouse)
}

function main = |args| {
  let DELAY = 1000_L
  let STEPS = 10
  let greenHousePoolSize = 1
  let randomGen = java.util.Random()
  let temperatureHolder = Temperature(0)

  var greenHouses = set[]
  for (var i = 0, i < greenHousePoolSize, i = i + 1){
    greenHouses: add(GreenHouse())
  }

  for (var i = 0, i < STEPS, i = i + 1) {
    Thread.sleep(DELAY)
    temperatureHolder: value( randomGen: nextInt(40) )
    foreach greenHouse in greenHouses {
      if (temperatureHolder: value() > 20) {
        regulateTooCold(greenHouse)
      } else {
        regulateTooHot(greenHouse)
      }
    }
  }
}

struct Temperature = { value }

function GreenHouse = {
  return DynamicObject():
  id(UUID.randomUUID(): toString()):
  temperature(0):
  define("toString", |this| -> "Greenhouse #" + this: id()):
  define("regulate", |this| -> println(this + " regulated"))
}
