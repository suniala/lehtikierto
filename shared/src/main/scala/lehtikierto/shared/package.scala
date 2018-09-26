package lehtikierto

package object shared {
  case class User(username: String)

  case class MagazineId(id: String)
  case class Magazine(id: MagazineId, name: String)

  case class SubscriptionId(id: String)
  case class Subscription(id: SubscriptionId, user: User, magazine: Magazine)

  case class NumberId(id: String)
  case class Number(id: Option[NumberId], magazine: Magazine, year: Int, number: String)

  case class ShareId(id: String)
  case class Share(id: ShareId, user: User, number: Number)
}
