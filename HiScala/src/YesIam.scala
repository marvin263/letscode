import scala.runtime.Null$
package com {
  package horstmann {
    package impatient {

      class Employee {

      }

    }

  }

}

/**
  * Created by libing2 on 2016/3/22.
  */
class YesIam {
}

trait Logged {
  def log(msg: String) {}

}

class Account {
  var balance: Double = 100.9D
}

class SavingsAccount extends Account with Logged {
  def withdraw(amount: Double) {
    if (amount > balance) {
      log("Insufficient funds")
    } else {

    }
  }
}

trait ConsoleLogger extends Logged {
  override def log(msg: String) {
    println(msg)
  }
}


//def abs1(x: Double) {
//  if (x >= 0) x else -x
//}


object ForRun {

  def abs(x: Double, s: String) = {
    val border = "-" * s.length + "--\n"
    println(border + "|" + s + "|\n" + border)
    var a = 10
  }

  def box(s: String) = {
    val border = "-" * s.length + "--\n"
    println(border + "|" + s + "|\n" + border)
    var a = 10
    a
  }

  def main(args: Array[String]) {
    println(abs(9.0D, "dddddddddddd").getClass)
    println(box("aaaaaaaa"))
    println(Void.TYPE)
  }
}







































