package Transaction

import java.util.UUID

case class Transaction(transactionUUID: UUID, sender: BankId, receiver: BankId, money: Double)
case class MoneyRequest(money: Double)
case class MoneyResponse(money: Double)
case class MoneyDeposit(money: Double)

case class BankId(idNumber: Int) {
  if (idNumber <= 0) throw new RuntimeException(s"ID number $idNumber is invalid")
}