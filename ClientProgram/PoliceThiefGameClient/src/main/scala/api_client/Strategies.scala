package api_client

import config_manager.ConfigManager

object Strategies {
  val config = ConfigManager.getConfig
  val simRankThresholdForThief = config.getDouble("params.simRankThresholdForThief")

  def naivePolice(status: StatusResponse): Int = {
    status.data._1._2.minBy(_._2._1)._1
  }

  def smartPolice(status: StatusResponse): Int = {
    status.data._1._2.minBy(_._2._2)._1
  }

  def naiveThief(status: StatusResponse): Int = {
    status.data._2._2.minBy(_._2._3)._1
  }

  def smartThief(status: StatusResponse): Int = {
    val dataMap = status.data._2._2
    val filteredDataMap = dataMap.filter { case (_, (d, _, _)) => d > simRankThresholdForThief }
    if (filteredDataMap.nonEmpty){
      filteredDataMap.minBy(_._2._3)._1
    }else{
      dataMap.maxBy(_._2._1)._1
    }
  }
}
