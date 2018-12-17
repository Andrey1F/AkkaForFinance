import Transaction.BankId
import akka.actor.{Actor, Props}

case class accountCreateCmd(name: String, money: Double)
case class ActorRouterRequest(id: BankId)

class AccountSupervisor extends Actor{
  var count : Int = 1
  override def receive: Receive = {
    case cmd : accountCreateCmd => {
      println(s"count = $count")
      val ref = context.actorOf(Props(new AccountActor(BankId(count), cmd.name, cmd.money)), count.toString)
      println(s"Actor $ref created, count = $count")
      count = count + 1


    }
    case cmd : ActorRouterRequest => {
      context.child(cmd.id.idNumber.toString()) match {
        case Some(actorRef) => sender() ! actorRef
        case None => sender() ! new RuntimeException("Actor not found")
      }
    }
    case _ => {
      println("I didn't understand. Fuck you!")
    }


  }
}
