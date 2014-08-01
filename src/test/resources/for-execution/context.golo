module Context

function contextTest = |arg1|@(wifi) {
  println("wifi")
}

function contextTest = |arg1|@(lte) {
  println("lte")
}

function main = |args| {
  contextTest__$context$__lte("")
  contextTest__$context$__wifi("")
  contextTest("bytecode generated")
}