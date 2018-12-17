import Transaction.{MoneyRequest, MoneyResponse, Transaction, MoneyDeposit}
import akka.actor.{Actor, ActorRef}

class TransactionActor(actorRef: ActorRef) extends Actor{
  var depositMoney:Double = 0
  override def receive: Receive = {
    case msg : Transaction => {
      sender() ! MoneyRequest(msg.money)
    }
    case msg : MoneyResponse => {
      depositMoney += msg.money
      actorRef ! MoneyDeposit(msg.money)
      depositMoney -= msg.money
    }
  }
}
