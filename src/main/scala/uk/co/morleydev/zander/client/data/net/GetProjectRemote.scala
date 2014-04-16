package uk.co.morleydev.zander.client.data.net

import uk.co.morleydev.zander.client.model.arg.Compiler.Compiler
import uk.co.morleydev.zander.client.model.net.ProjectDto
import java.net.URL
import scala.concurrent.{ExecutionContext, Future}
import com.lambdaworks.jacks.JacksMapper
import com.stackmob.newman.request.HttpRequest
import com.stackmob.newman.ApacheHttpClient
import com.stackmob.newman.dsl.GET
import com.stackmob.newman.response.HttpResponseCode
import uk.co.morleydev.zander.client.data.exceptions.ProjectNotFoundException
import uk.co.morleydev.zander.client.model.arg.Project
import uk.co.morleydev.zander.client.util.Log
import uk.co.morleydev.zander.client.data.GetProject
import java.lang

private object GetProjectRemoteDefaultDsl extends (URL => HttpRequest) {
  def apply(url : URL) : HttpRequest = {
    implicit val httpClient = new ApacheHttpClient
    GET(url).toRequest
  }
}

class GetProjectRemote(url : URL,
                       get : (URL => HttpRequest) = GetProjectRemoteDefaultDsl,
                       implicit val executionContext : ExecutionContext = ExecutionContext.global)
  extends GetProject {
  override def apply(projectName: Project, compiler: Compiler): Future[ProjectDto] = {
    val targetUrl = new URL(url, "/" + projectName + "/" + compiler.toString)
    get(targetUrl).apply
      .transform(response => {
      Log("Request to", targetUrl.toString, "returned", response.code)
      if (response.code != HttpResponseCode.Ok)
        throw new ProjectNotFoundException
      else
        response.bodyString
    },
        exception => throw new ProjectNotFoundException)
      .map(json => JacksMapper.readValue[ProjectDto](json))
  }
}
