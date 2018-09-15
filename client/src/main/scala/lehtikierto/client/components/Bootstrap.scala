package lehtikierto.client.components

import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.vdom.html_<^._

import scala.language.implicitConversions
import scala.scalajs.js
import scalacss.ScalaCssReact._
import lehtikierto.client.CssSettings._

/**
 * Common Bootstrap components for scalajs-react
 */
object Bootstrap {

  // shorthand for styles
  @inline private def bss = GlobalStyles.bootstrapStyles

  @js.native
  trait BootstrapJQuery extends JQuery {
    def modal(action: String): BootstrapJQuery = js.native
    def modal(options: js.Any): BootstrapJQuery = js.native
  }

  implicit def jq2bootstrap(jq: JQuery): BootstrapJQuery = jq.asInstanceOf[BootstrapJQuery]

  // Common Bootstrap contextual styles
  object CommonStyle extends Enumeration {
    val default, primary, success, info, warning, danger = Value
  }

  object Button {

    case class Props(onClick: Callback, style: CommonStyle.Value = CommonStyle.default, addStyles: Seq[StyleA] = Seq())

    private val component = ScalaComponent.builder[Props]("Button")
      .renderPC((_, p, c) =>
        <.button(bss.buttonOpt(p.style), p.addStyles.toTagMod, ^.tpe := "button", ^.onClick --> p.onClick, c)
      ).build

    def apply(props: Props, children: VdomNode*): Unmounted[Props, Unit, Unit] = component(props)(children: _*)
    //noinspection TypeAnnotation
    def apply() = component
  }

  object Panel {

    case class Props(heading: String, style: CommonStyle.Value = CommonStyle.default)

    private val component = ScalaComponent.builder[Props]("Panel")
      .renderPC((_, p, c) =>
        <.div(bss.panelOpt(p.style),
          <.div(bss.panelHeading, p.heading),
          <.div(bss.panelBody, c)
        )
      ).build

    def apply(props: Props, children: VdomNode*): Unmounted[Props, Unit, Unit] = component(props)(children: _*)
    //noinspection TypeAnnotation
    def apply() = component
  }

  object Modal {

    // header and footer are functions, so that they can get access to the the hide() function for their buttons
    case class Props(header: Callback => VdomNode, footer: Callback => VdomNode, closed: Callback, backdrop: Boolean = true,
                     keyboard: Boolean = true)

    class Backend(t: BackendScope[Props, Unit]) {
      private def hide =
        // instruct Bootstrap to hide the modal
        t.getDOMNode.map(jQuery(_).modal("hide")).void

      // jQuery event handler to be fired when the modal has been hidden
      def hidden(e: JQueryEventObject): js.Any = {
        // inform the owner of the component that the modal was closed/hidden
        t.props.flatMap(_.closed).runNow()
      }

      def render(p: Props, c: PropsChildren): VdomElement = {
        val modalStyle = bss.modal
        <.div(modalStyle.modal, modalStyle.fade, ^.role := "dialog", ^.aria.hidden := true,
          <.div(modalStyle.dialog,
            <.div(modalStyle.content,
              <.div(modalStyle.header, p.header(hide)),
              <.div(modalStyle.body, c),
              <.div(modalStyle.footer, p.footer(hide))
            )
          )
        )
      }
    }

    private val component = ScalaComponent.builder[Props]("Modal")
      .renderBackendWithChildren[Backend]
      .componentDidMount(scope => Callback {
        val p = scope.props
        // instruct Bootstrap to show the modal
        jQuery(scope.getDOMNode).modal(js.Dynamic.literal("backdrop" -> p.backdrop, "keyboard" -> p.keyboard, "show" -> true))
        // register event listener to be notified when the modal is closed
        jQuery(scope.getDOMNode).on("hidden.bs.modal", null, null, scope.backend.hidden _)
      })
      .build

    def apply(props: Props, children: VdomElement*): Unmounted[Props, Unit, Backend] = component(props)(children: _*)
    //noinspection TypeAnnotation
    def apply() = component
  }

}
