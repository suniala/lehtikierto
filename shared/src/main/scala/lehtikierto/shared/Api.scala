package lehtikierto.shared

//noinspection AccessorLikeMethodIsEmptyParen
trait Api {
  def getUser(): Option[User]
  
  def getAllMagazines(): Seq[Magazine]
  
  def getSubscriptions(): Seq[Subscription]
  def addSubscription(id: MagazineId): Subscription
  def unsubscribe(id: SubscriptionId): Boolean
  
  def getShares(): Seq[Share]
  def addShare(number: Number): Share
}
