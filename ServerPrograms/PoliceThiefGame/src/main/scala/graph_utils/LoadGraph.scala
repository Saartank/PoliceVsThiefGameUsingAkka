package graph_utils

import aws_utils.AWSUtils.getS3File
import io.circe.generic.auto._
import io.circe.parser._

import java.nio.file.{Files, Paths}

/**
 * Class that holds the graph loaded from the json file
 * @param adjList Adjacency list of graph
 * @param nodeParameters Node data
 */
case class SerializedGraph(adjList: Map[Int, List[Int]], nodeParameters: Map[Int, (Double, Boolean)])


object LoadGraph{
  /**
   *
   * @param path Path to the graph JSON file
   * @return Object of instance SerializedGraph
   */
  def loadGraph(path: String): SerializedGraph = {
    try {
      val jsonString = if (path.toLowerCase.contains("s3")) getS3File(path) else new String(Files.readAllBytes(Paths.get(path)))
      decode[SerializedGraph](jsonString).getOrElse(SerializedGraph(Map.empty, Map.empty))
    } catch {
      case _: Throwable => SerializedGraph(Map.empty, Map.empty)
    }
  }
}


