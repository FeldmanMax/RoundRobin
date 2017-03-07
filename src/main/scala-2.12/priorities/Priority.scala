package priorities

import modules.Connection

trait Priority {
	def getConnections(connections: List[Connection]): List[Connection]
}
