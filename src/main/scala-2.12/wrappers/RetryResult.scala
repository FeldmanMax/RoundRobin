package wrappers

case class RetryResult[TResult](isSuccess: Boolean, result: Option[TResult]) {}
