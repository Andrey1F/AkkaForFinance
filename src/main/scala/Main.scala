import java.util.UUID

import Transaction.{BankId, Transaction}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Fuck_the_system")
    val supervisor = system.actorOf(Props[AccountSupervisor], name = "Supervisor")

    while (true) {
      println("for creating actor enter 1, for transaction input 2")
      val option:Int = scala.io.StdIn.readInt()

      if (option == 1) {
        println("Gimme fookin' naem")
        val name: String = scala.io.StdIn.readLine()
        println("Enter money")
        val money = scala.io.StdIn.readDouble()
        val command = accountCreateCmd(name, money)
        supervisor ! command
      } else {
        println("enter sender bank id")
        val senderBankId = scala.io.StdIn.readInt()
        println("enter receiver bank id")
        val receiverBankId = scala.io.StdIn.readInt()
        println("enter amount")
        val money = scala.io.StdIn.readDouble()

        val transaction = new Transaction(UUID.randomUUID(), BankId(senderBankId), BankId(receiverBankId), money)

        implicit val timeout = Timeout(10 seconds)
        implicit val ec = ExecutionContext.global

        val checkSenderRequest = (supervisor ? ActorRouterRequest(BankId(senderBankId))).mapTo[ActorRef]
        checkSenderRequest.onComplete {
          case Success(actorRef) => {
            actorRef ! transaction
            println("transaction has been sent")
          }
          case Failure(exception) => println("sender not found")
        }


      }
    }

  }

}
