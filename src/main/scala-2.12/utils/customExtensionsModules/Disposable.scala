package utils.customExtensionsModules

trait Disposable[S] {
	def dispose()

	implicit def usingCloseToDispose[S](what : S { def close() }) : Disposable[S] = new Disposable[S] {
		def dispose() {
			try {
				what.close
			}
			catch {
				case t => ()
			}
		}
	}

	implicit def usingDisponseToDispose[S](what : S { def dispose() }) : Disposable[S] = new Disposable[S] {
		def dispose() {
			what.dispose
		}
	}

	def using[S <% Disposable[S], T](what : S)(block : S => T) : T = {
		try {
			block(what)
		}
		finally {
			what.dispose
		}
	}
}
