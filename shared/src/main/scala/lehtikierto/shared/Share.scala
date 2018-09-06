package lehtikierto.shared

case class ShareId(id: String)
case class Share(id: ShareId, user: User, magazine: Magazine)