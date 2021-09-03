import java.util.Date

class PaymentRecord() {
  private var paymentId = ""
  private var tenantId = ""
  private var txnDate = 0L

  def this(paymentId: String, tenantId: String, txnDate: Date) {
    this()
    this.paymentId = paymentId
    this.tenantId = tenantId
    this.txnDate = txnDate.getTime
  }

  def getPaymentId: String = paymentId
  def getTenantId: String = tenantId
  def getTxnDate: Long = txnDate
}