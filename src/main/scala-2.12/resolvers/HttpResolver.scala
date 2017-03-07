package resolvers

import utils.StringUtils
import utils.httpUtils.HttpRequestWrapper

class HttpResolver[TResult](val connectionTimeout: Int,
                            val commandTimeout: Int){
	//extends Resolver[TResult]
	//with HttpRequestWrapper {

//	override def resolve[String, Map[String, String]](data: String, params: Map[String, String]): Option[TResult] = {
//		getByJson(StringUtils.replace(data, params), connectionTimeout, commandTimeout)
//	}
}
