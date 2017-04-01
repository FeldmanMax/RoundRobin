package resolvers

trait Resolver {
	def resolve(command: String) : Option[String]
}
