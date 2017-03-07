package utils

package object DataStructure {
	implicit class DataStructureSeqExtenstions[TData](sequance: Seq[TData]){
		import Operators.OperatorsExtensions

		def isEmptyCondition(func: TData => Boolean) : Boolean = ! sequance.exists(func)//sequance.count(func) == 0
		def toListOrDefault[TData](isDistinct: Boolean) : List[TData] = {
			sequance.isEmpty ? (List.empty,
													isDistinct ? (sequance.toList.asInstanceOf[List[TData]].distinct,
																					sequance.toList.asInstanceOf[List[TData]]))
		}

		def toMapConditionted[TKey, TValue](func: (TData) => (TKey,TValue)) : Map[TKey, TValue] = sequance.map(func).toMap
		def isNonEmptyCondition(func: TData => Boolean) : Boolean = !isEmptyCondition(func)
		def filterHead(func: TData => Boolean): Option[TData] = sequance.find(func)
		def addIfNotExist(data: TData, func: (TData, TData) => Boolean) : Seq[TData] = {
			if(sequance.forall(x=>func(x, data))) sequance
			else  data :: sequance.toList
		}
	}
}
