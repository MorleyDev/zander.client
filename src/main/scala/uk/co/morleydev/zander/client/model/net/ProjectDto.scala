package uk.co.morleydev.zander.client.model.net

import com.fasterxml.jackson.annotation.JsonProperty

case class ProjectVcsDto(@JsonProperty("vcs") vcs : String,
                         @JsonProperty("href") href : String) {
}


case class ProjectDto(@JsonProperty("src") src : ProjectVcsDto) {
}
