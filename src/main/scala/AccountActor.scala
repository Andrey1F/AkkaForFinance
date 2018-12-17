import Transaction.{BankId, MoneyRequest, MoneyResponse, Transaction, MoneyDeposit}
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class AccountActor(val id: BankId, val name: String, var money: Double) extends Actor{
  override def preStart(): Unit = {
    println(s"$name has been created with an id of ${id.idNumber} and a money of $money")
  }
  override def receive: Receive = {
    case msg : Transaction => {
      implicit val timeout = Timeout(10 seconds)
      implicit val ec = ExecutionContext.global
      val routerRequest = (context.parent ? ActorRouterRequest(msg.receiver)).mapTo[ActorRef]
      routerRequest.onComplete {
        case Success(actorRef) => {
          val ref = context.actorOf(Props(new TransactionActor(actorRef)))
          ref ! msg
        }
        case Failure(exception) => println("transaction failure")

      }
      //money += msg.money
      //println(s"[$name] just received ${msg.money} and their current balance is $money")
    }
    case msg : MoneyRequest => {
      if (msg.money <= money) {
        money -= msg.money
        sender() ! MoneyResponse(msg.money)
        println("sending moneyResponse to TransactionActor")
      } else {
        println("not enough money for response")
      }
    }
    case msg : MoneyDeposit => {
      money += msg.money
      println(s"${msg.money} received by $name")
    }
    case _ => {
      println("Fuck you")
    }
  }
}
