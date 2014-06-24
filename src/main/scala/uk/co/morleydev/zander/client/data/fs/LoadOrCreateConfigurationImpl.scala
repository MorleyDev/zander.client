package uk.co.morleydev.zander.client.data.fs

import java.io.{PrintWriter, File, FileNotFoundException}

import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.data.LoadOrCreateConfiguration
import uk.co.morleydev.zander.client.model.Configuration
import uk.co.morleydev.zander.client.util.{Log, using}

import scala.io.Source

object LoadOrCreateConfigurationImpl extends LoadOrCreateConfiguration {
  def apply(configPath : String) : Configuration = {
    val configJson = try {
      using(Source.fromFile(configPath)) {
        file => file.getLines().mkString("\n")
      }
    } catch {
      case e: FileNotFoundException =>
        Log.warning("Could not open config file %s, using defaults".format(configPath))
        val configJson = JacksMapper.writeValueAsString(new Configuration())
        val configParentPath = new File(configPath).getParentFile
        if (!configParentPath.exists())
          configParentPath.mkdirs()

        try {
          using(new PrintWriter(configPath)) { write => write.write(configJson)}
        } catch { case _: Throwable => }
        configJson
    }
    JacksMapper.readValue[Configuration](configJson)
  }
}
