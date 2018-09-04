package services

import java.util.{UUID, Date}

import lehtikierto.shared._

class ApiService extends Api {
  object DummyUsers {
    val teppo = User("Teppo");
  }
  
  object DummyMagazines {
    val yolehti = Magazine("1", "Yölehti")
    val kp = Magazine("2", "Koillis-Pirkanmaa")
  }
  
  val user = Some(DummyUsers.teppo)
  
  val allMagazines = Seq(
      DummyMagazines.yolehti,
      DummyMagazines.kp
  )

  val allSubscriptions = Map[User, Seq[Subscription]](
      DummyUsers.teppo -> Seq(
          Subscription("1", DummyUsers.teppo, DummyMagazines.yolehti)
      )
  ) withDefaultValue Nil
  
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
      case Some(user) => allSubscriptions(user)
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
