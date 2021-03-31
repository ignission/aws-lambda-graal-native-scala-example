package example

sealed trait AppError

case class AwsRequestFailed(reason: String)                  extends AppError
case class AwsResponseFailed(reason: String)                 extends AppError
case class RequestIdNotDefined(headers: Map[String, String]) extends AppError
case class GitHubRequestFailed(reason: String)               extends AppError
