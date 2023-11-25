import graph_utils.GraphUtils.{findClosestValuableNodeDistance, findDistance, getTwoCommonNodes}
import org.slf4j.LoggerFactory
import graph_utils.SimRank.simRank
import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.mutable

class AllTests extends AnyFlatSpec {
  val logger = LoggerFactory.getLogger(getClass)

  val originalGraph: Map[Int, List[Int]] =
    Map(
      1 -> List(2, 3),
      2 -> List(1, 3, 4),
      3 -> List(1, 2),
      4 -> List(2)
    )

  val perturbedGraph: Map[Int, List[Int]] =
    Map(
      2 -> List(3, 4),
      3 -> List(2),
      4 -> List(2),
      5 -> List(3)
    )

  val originalNodes: Map[Int, (Double, Boolean)] = Map(
    1 -> (0.5, false),
    2 -> (0.6, false),
    3 -> (0.4, false),
    4 -> (0.3, true)
  )

  val perturbedNodes: Map[Int, (Double, Boolean)] = Map(
    2 -> (0.01, false),
    3 -> (0.7, false),
    4 -> (0.3, true),
    5 -> (0.2, true)
  )

  val g = graph_utils.SerializedGraph(originalGraph, originalNodes)
  val pg = graph_utils.SerializedGraph(perturbedGraph, perturbedNodes)

  "simRank" should "calculate sim rank between nodes of the 2 graphs" in {
    val memo = mutable.Map[(Int, Int, Int), Double]()
    val simRankData = simRank(pg, g, memo)
    assert(simRankData(4)==(4,1))
    assert(simRankData(3)._2>0.1) //modified node correctly recognized
    assert(simRankData(2)._2>0.1) //modified node correctly recognized
    assert(simRankData(5)._2<0.1) //added node correctly recognized
  }

  "simRank" should "return matching node and sim-score for each node in the graph" in {
    val memo = mutable.Map[(Int, Int, Int), Double]()
    val simRankData = simRank(pg, g, memo)
    assert(simRankData.keySet == pg.adjList.keySet)
  }

  "getTwoCommonNodes" should "output 2 distinct common nodes in the 2 graphs" in {
    val (n1, n2) = getTwoCommonNodes(originalGraph, perturbedGraph)
    assert(n1!=n2)
    assert(originalGraph.contains(n1) && perturbedGraph.contains(n1))
    assert(originalGraph.contains(n2) && perturbedGraph.contains(n2))
  }

  "findDistance" should "return correct distance between two connected nodes" in {
    assertResult(2)(findDistance(originalGraph, 1, 4))
  }

  it should "return 0 when the nodes are the same" in {
    assertResult(0)(findDistance(originalGraph, 1, 1))
  }

  it should "return -1 for nodes not in graph" in {
    assertResult(-1)(findDistance(originalGraph, 1, 5))
  }

  "findClosestValuableNodeDistance" should "return find the closest valuable node and the distance to it" in {
    val valuableNodes = g.nodeParameters.collect {
      case (key, (_, true)) => key
    }.toList

    val (dist, node) = findClosestValuableNodeDistance(g.adjList, 1, valuableNodes)
    assert(node.contains(4))
    assert(dist==2)
  }


}
