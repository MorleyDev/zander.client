package uk.co.morleydev.zander.client.spec

import java.io.{FileOutputStream, File}
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.util.Using.using
import uk.co.morleydev.zander.client.model.Configuration

class TestConfigurationFile(configuration : Configuration) extends AutoCloseable {

  private def createConfigFile() : File = {
    val configString = JacksMapper.writeValueAsString(configuration)
    val configFile = File.createTempFile("config", ".json")
    configFile.createNewFile()

    using(new FileOutputStream(configFile)) {
      file => file.write(configString.getBytes("UTF-8"))
    }
    configFile
  }

  val file = createConfigFile()

  def close(): Unit = {
    if(!file.delete())
      println("Warning: test failed to delete configFile")
  }
}
