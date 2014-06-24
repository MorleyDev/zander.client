package uk.co.morleydev.zander.client.test.spec

import java.io.{FileOutputStream, File}
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.util.using
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
    file.delete()
  }
}
