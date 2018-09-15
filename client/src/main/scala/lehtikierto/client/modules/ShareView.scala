package lehtikierto.client.modules

import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react.{CtorType, _}
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.components.Bootstrap._
import lehtikierto.client.components._
import lehtikierto.client.logger._
import lehtikierto.client.services._
import lehtikierto.shared._
import scalacss.ScalaCssReact._
import diode.NoAction
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.component.builder.Builder.Step1
import org.scalajs.dom.html.Div

object ShareView {
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(proxy: ModelProxy[Pot[Seq[Magazine]]])

  case class State(magazine: Option[Magazine], year: Option[Int], number: Option[String])

  trait Phase {
    def isDone(state: State): Boolean
  }

  private val magazinePhase = new Phase() {
    def isDone(state: State): Boolean = state.magazine.isDefined
  }
  private val yearPhase = new Phase() {
    def isDone(state: State): Boolean = state.year.isDefined
  }
  private val numberPhase = new Phase() {
    def isDone(state: State): Boolean = state.number.isDefined
  }
  val phases = Seq(magazinePhase, yearPhase, numberPhase)

  class Backend($: BackendScope[Props, State]) {
    def mounted(props: Props): Callback =
      Callback.when(props.proxy().isEmpty)(props.proxy.dispatchCB(UpdateMagazines()))

    def render(p: Props, s: State): VdomElement = {
      val proxy = p.proxy
      val years = Seq(2018, 2017) // TODO: calculate appropriate years
      val phaseResolver = (phase: Phase) => phase.isDone(s)

      def renderItem(item: Magazine): VdomElement = {
        <.li(
          bss.listGroup.item,
          <.span(item.name),
          Button(Button.Props($.modState(s => s.copy(magazine = Some(item))), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Valitse"))
      }

      def renderYear(year: Int) = {
        <.li(
          bss.listGroup.item,
          <.span(year),
          Button(Button.Props($.modState(s => s.copy(year = Some(year))), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Valitse"))
      }

      <.div(
        SmartPanel(
          SmartPanel.Props(phaseResolver, phases, magazinePhase, s.magazine.fold("Mikä lehti?")(_.name)),
          proxy().renderPending(_ => <.p("Ladataan...")),
          proxy().renderFailed(_ => <.p("Lehtien lataaminen epäonnistui!")),
          proxy().renderReady(m => <.ul(bss.listGroup.listGroup)(m.toTagMod(renderItem)))),
        SmartPanel(
          SmartPanel.Props(phaseResolver, phases, yearPhase, s.year.fold("Mikä vuosi?")(_.toString())),
          <.ul(bss.listGroup.listGroup)(years.toTagMod(renderYear))),
        SmartPanel(
          SmartPanel.Props(phaseResolver, phases, numberPhase, s.number.fold("Mikä numero?")((s: String) => s)),
          <.div("lomake tähän...")))
    }
  }

  def showIfGiven(option: Option[_]): VdomNode => VdomNode =
    (child: VdomNode) => option match {
      case Some(_) => child
      case _       => VdomArray.empty()
    }

  private val component = ScalaComponent.builder[Props]("ShareView")
    .initialState(State(None, None, None))
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[Pot[Seq[Magazine]]]): Unmounted[Props, State, Backend] = component(Props(proxy))
}

object SmartPanel {
  case class Props(phaseResolver: ShareView.Phase => Boolean, phases: Seq[ShareView.Phase], curr: ShareView.Phase, heading: String, style: CommonStyle.Value = CommonStyle.default)

  private val component = ScalaComponent.builder[Props]("SmartPanel")
    .renderPC((_, p, c) => {
      val allPreviousPhasesDefined = p.phases.takeWhile(_ != p.curr).forall(p.phaseResolver(_))
      val isCurrDefined = p.phaseResolver(p.curr)
      
      Panel(
        Panel.Props(p.heading, p.style),
        if (allPreviousPhasesDefined && !isCurrDefined) c
        else VdomArray.empty())
    })
    .build

  def apply(props: Props, children: VdomNode*): Unmounted[Props, Unit, Unit] = component(props)(children: _*)
  //noinspection TypeAnnotation
  def apply() = component
}
