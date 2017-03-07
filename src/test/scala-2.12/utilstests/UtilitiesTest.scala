package utilstests

import org.scalatest.FunSuite

class UtilitiesTest extends FunSuite {
	test("tryCatch - Normal Request") {
		import utils.Utilities.WrapperCommands
		val wrapperCommands: WrapperCommands = new WrapperCommands {}
		val action = () => 1
		val exception = (ex: Exception) => throw ex

		val result: Int = wrapperCommands.tryCatch(action, exception)
		assert(result == 1)
	}

	test("tryCatch - Result is Int, Response is String") {
		import utils.Utilities.WrapperCommands
		val wrapperCommands: WrapperCommands = new WrapperCommands {}
		val action = () => 1
		val toResponse = (int: Int) => int.toString
		val exception = (ex: Exception) => throw ex

		val result: String = wrapperCommands.tryCatch(action, exception)(toResponse)
		assert(result == "1")
	}
}
