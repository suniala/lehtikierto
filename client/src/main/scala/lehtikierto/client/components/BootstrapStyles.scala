package lehtikierto.client.components

import japgolly.univeq.UnivEq
import lehtikierto.client.components.Bootstrap.CommonStyle
import lehtikierto.client.CssSettings._
import scalacss.internal.mutable
import lehtikierto.client.components.Bootstrap.CommonStyle._

class BootstrapStyles(implicit r: mutable.Register) extends StyleSheet.Inline()(r) {

  import dsl._

  implicit val styleUnivEq: UnivEq[CommonStyle.Value] = new UnivEq[CommonStyle.Value] {}

  val csDomain: Domain[Bootstrap.CommonStyle.Value] = Domain.ofValues(default, primary, success, info, warning, danger)

  val contextDomain: Domain[Bootstrap.CommonStyle.Value] = Domain.ofValues(success, info, warning, danger)

  def commonStyle[A: UnivEq](domain: Domain[A], base: String): A => StyleA = styleF(domain)(opt =>
    styleS(addClassNames(base, s"$base-$opt"))
  )

  def styleWrap(classNames: String*) = style(addClassNames(classNames: _*))

  val buttonOpt: Bootstrap.CommonStyle.Value => StyleA = commonStyle(csDomain, "btn")

  val button = buttonOpt(default)

  val panelOpt: Bootstrap.CommonStyle.Value => StyleA = commonStyle(csDomain, "panel")

  val panel = panelOpt(default)

  val labelOpt: Bootstrap.CommonStyle.Value => StyleA = commonStyle(csDomain, "label")

  val label = labelOpt(default)

  val alert: Bootstrap.CommonStyle.Value => StyleA = commonStyle(contextDomain, "alert")

  val panelHeading: StyleA = styleWrap("panel-heading")

  val panelBody: StyleA = styleWrap("panel-body")

  // wrap styles in a namespace, assign to val to prevent lazy initialization
  object modal {
    val modal: StyleA = styleWrap("modal")
    val fade: StyleA = styleWrap("fade")
    val dialog: StyleA = styleWrap("modal-dialog")
    val content: StyleA = styleWrap("modal-content")
    val header: StyleA = styleWrap("modal-header")
    val body: StyleA = styleWrap("modal-body")
    val footer: StyleA = styleWrap("modal-footer")
  }

  val _modal: modal.type = modal

  object listGroup {
    val listGroup: StyleA = styleWrap("list-group")
    val item: StyleA = styleWrap("list-group-item")
    val itemOpt: Bootstrap.CommonStyle.Value => StyleA = commonStyle(contextDomain, "list-group-item")
  }

  val _listGroup: listGroup.type = listGroup
  val pullRight: StyleA = styleWrap("pull-right")
  val buttonXS: StyleA = styleWrap("btn-xs")
  val close: StyleA = styleWrap("close")

  val labelAsBadge = style(addClassName("label-as-badge"), borderRadius(1.em))

  val navbar: StyleA = styleWrap("nav", "navbar-nav")
  val navbarRight: StyleA = styleWrap("navbar-right")

  val formGroup: StyleA = styleWrap("form-group")
  val formControl: StyleA = styleWrap("form-control")
}
