import org.slf4j.LoggerFactory
import config_manager.ConfigManager

object Main {
  private val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    ConfigManager.overrideWithArgs(args)
    val config = ConfigManager.getConfig
    val serverPort = config.getInt("network.serverPort")
    logger.info(s"Starting game service! say hello at endpoint /hello on port $serverPort.")
    game.GameService.startGameService()
  }
}