package utilstests

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class SomeTest extends FunSuite {
	test("Some Test") {

	}

	def sdsd(count: Int): Unit = {
		val first: String = "simon"
		val second: String = "claudio"
		val list: List[String] = List("sarah claudio", "sarah paul", "claudio simon")

	}

	def buildTree(list: List[String], tree: Option[Branch[String]]): Tree[String] = {
		if(list.isEmpty)  tree.get
		else {
			val pair: List[String] = list.head.split(" ").toList
			val manager: String = pair.head
			val employee: String = pair.last
			var tempTree: Option[Branch[String]] = None
			if(tree.isEmpty) {
				tempTree = Option(new Branch(Option(new Branch(None, None, pair.last)), None, pair.head))
				buildTree(list.tail, tempTree)
			}
			else {
				val rightSide: Option[Branch[String]] = tree.get.le
				val leftSide: Option[Branch[String]] = tree.get.right
				if(rightSide.isEmpty && tree.get.value == manager) {
					rightSide
				}
//				if(tree.get.right.isEmpty && tree.get.right.get.asInstanceOf[Branch[String]].value == manager) {
//					tempTree = Option(Branch(None, None, employee))
//
//				}
			}
			tempTree.get
		}
	}
}

sealed trait Tree[+A]
class Branch[A](left: Option[Tree[A]], right: Option[Tree[A]], value: A) extends Tree[A]
