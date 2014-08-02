module benchmark

#############

function contextTest = |arg1, arg2|@(WiFiConnectivityContext) {
  return("wifi@" + arg1 + "#" + arg2)
}

function contextTest = |arg1, arg2|@(LTEConnectivityContext) {
  return("lte@" + arg1 + "#" + arg2)
}

function noContextTest = |arg1, arg2|{
  return("no context " + arg1 + "#" + arg2)
}

#############

function contextTestInvoke = |arg1, arg2|{
  return contextTest(arg1, arg2)
}

function noContextTestInvoke = |arg1, arg2|{
  return noContextTest(arg1, arg2)
}

#############
#############

function contextTest_10 = @(LTEConnectivityContext) {
  return java.time.LocalTime.now()
}

function contextTest_9 = @(LTEConnectivityContext) {
  return contextTest_10()
}

function contextTest_8 = @(LTEConnectivityContext) {
  return contextTest_9()
}

function contextTest_7 = @(LTEConnectivityContext) {
  return contextTest_8()
}

function contextTest_6 = @(LTEConnectivityContext) {
  return contextTest_7()
}

function contextTest_5 = @(LTEConnectivityContext) {
  return contextTest_6()
}

function contextTest_4 = @(LTEConnectivityContext) {
  return contextTest_5()
}

function contextTest_3 = @(LTEConnectivityContext) {
  return contextTest_4()
}

function contextTest_2 = @(LTEConnectivityContext) {
  return contextTest_3()
}

function contextTest_1 = @(LTEConnectivityContext) {
  return contextTest_2()
}

###########

function noContextTest_10 =  {
  return java.time.LocalTime.now()
}

function noContextTest_9 =  {
  return noContextTest_10()
}
function noContextTest_8 =  {
  return noContextTest_9()
}

function noContextTest_5 =  {
  return noContextTest_6()
}

function noContextTest_7 =  {
  return noContextTest_8()
}

function noContextTest_6 =  {
  return noContextTest_7()
}

function noContextTest_4 =  {
  return noContextTest_5()
}

function noContextTest_3 =  {
  return noContextTest_4()
}

function noContextTest_2 =  {
  return noContextTest_3()
}

function noContextTest_1 =  {
  return noContextTest_2()
}

###########
###########

function contextTest_1Invoke = {
  return contextTest_1()
}

function noContextTest_1Invoke = {
  return noContextTest_1()
}