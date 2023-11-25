import org.slf4j.LoggerFactory
import config_manager.ConfigManager

object Main {
  def main(args: Array[String]): Unit = {
    ConfigManager.overrideWithArgs(args)
    val config = ConfigManager.getConfig
    val logger = LoggerFactory.getLogger(getClass)
    val numberOfIterations =config.getInt("params.numberOfIterations")
    val analysisOutputDir =config.getString("params.analysisOutputDir").stripSuffix("/")

    logger.info("Starting client!")

    val resultsMap = api_client.Play.runForIterations(numberOfIterations)
    save_yaml.SaveAsYaml.saveMapAsYaml(resultsMap, "program_output.yaml")

    logger.info(s"Output saved in file $analysisOutputDir/program_output.yaml")
  }
}