package test

class TestBean {
  def testMethod(i: Int): Int = {
    println(i)
    i + 1
  }

  def exceptionMethod() {
    println("generate a exception...")
    throw new NullPointerException("a generated exception")
  }

  def testNullParameter(i: java.lang.Integer) {
    println(i)
  }
}