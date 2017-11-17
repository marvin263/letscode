
package com.horstmann.impatient


package object people {
  val defaultName = "Marvin is great"

  // 1. 根据返回值确定了 函数的类型
  // 2. if else都是带有返回值的
  // 3. 定义function很蛋疼，必须有 =
  // 4. 没有=，则，含义就是 我一定是 啥都不返回的
  def trim2empty(str: String): String = {
    if (str == null) "" else str.trim
  }

  // 完整的写法
  def box(str: String): Unit = {
    val border = "-" * str.length + "--\n"
    println(border + "|" + str + "|\n" + border)
  }

  // = 是必须的，function定义就是这样
  // 返回类型是 推导出来的
  def box2(str: String) = {
    val border = "-" * str.length + "--\n"
    println(border + "|" + str + "|\n" + border)
  }

  // 没有 = ，则，必须啥都不返回
  def box3(str: String) {
    val border = "-" * str.length + "--\n"
    println(border + "|" + str + "|\n" + border)
  }
  def encounterRenaming(): Unit ={
    import java.util.{HashMap=>MarvinHashMap, ArrayList=>MarvinArrayList}
    new MarvinHashMap[String, String].clear()
    new MarvinArrayList[Int].add(0)
  }
}

package people {
  class Visibility{
    private[people] def description = "This method is visible in its own package"
    private[impatient] def seemore = "This method is visible in impatient as well as subpackage"


  }
  class Person {
    var name = defaultName // A constant from the package
  }

}

package howtorun{
  object Hello{
    def main(args: Array[String]) {
      println("Hello World!")
    }
  }
  object HelloApp extends App{
    println("Place code into the constructor body")
    val a = Array.apply("I am", " using", "apply")
    println(a.toList)
  }
}


