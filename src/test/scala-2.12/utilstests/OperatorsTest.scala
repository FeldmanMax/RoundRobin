package utilstests

import org.scalatest.FunSuite

class OperatorsTest extends FunSuite {
	import utils.Operators.OperatorsExtensions

	test("if (true) false else true"){
		val isTrue: Boolean = false
		val isFalse: Boolean = true
		assert {
			!(true ? (isTrue, isFalse))
		}
	}

	test("if (false) func1 else func2 when func1 throws exception if executed"){
		val isTrue = () => throw new Exception("hahaha")
		val isFalse = () => "All Good"

		assert((false ? (isTrue, isFalse)) == "All Good")
		try{
			true ? (isTrue, isFalse)
			assert(false)
		}
		catch{
			case ex: Exception => assert(true)
		}
	}

	test("if (true) func1 else true when func1 returns false") {
		val isTrue = () => false

		assert(!(true ? (isTrue, true)))
		assert(false ? (isTrue, true))
	}

	test("if (true) false else func2 when func2 returns true") {
		val isFalse = () => false

		assert(!(true ? (false, isFalse)))
	}
}
