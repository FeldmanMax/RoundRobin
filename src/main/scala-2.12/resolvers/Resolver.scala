package resolvers

trait Resolver[TResult <: AnyRef] {
	def resolve[TData, TParams](data: TData, params: Option[TParams]) : Option[TResult]
}
