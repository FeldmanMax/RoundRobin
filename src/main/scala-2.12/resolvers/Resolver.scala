package resolvers

trait Resolver {
	def resolve(command: String, connectionTimeout: Int, commandTimeout: Int) : Option[String]
}
