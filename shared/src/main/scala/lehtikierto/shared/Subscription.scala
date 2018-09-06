package lehtikierto.shared

case class SubscriptionId(id: String)
case class Subscription(id: SubscriptionId, user: User, magazine: Magazine)