package game

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import graph_utils.GraphUtils.{adjacentNodesDataPolice, adjacentNodesDataThief, getTwoCommonNodes}

object GameActor {
  sealed trait Command

  case class Response( message: String, data: (((String, Int), Map[Int, (Int, Int)]), ((String, Int), Map[Int, (Double, Int, Int)])) )


  case class Status(replyTo: ActorRef[Response]) extends Command
  case class MovePolice(node: Int, replyTo: ActorRef[Response]) extends Command
  case class MoveThief(node: Int, replyTo: ActorRef[Response]) extends Command
  case class Reset(replyTo: ActorRef[Response]) extends Command


  def apply(graph: Map[Int, List[Int]], perturbedGraph: Map[Int, List[Int]], valuableNodes: List[Int], simRankData:  Map[Int, (Int, Double)], startNodePolice: Int = 0, startNodeThief: Int = 1, chance: String = "police", winner: String = ""): Behavior[Command] = {
    val resDataDefault = ((("Police", startNodePolice), adjacentNodesDataPolice(graph = graph, police = startNodePolice, thief = startNodeThief, valuableNodes = valuableNodes)),
      (("Thief", startNodeThief), adjacentNodesDataThief(graph = graph, police = startNodePolice, thief = startNodeThief, valuableNodes = valuableNodes, simRankData)))

    def isGameOver:String={

      if (startNodeThief == startNodePolice) {
        return s"Police won! Game over! Please reset."
      } else if (valuableNodes.contains(startNodeThief)) {
        return s"Thief won! Game over! Please reset."
      } else if (winner != "") {
        return s"$winner won! Game over! Please reset."
      }else if(resDataDefault._2._2.isEmpty){
        return s"Police won! Thief got trapped! Game over! Please reset."
      } else {
        return ""
      }
    }

    Behaviors.receiveMessage {
      case Status(replyTo) =>
        val tmp = isGameOver
        if (tmp != ""){
          replyTo ! Response(tmp, resDataDefault)
        }else{
          replyTo ! Response(s"Its $chance's turn!'", resDataDefault)
        }
        Behaviors.same

      case MovePolice(node, replyTo) =>
        val tmp = isGameOver
        if (tmp != "") {
          replyTo ! Response(tmp, resDataDefault)
          Behaviors.same
        }else if(chance!="police"){
          replyTo ! Response("Not your turn! Please wait for the thief to make a move.", resDataDefault)
          Behaviors.same
        }else{
          if (!(graph(startNodePolice).contains(node) || node==startNodePolice)){
            replyTo ! Response("Illegal Move, please choose a valid adjacent node.", resDataDefault)
            Behaviors.same
          }else{
            if (node == startNodeThief){
              val newResData = ((("Police", node), adjacentNodesDataPolice(graph = graph, police = node, thief = startNodeThief, valuableNodes = valuableNodes)),
                (("Thief", startNodeThief), adjacentNodesDataThief(graph = graph, police = node, thief = startNodeThief, valuableNodes = valuableNodes, simRankData)))

              replyTo ! Response(s"Police won! Game over! Please reset.", newResData)
              apply(graph, perturbedGraph, valuableNodes, simRankData, node, startNodeThief, chance = "thief", winner="Police")
            }else{
              val newResData = ((("Police", node), adjacentNodesDataPolice(graph = graph, police = node, thief = startNodeThief, valuableNodes = valuableNodes)),
                (("Thief", startNodeThief), adjacentNodesDataThief(graph = graph, police = node, thief = startNodeThief, valuableNodes = valuableNodes, simRankData)))

              if(!newResData._1._2.keys.exists(key => key != node && !valuableNodes.contains(key))){
                replyTo ! Response(s"Thief won! Police got trapped! Game over! Please reset.", newResData)
                apply(graph, perturbedGraph, valuableNodes, simRankData, node, startNodeThief, chance = "thief", winner="Thief")
              }else{
                replyTo ! Response(s"Police moved to: $node", newResData)
                apply(graph, perturbedGraph, valuableNodes, simRankData, node, startNodeThief, chance = "thief")
              }

            }

          }
        }

      case MoveThief(node, replyTo) =>
        val tmp = isGameOver
        if (tmp != "") {
          replyTo ! Response(tmp, resDataDefault)
          Behaviors.same
        }else if (chance != "thief") {
          replyTo ! Response("Not your turn! Please wait for the police to make a move.", resDataDefault)
          Behaviors.same
        }else {
          if (!graph(startNodeThief).contains(node) && !perturbedGraph(startNodeThief).contains(node) && node!=startNodeThief) {
            replyTo ! Response("Illegal Move, please choose a valid adjacent node.", resDataDefault)
            Behaviors.same
          }else if((!graph(startNodeThief).contains(node) && perturbedGraph(startNodeThief).contains(node)) || (graph(startNodeThief).contains(node) && !perturbedGraph(startNodeThief).contains(node))){
            replyTo ! Response(s"You walked into a police trap. Police won! Game over! Please reset.", resDataDefault)
            apply(graph, perturbedGraph, valuableNodes, simRankData, startNodePolice, startNodeThief, chance = "police", winner="Police")
          }else {

            if(node == startNodePolice){
              val newResData = ((("Police", startNodePolice), adjacentNodesDataPolice(graph = graph, police = startNodePolice, thief = node, valuableNodes = valuableNodes)),
                (("Thief", node), adjacentNodesDataThief(graph = graph, police = startNodePolice, thief = node, valuableNodes = valuableNodes, simRankData)))

              replyTo ! Response(s"Police won! Game over! Please reset.", newResData)
              apply(graph, perturbedGraph, valuableNodes, simRankData, startNodePolice, node, chance = "police")
            }else if(valuableNodes.contains(node)){
              val newResData = ((("Police", startNodePolice), adjacentNodesDataPolice(graph = graph, police = startNodePolice, thief = node, valuableNodes = valuableNodes)),
                (("Thief", node), adjacentNodesDataThief(graph = graph, police = startNodePolice, thief = node, valuableNodes = valuableNodes, simRankData)))

              replyTo ! Response(s"Thief won! Game over! Please reset.", newResData)
              apply(graph, perturbedGraph, valuableNodes, simRankData, startNodePolice, node, chance = "police")
            }else{
              val newResData = ((("Police", startNodePolice), adjacentNodesDataPolice(graph = graph, police = startNodePolice, thief = node, valuableNodes = valuableNodes)),
                (("Thief", node), adjacentNodesDataThief(graph = graph, police = startNodePolice, thief = node, valuableNodes = valuableNodes, simRankData)))

              if (newResData._2._2.isEmpty){
                replyTo ! Response(s"Police won! Thief got trapped! Game over! Please reset.", newResData)
                apply(graph, perturbedGraph, valuableNodes, simRankData, startNodePolice, node, chance = "police", winner="Police")
              }else{
                replyTo ! Response(s"Thief moved to: $node", newResData)
                apply(graph, perturbedGraph, valuableNodes, simRankData, startNodePolice, node, chance = "police")
              }

            }
          }
        }

      case Reset(replyTo) =>
        val startNodes = getTwoCommonNodes(graph, perturbedGraph)
        val newStartNodePolice = startNodes._1
        val newStartNodeThief = startNodes._2
        val resData = ((("Police", newStartNodePolice), adjacentNodesDataPolice(graph = graph, police = newStartNodePolice, thief = newStartNodeThief, valuableNodes = valuableNodes)),
          (("Thief", newStartNodeThief), adjacentNodesDataThief(graph = graph, police = newStartNodePolice, thief = newStartNodeThief, valuableNodes = valuableNodes, simRankData)))

        replyTo ! Response("Game reset!", resData)
        apply(graph, perturbedGraph, valuableNodes, simRankData, newStartNodePolice, newStartNodeThief)
    }
  }
}
