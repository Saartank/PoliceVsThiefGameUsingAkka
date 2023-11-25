package graph_utils

import aws_utils.AWSUtils.getS3File
import io.circe.generic.auto._
import io.circe.parser._

import java.nio.file.{Files, Paths}

case class SerializedGraph(adjList: Map[Int, List[Int]], nodeParameters: Map[Int, (Double, Boolean)])

object LoadGraph{
  def loadGraph(path: String): SerializedGraph = {
    try {
      val jsonString = if (path.toLowerCase.contains("s3")) getS3File(path) else new String(Files.readAllBytes(Paths.get(path)))
      decode[SerializedGraph](jsonString).getOrElse(SerializedGraph(Map.empty, Map.empty))
    } catch {
      case _: Throwable => SerializedGraph(Map.empty, Map.empty)
    }
  }
}


