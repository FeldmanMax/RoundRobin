package utils

package object Operators {
	implicit class OperatorsExtensions(boolean: Boolean){
		def ?[TResult] (ifTrue: TResult, ifFalse: TResult) : TResult = {
			if(boolean) ifTrue else ifFalse
		}
		def ?[TResult] (func1: () => TResult, func2: () => TResult) : TResult = {
			if(boolean) func1() else func2()
		}
		def ?[TResult] (func1: () => TResult, ifFalse: TResult) : TResult = {
			if(boolean) func1() else ifFalse
		}
		def ?[TResult] (ifTrue: TResult, func2: () => TResult) : TResult = {
			if(boolean) ifTrue else func2()
		}
	}
}
