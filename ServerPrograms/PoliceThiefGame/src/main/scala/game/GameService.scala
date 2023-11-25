package game

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import org.slf4j.LoggerFactory
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.{AskPattern, Behaviors}
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.http.scaladsl.{Http, server}
import akka.http.scaladsl.model.{MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller, ToResponseMarshaller}
import akka.util.Timeout
import io.circe.generic.auto._
import io.circe.syntax._
import GameActor._
import config_manager.ConfigManager
import graph_utils.{LoadGraph, SimRank}
import graph_utils.GraphUtils.getTwoCommonNodes

/**
 * This Object runs the API service on the specified port and interface
 */
object GameService {
  private val logger = LoggerFactory.getLogger(getClass)
  private val config = ConfigManager.getConfig
  private val originalGraphPath = config.getString("locations.originalGraph").stripSuffix("/")
  private val perturbedGraphPath = config.getString("locations.perturbedGraph").stripSuffix("/")
  private val serverPort = config.getInt("network.serverPort")
  private val bindInterface = config.getString("network.bindInterface")


  implicit val marshallerStatusResponseEntity: ToEntityMarshaller[Response] =
    Marshaller.StringMarshaller.wrap(MediaTypes.`application/json`) { map: Response =>
      map.asJson.noSpaces
    }

  /**
   * This function starts the game service at the specified port and interface
   */
  def startGameService(): Unit = {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "GameSystem")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    implicit val timeout: Timeout = 3.seconds

    val g = LoadGraph.loadGraph(originalGraphPath)
    val pg = LoadGraph.loadGraph(perturbedGraphPath)
    logger.info(s"Graph successfully loaded, got ${g.nodeParameters.size} nodes in original graph!")

    val startNodes = getTwoCommonNodes(g.adjList, pg.adjList)
    val valuableNodes =  List(g.nodeParameters.collect {
      case (key, (_, true)) => key
    }.toList.head)
    val memo = mutable.Map[(Int, Int, Int), Double]()
    val simRankData = SimRank.simRank(pg, g, memo)

    val keysMissing = pg.adjList.keySet -- simRankData.keySet

    val filteredSimRankData = simRankData.filter {
      case (_, (_, doubleValue)) => doubleValue < 1
    }
    val gameActor = system.systemActorOf(GameActor(g.adjList, pg.adjList, valuableNodes, simRankData, startNodePolice = startNodes._1, startNodeThief = startNodes._2), "gameActor")

    val route : server.Route = cors(){
      path("hello") {
        get {
          logger.info("Received request for /hello")
          complete("Hello there, how are you doing?")
        }
      } ~
        pathPrefix("move") {
          path("police" / IntNumber) { node =>
            post {
              logger.info(s"Received request for /police/$node")
              onSuccess(gameActor.ask(MovePolice(node, _))) {
                response: Response =>
                  complete(StatusCodes.OK, response)
              }
            }
          } ~
            path("thief" / IntNumber) { node =>
              post {
                logger.info(s"Received request for /thief/$node")
                onSuccess(gameActor.ask(MoveThief(node, _))) {
                  response: Response =>
                    complete(StatusCodes.OK, response)
                }
              }
            }
        } ~
        pathPrefix("status") {
          get {
            logger.info(s"Received request for /status")
            onSuccess(gameActor.ask(Status)) {
              response: Response =>
                complete(StatusCodes.OK, response)
            }
          }
        } ~
        pathPrefix("reset") {
          get {
            logger.info(s"Received request for /reset")
            onSuccess(gameActor.ask(Reset)){
              response: Response =>
                complete(StatusCodes.OK, response)
            }
          }
        }
    }
    val bindingFuture = Http().newServerAt(bindInterface, serverPort).bind(route)

    println(s"Server online at port $serverPort\n")
  }
}

