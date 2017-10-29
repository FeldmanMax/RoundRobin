package wrappers

case class RoundRobinResult[TResult](status: Int, result: Option[TResult]) {
}
