package utils

import utils.customExtensionsModules.Disposable

class Lock extends Disposable[Unit] {
	var available = true

	def acquire() = synchronized {
		while (!available) wait()
		available = false
	}

	def release() = synchronized {
		available = true
		notify()
	}

	override def dispose(): Unit = release()
}
