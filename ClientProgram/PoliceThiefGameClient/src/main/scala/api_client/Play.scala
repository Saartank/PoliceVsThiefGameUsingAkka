package api_client

import api_client.ApiClient.{gameReset, move}
import api_client.Strategies.{naivePolice, naiveThief, smartPolice, smartThief}
import org.slf4j.LoggerFactory

import scala.annotation.tailrec

object Play {
  val logger = LoggerFactory.getLogger(getClass)

  @tailrec
  def play(status: StatusResponse, chance: String, policeStrategy: (StatusResponse) => Int, thiefStrategy: (StatusResponse) => Int): String = {
    if (status.message.toLowerCase.contains("game over")) {
      return status.message
    }
    if (chance.toLowerCase == "police") {
      return play(move("police", policeStrategy(status)), "thief", policeStrategy, thiefStrategy)
    } else {
      return play(move("thief", thiefStrategy(status)), "police", policeStrategy, thiefStrategy)
    }
  }

  def runForIterations(numberOfIterations: Int): Map[String, Map[String, String]] = {

    def withStrategy(policeStrategy: (StatusResponse) => Int, thiefStrategy: (StatusResponse) => Int): (Double, Double) = {
      val (policeWins, thiefWins) = (1 to numberOfIterations).map { i =>
        logger.info(s"Iteration: $i")
        val initStatus = gameReset()
        val result: String = play(initStatus, "police", policeStrategy, thiefStrategy)
        logger.info(result)
        if (result.toLowerCase.contains("police")) "police" else "thief"
      }.foldLeft((0, 0)) { (acc, winner) =>
        winner match {
          case "police" => (acc._1 + 1, acc._2)
          case "thief" => (acc._1, acc._2 + 1)
        }
      }
      return (((policeWins.toDouble / numberOfIterations) * 100), ((thiefWins.toDouble / numberOfIterations) * 100))
    }

    val combinations = List(
      ("Naive police", "Naive thief") -> ((status: StatusResponse) => naivePolice(status), (status: StatusResponse) => naiveThief(status)),
      ("Naive police", "Smart thief") -> ((status: StatusResponse) => naivePolice(status), (status: StatusResponse) => smartThief(status)),
      ("Smart police", "Naive thief") -> ((status: StatusResponse) => smartPolice(status), (status: StatusResponse) => naiveThief(status)),
      ("Smart police", "Smart thief") -> ((status: StatusResponse) => smartPolice(status), (status: StatusResponse) => smartThief(status))
    )

    val resultsMap = combinations.foldLeft(Map[String, Map[String, String]]()) { (currentMap, combination) =>
      val ((policeName, thiefName), (policeFunc, thiefFunc)) = combination
      logger.info(s"Using strategy: ${(policeName, thiefName)}")
      val (policeScore, thiefScore) = withStrategy(policeFunc, thiefFunc)
      val key = s"$policeName, $thiefName"
      currentMap.updated(key, Map("police" -> f"$policeScore%.2f%%", "thief" -> f"$thiefScore%.2f%%"))
    }

    return resultsMap

  }

}
