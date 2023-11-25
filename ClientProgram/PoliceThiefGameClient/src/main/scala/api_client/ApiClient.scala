package api_client

import sttp.client3._
import io.circe.generic.auto._
import io.circe.parser._
import config_manager.ConfigManager
import org.slf4j.LoggerFactory

object ApiClient {
  val config = ConfigManager.getConfig
  val logger = LoggerFactory.getLogger(getClass)

  private val backend = HttpURLConnectionBackend()
  private val apiUrl = config.getString("params.apiUrl").stripSuffix("/")

  /**
   * Function is used to reset the game
   * @return returns the status of the game
   */
  def gameReset(): StatusResponse = {
    val response = basicRequest.get(uri"$apiUrl/reset").send(backend)

    response.body match {
      case Left(error) =>
        throw new Exception(s"HTTP error: $error")
      case Right(data) =>
        decode[StatusResponse](data) match {
          case Right(parsedData) => parsedData
          case Left(error) =>
            throw new Exception(s"JSON parsing error: ${error.getMessage}")
        }
    }
  }

  /**
   * This function makes the move for Thief/Police
   * @param who Thief/Police
   * @param node Node for next move
   * @return
   */
  def move(who: String, node: Int): StatusResponse = {
    Thread.sleep(3)
    val reqPath = s"$apiUrl/move/$who/$node"
    val response = basicRequest.post(uri"$reqPath").send(backend)

    response.body match {
      case Left(error) =>
        throw new Exception(s"HTTP error: $error")
      case Right(data) =>
        decode[StatusResponse](data) match {
          case Right(parsedData) => parsedData
          case Left(error) =>
            throw new Exception(s"JSON parsing error: ${error.getMessage}")
        }
    }
  }


}
