package services

import java.util.{UUID, Date}

import scala.collection.mutable

import lehtikierto.shared._
import scala.collection.mutable.HashMap
import lehtikierto.shared.SubscriptionId
import lehtikierto.shared.MagazineId
import lehtikierto.shared.ShareId

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
  
  var todos = Seq(
    TodoItem("41424344-4546-4748-494a-4b4c4d4e4f50", 0x61626364, "Wear shirt that says “Life”. Hand out lemons on street corner.", TodoLow, completed = false),
    TodoItem("2", 0x61626364, "Make vanilla pudding. Put in mayo jar. Eat in public.", TodoNormal, completed = false),
    TodoItem("3", 0x61626364, "Walk away slowly from an explosion without looking back.", TodoHigh, completed = false),
    TodoItem("4", 0x61626364, "Sneeze in front of the pope. Get blessed.", TodoNormal, completed = true)
  )

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
  
  override def welcomeMsg(name: String): String = {
    Thread.sleep(1000)
    s"Welcome to SPA, $name! Time is now ${new Date}"
  }

  override def getAllTodos(): Seq[TodoItem] = {
    // provide some fake Todos
    Thread.sleep(300)
    println(s"Sending ${todos.size} Todo items")
    todos
  }

  // update a Todo
  override def updateTodo(item: TodoItem): Seq[TodoItem] = {
    // TODO, update database etc :)
    if(todos.exists(_.id == item.id)) {
      todos = todos.collect {
        case i if i.id == item.id => item
        case i => i
      }
      println(s"Todo item was updated: $item")
    } else {
      // add a new item
      val newItem = item.copy(id = UUID.randomUUID().toString)
      todos :+= newItem
      println(s"Todo item was added: $newItem")
    }
    Thread.sleep(300)
    todos
  }

  // delete a Todo
  override def deleteTodo(itemId: String): Seq[TodoItem] = {
    println(s"Deleting item with id = $itemId")
    Thread.sleep(300)
    todos = todos.filterNot(_.id == itemId)
    todos
  }
}
