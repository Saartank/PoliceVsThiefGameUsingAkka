package graph_utils

import scala.collection.mutable

object SimRank {

  val C = 0.9
  val ITERATIONS = 3

  /**
   *
   * @param graph Graph for whose nodes SimRank is calculated
   * @param reference_graph Reference graph which is used for comparison
   * @param memo Cache which is used for memoization
   * @return
   */
  def simRank(graph: SerializedGraph, reference_graph: SerializedGraph, memo: mutable.Map[(Int, Int, Int), Double]): Map[Int, (Int, Double)] = {

    def calculateSimilarity(a: Int, b: Int, iteration: Int): Double = {

      if (iteration == 0) return 0.0
      if (graph.nodeParameters(a)._1 == reference_graph.nodeParameters(b)._1) return 1.0
      memo.get((a, b, iteration)).orElse(memo.get((b, a, iteration))) match {
        case Some(result) => return result
        case None =>
      }
      val neighborsA = graph.adjList.getOrElse(a, List())
      val neighborsB = reference_graph.adjList.getOrElse(b, List())

      if (neighborsA.isEmpty || neighborsB.isEmpty) return 0.0
      val similarities = for {
        neighborA <- neighborsA
        neighborB <- neighborsB
      } yield calculateSimilarity(neighborA, neighborB, iteration - 1)

      val result = C * similarities.sum / (neighborsA.size * neighborsB.size)
      memo += ((a, b, iteration) -> result)
      result
    }

    val results = for {
      nodeA <- graph.adjList.keys
    } yield {
      val similarities = for {
        nodeB <- reference_graph.adjList.keys
      } yield (nodeB, calculateSimilarity(nodeA, nodeB, ITERATIONS))

      val maxSimilarity = similarities.maxBy(_._2)
      (nodeA, maxSimilarity)
    }

    results.toMap
  }

}

