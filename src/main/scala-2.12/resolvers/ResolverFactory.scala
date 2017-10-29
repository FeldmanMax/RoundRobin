package resolvers

object ResolverFactory {
	def get(name: String) : Resolver = {
		name match {
			case "Http" => new HttpResolver()
			case _ => throw new Exception(s"$name Resolver does not exist")
		}
	}
}
