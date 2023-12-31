package graph_utils

import scala.collection.mutable
import scala.util.Random

object GraphUtils {

  /**
   * This function finds two common distinct nodes between the 2 graphs.
   * @param g Original graph
   * @param pg Perturbed graph
   * @return
   */
  def getTwoCommonNodes(g: Map[Int, List[Int]], pg: Map[Int, List[Int]]) : (Int, Int) ={
    val commonNodes = g.keySet.intersect(pg.keySet)
    val twoRandomNodes = Random.shuffle(commonNodes.toList).take(2)
    (twoRandomNodes(0), twoRandomNodes(1))
  }

  /**
   * This function returns adjacent node data for the police
   * @param graph Original graph
   * @param police Police node id
   * @param thief Thief node id
   * @param valuableNodes Valuable Node
   * @return
   */
  def adjacentNodesDataPolice(graph: Map[Int, List[Int]], police: Int, thief: Int, valuableNodes: List[Int]): Map[Int, (Int, Int)] = {
    val data = graph.getOrElse(police, Nil).map { node: Int =>
      val distanceToThief = findDistance(graph, node, thief)
      val (closestValuableNodeDistance, closestValuableNode) = findClosestValuableNodeDistance(graph, node, valuableNodes)
      node -> (distanceToThief, closestValuableNodeDistance)
    }.toMap

    val policeDistanceToThief = findDistance(graph, police, thief)
    val (policeClosestValuableNodeDistance, _) = findClosestValuableNodeDistance(graph, police, valuableNodes)

    // Include the police node in the final map
    return (data + (police -> (policeDistanceToThief, policeClosestValuableNodeDistance)))

  }

  /**
   * This function returns adjacent node data for the police
   * @param graph Original graph
   * @param police Police node id
   * @param thief Thief node id
   * @param valuableNodes Valuable Node
   * @param simRankData SimRank data
   * @return
   */
  def adjacentNodesDataThief(graph: Map[Int, List[Int]], police: Int, thief: Int, valuableNodes: List[Int], simRankData: Map[Int, (Int,Double)]): Map[Int, (Double, Int, Int)] = {
    graph.getOrElse(thief, Nil).filter(simRankData.contains).map { node: Int =>
      val distanceFromPolice = findDistance(graph, police, node)
      val (closestValuableNodeDistance, closestValuableNode) = findClosestValuableNodeDistance(graph, node, valuableNodes)
      node -> (simRankData(node)._2 , distanceFromPolice, closestValuableNodeDistance)
    }.toMap
  }

  /**
   * This function calculates distance between 2 nodes in a graph, returns -1 path does not exist
   * @param graph any graph
   * @param node1 start node
   * @param node2 end node
   * @return
   */
  def findDistance(graph: Map[Int, List[Int]], node1: Int, node2: Int): Int = {
    // Check if node1 is not present in the graph for Police traps on perturbed graph
    if (!graph.contains(node1)) {
      return Random.nextInt(4) + 1
    }

    // Initialize a queue for BFS and a set to keep track of visited nodes
    val queue = mutable.Queue(node1)
    val visited = scala.collection.mutable.Set(node1)

    // Initialize a map to store distances from node1 to other nodes
    val distanceMap = scala.collection.mutable.Map(node1 -> 0)

    // Perform BFS
    while (queue.nonEmpty) {
      val currentNode = queue.dequeue()
      val currentDistance = distanceMap(currentNode)

      if (currentNode == node2) {
        return currentDistance
      }

      for (neighbor <- graph.getOrElse(currentNode, Nil)) {
        if (!visited.contains(neighbor)) {
          visited.add(neighbor)
          queue.enqueue(neighbor)
          distanceMap(neighbor) = currentDistance + 1
        }
      }
    }
    // If no path was found between node1 and node2
    -1
  }

  /**
   *
   * @param graph any graph
   * @param nodeId any node
   * @param valuableNodes distance to valuable node
   * @return
   */
  def findClosestValuableNodeDistance(graph: Map[Int, List[Int]], nodeId: Int, valuableNodes: List[Int]): (Int, Option[Int]) = {
    // Create a set to keep track of visited nodes
    val visited = scala.collection.mutable.Set[Int]()

    // Initialize a BFS queue with the starting node and distance
    val queue = mutable.Queue((nodeId, 0))

    var minDistance = -1
    var closestNode: Option[Int] = None

    while (queue.nonEmpty && minDistance == -1) {
      val (currentNode, distance) = queue.dequeue()

      // If the current node is in the valuableNodes list, update minDistance and closestNode
      if (valuableNodes.contains(currentNode)) {
        minDistance = distance
        closestNode = Some(currentNode)
      }

      // Mark the current node as visited
      visited += currentNode

      // Enqueue neighboring nodes that haven't been visited yet
      for (neighbor <- graph.getOrElse(currentNode, Nil) if !visited.contains(neighbor)) {
        queue.enqueue((neighbor, distance + 1))
      }
    }

    (minDistance, closestNode)
  }
}
