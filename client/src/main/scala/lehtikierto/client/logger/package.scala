package lehtikierto.client

package object logger {
  private val defaultLogger = LoggerFactory.getLogger("Log")

  def log: Logger = defaultLogger
}
