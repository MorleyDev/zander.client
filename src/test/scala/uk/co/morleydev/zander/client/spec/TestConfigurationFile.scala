package uk.co.morleydev.zander.client.spec

import java.io.{FileOutputStream, File}
import com.lambdaworks.jacks.JacksMapper
import uk.co.morleydev.zander.client.util.Using.using
import uk.co.morleydev.zander.client.model.Configuration

class TestConfigurationFile(configuration : Configuration) extends AutoCloseable {

  private def createConfigFile(filename : String) : File = {
    val configString = JacksMapper.writeValueAsString(configuration)
    val configFile = new File(filename)
    configFile.createNewFile()

    using(new FileOutputStream(configFile)) {
      file => file.write(configString.getBytes("UTF-8"))
    }
    configFile
  }

  val file = createConfigFile("test_config.json")

  def close(): Unit = {
    file.delete()
  }
}