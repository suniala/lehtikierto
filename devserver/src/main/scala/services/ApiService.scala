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

  object DummyNumbers {
    val yl1 = Number(Some(NumberId(idgen())), DummyMagazines.yl, 2018, "34")
    val kp1 = Number(Some(NumberId(idgen())), DummyMagazines.kp, 2018, "maaliskuu")
  }
  
  object DummyShares {
    val teppoKp = Share(ShareId(idgen()), DummyUsers.teppo, DummyNumbers.kp1)
    val liisaKp = Share(ShareId(idgen()), DummyUsers.liisa, DummyNumbers.kp1)
    val liisaYl = Share(ShareId(idgen()), DummyUsers.liisa, DummyNumbers.yl1)
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

  val allShares = mutable.ArrayBuffer(DummyShares.teppoKp, DummyShares.liisaKp, DummyShares.liisaYl)

  val allNumbers = mutable.ArrayBuffer(DummyNumbers.kp1, DummyNumbers.yl1)

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

  override def addShare(number: Number): Share = {
    // TODO: don't add if an identical share already exists
    user match {
      case Some(u) => {
        val share = Share(ShareId(idgen()), u, getNumberOrAdd(number))
        allShares.append(share)
        share
      }
      case _ => throw new RuntimeException("not allowed")
    }
  }

  private def getNumberOrAdd(candidateNumber: Number): Number = {
    val existingNumber = allNumbers.find(n => n.magazine.id == candidateNumber.magazine.id
      && n.year == candidateNumber.year && n.number == candidateNumber.number)
    if (existingNumber.isDefined) {
      existingNumber.get
    } else {
      val magazine = getMagazine(candidateNumber.magazine.id)
      val newNumber = Number(Some(NumberId(idgen())), magazine, candidateNumber.year, candidateNumber.number)
      allNumbers.append(newNumber)
      newNumber
    }
  }

  private def getMagazine(magazineId: MagazineId) = getOrFail(allMagazines)(m => m.id == magazineId)

  private def getOrFail[T](items: Seq[T])(p: T => Boolean): T = {
    val item = items.find(p)
    if (item.isDefined) item.get
    else throw new RuntimeException("item does not exist")
  }
}
