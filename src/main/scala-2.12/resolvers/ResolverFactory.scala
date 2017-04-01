package resolvers

object ResolverFactory {
	def get(name: String) : Resolver = {
		name match {
			case "Http" => new HttpResolver(100, 100)
			case _ => throw new Exception(s"$name Resolver does not exist")
		}
	}
}
