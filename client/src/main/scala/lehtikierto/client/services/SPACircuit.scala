package lehtikierto.client.services

import java.util.concurrent.TimeUnit

import autowire._
import diode._
import diode.data._
import diode.react.ReactConnector
import diode.util._
import lehtikierto.shared._

import scala.concurrent.duration.FiniteDuration
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

// Actions
case object FetchUser extends Action
case class ReceiveUser(user: Option[User]) extends Action

case class UpdateMagazines(potResult: Pot[Seq[Magazine]] = Empty) extends PotAction[Seq[Magazine], UpdateMagazines] {
  override def next(value: Pot[Seq[Magazine]]) = UpdateMagazines(value)
}

case class UpdateSubscriptions(potResult: Pot[Seq[Subscription]] = Empty) extends PotAction[Seq[Subscription], UpdateSubscriptions] {
  override def next(value: Pot[Seq[Subscription]]) = UpdateSubscriptions(value)
}
case class AddSubscription(id: MagazineId) extends Action
case class DeleteSubscription(id: SubscriptionId) extends Action

case class UpdateShares(potResult: Pot[Seq[Share]] = Empty) extends PotAction[Seq[Share], UpdateShares] {
  override def next(value: Pot[Seq[Share]]) = UpdateShares(value)
}
case class AddShare(number: Number) extends Action

case class RootModel(user: Pot[User], magazines: Pot[Seq[Magazine]], subscriptions: Pot[Seq[Subscription]], shares: Pot[Seq[Share]])

class UserHandler[M](modelRW: ModelRW[M, Pot[User]]) extends ActionHandler(modelRW) {
  override def handle: PartialFunction[Any, ActionResult[M]] = {
    case FetchUser =>
      effectOnly(Effect(AjaxClient[Api].getUser().call().map(ReceiveUser)))
    case ReceiveUser(user) =>
      updated(user match {
        case Some(u) => Ready(u)
        case _ => Empty
      })
  }
}

class MagazineHandler[M](modelRW: ModelRW[M, Pot[Seq[Magazine]]]) extends ActionHandler(modelRW) {
  implicit val runner: RunAfterJS = new RunAfterJS

  override def handle: PartialFunction[Any, ActionResult[M]] = {
    case action: UpdateMagazines =>
      val updateF = action.effect(AjaxClient[Api].getAllMagazines().call())(identity)
      // Handle with a handler that does progress updates every n milliseconds.
      action.handleWith(this, updateF)(PotAction.handler(FiniteDuration(100, TimeUnit.MILLISECONDS)))
  }
}

class SubscriptionHandler[M](modelRW: ModelRW[M, Pot[Seq[Subscription]]]) extends ActionHandler(modelRW) {
  implicit val runner: RunAfterJS = new RunAfterJS

  override def handle: PartialFunction[Any, ActionResult[M]] = {
    case action: UpdateSubscriptions =>
      val updateF = action.effect(AjaxClient[Api].getSubscriptions().call())(identity)
      // Handle with a handler that does progress updates every n milliseconds.
      action.handleWith(this, updateF)(PotAction.handler(FiniteDuration(100, TimeUnit.MILLISECONDS)))
    case AddSubscription(magazineId) =>
      effectOnly(Effect(AjaxClient[Api].addSubscription(magazineId).call().map(_ => UpdateSubscriptions())))
    case DeleteSubscription(id) =>
      effectOnly(Effect(AjaxClient[Api].unsubscribe(id).call().map(_ => UpdateSubscriptions())))
  }
}

class ShareHandler[M](modelRW: ModelRW[M, Pot[Seq[Share]]]) extends ActionHandler(modelRW) {
  implicit val runner: RunAfterJS = new RunAfterJS

  override def handle: PartialFunction[Any, ActionResult[M]] = {
    case action: UpdateShares =>
      val updateF = action.effect(AjaxClient[Api].getShares().call())(identity)
      // Handle with a handler that does progress updates every n milliseconds.
      action.handleWith(this, updateF)(PotAction.handler(FiniteDuration(100, TimeUnit.MILLISECONDS)))
    case AddShare(number) =>
      effectOnly(Effect(AjaxClient[Api].addShare(number).call().map(_ => UpdateShares())))
  }
}

// Application circuit
object SPACircuit extends Circuit[RootModel] with ReactConnector[RootModel] {
  // initial application model
  override protected def initialModel = RootModel(Empty, Empty, Empty, Empty)
  // combine all handlers into one
  override protected val actionHandler: SPACircuit.HandlerFunction = composeHandlers(
    new UserHandler(zoomRW(_.user)((m, v) => m.copy(user = v))),
    new MagazineHandler(zoomRW(_.magazines)((m, v) => m.copy(magazines = v))),
    new SubscriptionHandler(zoomRW(_.subscriptions)((m, v) => m.copy(subscriptions = v))),
    new ShareHandler(zoomRW(_.shares)((m, v) => m.copy(shares = v)))
  )
}