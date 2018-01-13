package utils

import java.util.concurrent.ForkJoinPool

import scala.concurrent.ExecutionContext

object ImplicitExecutionContext {
  implicit val RoundRobinExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool(100))
}
