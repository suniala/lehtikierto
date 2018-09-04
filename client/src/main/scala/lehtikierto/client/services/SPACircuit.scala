package lehtikierto.client.services

import autowire._
import diode._
import diode.data._
import diode.util._
import diode.react.ReactConnector
import lehtikierto.shared.{TodoItem, Api}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import lehtikierto.shared.{User, Magazine, Subscription}
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit

// Actions
case object FetchUser extends Action
case class ReceiveUser(user: Option[User]) extends Action

case class UpdateMagazines(potResult: Pot[Seq[Magazine]] = Empty) extends PotAction[Seq[Magazine], UpdateMagazines] {
  override def next(value: Pot[Seq[Magazine]]) = UpdateMagazines(value)
}

case class UpdateSubscriptions(potResult: Pot[Seq[Subscription]] = Empty) extends PotAction[Seq[Subscription], UpdateSubscriptions] {
  override def next(value: Pot[Seq[Subscription]]) = UpdateSubscriptions(value)
}

case object RefreshTodos extends Action

case class UpdateAllTodos(todos: Seq[TodoItem]) extends Action

case class UpdateTodo(item: TodoItem) extends Action

case class DeleteTodo(item: TodoItem) extends Action

case class UpdateMotd(potResult: Pot[String] = Empty) extends PotAction[String, UpdateMotd] {
  override def next(value: Pot[String]) = UpdateMotd(value)
}

// The base model of our application
case class RootModel(user: Pot[User], magazines: Pot[Seq[Magazine]], subscriptions: Pot[Seq[Subscription]], todos: Pot[Todos], motd: Pot[String])

class UserHandler[M](modelRW: ModelRW[M, Pot[User]]) extends ActionHandler(modelRW) {
  override def handle = {
    case FetchUser =>
      effectOnly(Effect(AjaxClient[Api].getUser().call().map(ReceiveUser)))
    case ReceiveUser(user) =>
      updated(user match {
        case Some(user) => Ready(user)
        case _ => Empty
      })
  }
}

class MagazineHandler[M](modelRW: ModelRW[M, Pot[Seq[Magazine]]]) extends ActionHandler(modelRW) {
  implicit val runner = new RunAfterJS

  override def handle = {
    case action: UpdateMagazines =>
      val updateF = action.effect(AjaxClient[Api].getAllMagazines().call())(identity _)
      // Handle with a handler that does progress updates every n milliseconds.
      action.handleWith(this, updateF)(PotAction.handler(FiniteDuration(100, TimeUnit.MILLISECONDS)))
  }
}

class SubscriptionHandler[M](modelRW: ModelRW[M, Pot[Seq[Subscription]]]) extends ActionHandler(modelRW) {
  implicit val runner = new RunAfterJS

  override def handle = {
    case action: UpdateSubscriptions =>
      val updateF = action.effect(AjaxClient[Api].getSubscriptions().call())(identity _)
      // Handle with a handler that does progress updates every n milliseconds.
      action.handleWith(this, updateF)(PotAction.handler(FiniteDuration(100, TimeUnit.MILLISECONDS)))
  }
}

case class Todos(items: Seq[TodoItem]) {
  def updated(newItem: TodoItem) = {
    items.indexWhere(_.id == newItem.id) match {
      case -1 =>
        // add new
        Todos(items :+ newItem)
      case idx =>
        // replace old
        Todos(items.updated(idx, newItem))
    }
  }
  def remove(item: TodoItem) = Todos(items.filterNot(_ == item))
}

/**
  * Handles actions related to todos
  *
  * @param modelRW Reader/Writer to access the model
  */
class TodoHandler[M](modelRW: ModelRW[M, Pot[Todos]]) extends ActionHandler(modelRW) {
  override def handle = {
    case RefreshTodos =>
      effectOnly(Effect(AjaxClient[Api].getAllTodos().call().map(UpdateAllTodos)))
    case UpdateAllTodos(todos) =>
      // got new todos, update model
      updated(Ready(Todos(todos)))
    case UpdateTodo(item) =>
      // make a local update and inform server
      updated(value.map(_.updated(item)), Effect(AjaxClient[Api].updateTodo(item).call().map(UpdateAllTodos)))
    case DeleteTodo(item) =>
      // make a local update and inform server
      updated(value.map(_.remove(item)), Effect(AjaxClient[Api].deleteTodo(item.id).call().map(UpdateAllTodos)))
  }
}

/**
  * Handles actions related to the Motd
  *
  * @param modelRW Reader/Writer to access the model
  */
class MotdHandler[M](modelRW: ModelRW[M, Pot[String]]) extends ActionHandler(modelRW) {
  implicit val runner = new RunAfterJS

  override def handle = {
    case action: UpdateMotd =>
      val updateF = action.effect(AjaxClient[Api].welcomeMsg("User X").call())(identity _)
      // Handle with a handler that does progress updates every n milliseconds.
      action.handleWith(this, updateF)(PotAction.handler(FiniteDuration(100, TimeUnit.MILLISECONDS)))
  }
}

// Application circuit
object SPACircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  // initial application model
  override protected def initialModel = RootModel(Empty, Empty, Empty, Empty, Empty)
  // combine all handlers into one
  override protected val actionHandler = composeHandlers(
    new UserHandler(zoomRW(_.user)((m, v) => m.copy(user = v))),
    new MagazineHandler(zoomRW(_.magazines)((m, v) => m.copy(magazines = v))),
    new SubscriptionHandler(zoomRW(_.subscriptions)((m, v) => m.copy(subscriptions = v))),
    new TodoHandler(zoomRW(_.todos)((m, v) => m.copy(todos = v))),
    new MotdHandler(zoomRW(_.motd)((m, v) => m.copy(motd = v)))
  )
}