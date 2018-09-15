package services

import lehtikierto.shared.{MagazineId, ShareId, SubscriptionId, _}

import scala.collection.mutable

class ApiService extends Api {
  val idgen: () => String = (() => {
    var counter = 1
    () => {
      counter += 1
      counter.toString
    }
  })()
  
  object DummyUsers {
    val teppo = User("Teppo")
    val liisa = User("Liisa")
    val jaakko = User("Jaakko")
  }
  
  object DummyMagazines {
    val yl = Magazine(MagazineId(idgen()), "Yölehti")
    val kp = Magazine(MagazineId(idgen()), "Koillis-Pirkanmaa")
    val vv = Magazine(MagazineId(idgen()), "Valtavirta")
  }
  
  object DummyShares {
    val teppoKp = Share(ShareId(idgen()), DummyUsers.teppo, DummyMagazines.kp)
    val liisaKp = Share(ShareId(idgen()), DummyUsers.liisa, DummyMagazines.kp)
    val liisaYl = Share(ShareId(idgen()), DummyUsers.liisa, DummyMagazines.yl)
  }
  
  val user = Some(DummyUsers.teppo)
//  val user: Option[User] = None
  
  val allMagazines = Seq(
      DummyMagazines.yl,
      DummyMagazines.kp,
      DummyMagazines.vv
  )
  
  val allSubscriptions: mutable.Map[SubscriptionId, Subscription] = mutable.Map() ++ Seq(
      Subscription(SubscriptionId(idgen()), DummyUsers.teppo, DummyMagazines.yl)
  ).map(s => (s.id, s)).toMap

  val allShares = Seq(DummyShares.teppoKp, DummyShares.liisaKp, DummyShares.liisaYl)
  
  override def getUser(): Option[User] = user
  
  override def getAllMagazines(): Seq[Magazine] = {
    Thread.sleep(600)
    allMagazines
  }
  
  override def getSubscriptions(): Seq[Subscription] = {
    Thread.sleep(900)
    user match {
      case Some(u) => allSubscriptions.filter(p => p._2.user == u).values.toSeq
      case _ => Nil
    }
  }
  
  override def addSubscription(magazineId: MagazineId): Subscription = {
    user match {
      case Some(u) =>
        allSubscriptions.values.find(_.magazine.id == magazineId) match {
          case None =>
            val subscription = Subscription(SubscriptionId(idgen()), u, allMagazines.find(_.id.equals(magazineId)).get)
            allSubscriptions(subscription.id) = subscription
            subscription
          case Some(subscription) => subscription
        }
    }
  }
  
  override def unsubscribe(id: SubscriptionId): Boolean = {
    user match {
      case Some(_) => allSubscriptions.remove(id) match {
        case Some(_) => true
        case _ => false
      }
      case _ => false
    }
  }

  override def getShares(): Seq[Share] = {
    Thread.sleep(300)
    user match {
      case Some(u) => allShares.filter(_.user == u)
      case _ => Nil
    }
  }
}
