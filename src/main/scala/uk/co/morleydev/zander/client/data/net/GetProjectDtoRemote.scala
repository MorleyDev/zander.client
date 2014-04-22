package uk.co.morleydev.zander.client.data.net

import uk.co.morleydev.zander.client.model.arg.BuildCompiler.BuildCompiler
import uk.co.morleydev.zander.client.model.net.ProjectDto
import java.net.URL
import scala.concurrent.{ExecutionContext, Future}
import com.lambdaworks.jacks.JacksMapper
import com.stackmob.newman.request.HttpRequest
import com.stackmob.newman.ApacheHttpClient
import com.stackmob.newman.dsl.GET
import com.stackmob.newman.response.HttpResponseCode
import uk.co.morleydev.zander.client.data.exception.ProjectEndpointNotFoundException
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.util.Log
import uk.co.morleydev.zander.client.data.GetProjectDto
import java.lang

private object GetProjectRemoteDefaultDsl extends (URL => HttpRequest) {
  def apply(url : URL) : HttpRequest = {
    implicit val httpClient = new ApacheHttpClient
    GET(url).toRequest
  }
}

class GetProjectDtoRemote(url : URL,
                       get : (URL => HttpRequest) = GetProjectRemoteDefaultDsl,
                       implicit val executionContext : ExecutionContext = ExecutionContext.global)
  extends GetProjectDto {
  override def apply(projectName: Project, compiler: BuildCompiler): Future[ProjectDto] = {
    val targetUrl = new URL(url, "/" + projectName + "/" + compiler.toString)
    get(targetUrl).apply
      .transform(response => {
      Log.message("Request to %s returned %s".format(targetUrl, response.code))
      if (response.code != HttpResponseCode.Ok)
        throw new ProjectEndpointNotFoundException
      else
        response.bodyString
    },
        exception => new ProjectEndpointNotFoundException)
      .map(json => JacksMapper.readValue[ProjectDto](json))
  }
}
