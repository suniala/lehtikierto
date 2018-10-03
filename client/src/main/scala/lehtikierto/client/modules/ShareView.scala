package lehtikierto.client.modules

import diode.data.Pot
import diode.react.ReactPot._
import diode.react._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._
import lehtikierto.client.Routes
import lehtikierto.client.Routes.DashboardLoc
import lehtikierto.client.components.Bootstrap._
import lehtikierto.client.components._
import lehtikierto.client.services._
import lehtikierto.shared._
import scalacss.ScalaCssReact._

object ShareView {
  @inline private def bss = GlobalStyles.bootstrapStyles

  case class Props(proxy: ModelProxy[Pot[Seq[Magazine]]])

  case class State(magazine: Option[Magazine], year: Option[Int], number: Option[String], editNumber: Option[String])

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

      def phaseEnabled(phase: Phase): Boolean = {
        val areAllPreviousPhasesDone = phases.takeWhile(_ != phase).forall(_.isDone(s))
        val isThisPhaseDone = phase.isDone(s)
        areAllPreviousPhasesDone && !isThisPhaseDone
      }

      def renderItem(item: Magazine): VdomElement = {
        <.li(
          bss.listGroup.item,
          <.span(item.name),
          Button(Button.Props($.modState(_.copy(magazine = Some(item))), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Valitse"))
      }

      def renderYear(year: Int) = {
        <.li(
          bss.listGroup.item,
          <.span(year),
          Button(Button.Props($.modState(_.copy(year = Some(year))), addStyles = Seq(bss.pullRight, bss.buttonXS)), "Valitse"))
      }

      def updateNumber(e: ReactEventFromInput): Callback = {
        val text = e.target.value
        $.modState(_.copy(editNumber = Option(text)))
      }

      val submitNumberCB = $.modState(_.copy(number = s.editNumber, editNumber = None))

      val submitCB = Callback(proxy.dispatchNow(AddShare(Number(None, s.magazine.get, s.year.get, s.number.get)))) >>
        Routes.go(DashboardLoc)

      <.div(
        PhasePanel(
          PhasePanel.Props(phaseEnabled(magazinePhase), s.magazine.fold("Mikä lehti?")(_.name)),
          proxy().renderPending(_ => <.p("Ladataan...")),
          proxy().renderFailed(_ => <.p("Lehtien lataaminen epäonnistui!")),
          proxy().renderReady(m => <.ul(bss.listGroup.listGroup)(m.toTagMod(renderItem)))),
        PhasePanel(
          PhasePanel.Props(phaseEnabled(yearPhase), s.year.fold("Mikä vuosi?")(_.toString())),
          <.ul(bss.listGroup.listGroup)(years.toTagMod(renderYear))),
        PhasePanel(
          PhasePanel.Props(phaseEnabled(numberPhase), s.number.fold("Mikä numero?")((s: String) => s)),
          <.div(bss.formGroup,
            <.label(^.`for` := "number", "Lehden numero"),
            <.input.text(bss.formControl, ^.id := "number", ^.value := s.editNumber.getOrElse(""),
              ^.placeholder := "Kirjoita lehden numero tähän", ^.onChange ==> updateNumber)),
            <.div(bss.pullRight,
              <.span(Button(Button.Props(submitNumberCB, disabled = s.editNumber.isEmpty), "Valmis")))),
          <.div(bss.pullRight,
            <.span(^.className := "button-spacing", Routes.link(DashboardLoc)("Peruuta")),
            <.span(Button(Button.Props(submitCB, disabled = s.number.isEmpty), "Jaa lehti")))
      )
    }
  }

  def showIfGiven(option: Option[_]): VdomNode => VdomNode =
    (child: VdomNode) => option match {
      case Some(_) => child
      case _       => VdomArray.empty()
    }

  private val component = ScalaComponent.builder[Props]("ShareView")
    .initialState(State(None, None, None, None))
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[Pot[Seq[Magazine]]]): Unmounted[Props, State, Backend] = component(Props(proxy))
}

object PhasePanel {
  case class Props(enabled: Boolean, heading: String, style: CommonStyle.Value = CommonStyle.default)

  private val component = ScalaComponent.builder[Props]("SmartPanel")
    .renderPC((_, p, c) => {
      Panel(
        Panel.Props(p.heading, p.style),
        if (p.enabled) c
        else VdomArray.empty())
    })
    .build

  def apply(props: Props, children: VdomNode*): Unmounted[Props, Unit, Unit] = component(props)(children: _*)
  //noinspection TypeAnnotation
  def apply() = component
}
