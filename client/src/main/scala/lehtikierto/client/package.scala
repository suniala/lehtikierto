package lehtikierto
import scalacss.defaults.Exports
import scalacss.internal.mutable.Settings

package object client {

  val CssSettings: Exports with Settings = scalacss.devOrProdDefaults

}
