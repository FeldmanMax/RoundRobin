package modules

import configuration.ConnectionActionsElement
import data_modules.{Endpoints, RoundRobinDTO}
import resolvers.Resolver
import utils.loggin.Log
import utils.{Lock, StringUtils}
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

class ConnectionResultCache(val resolver: Resolver,
                            val destFunc: () => RoundRobinDTO,
                            val reloadEvery: Int,
                            val actions: ConnectionActionsElement,
                            val endpointsGenerator: EndpointsGenerator,
                            val region: String) {

	private val _lock = new Lock()
	private var resolvedEndpoints: Option[EndpointsContainer] = None

	def hasCachedEndpoints: Boolean = resolvedEndpoints.nonEmpty

	def getEndpoints: Option[EndpointsContainer] = {
		_lock.acquire()
		if (resolvedEndpoints.isEmpty)
			reload()
		_lock.release()
		resolvedEndpoints
	}

	private def reload() = {
		resolvedEndpoints = reloadImpl()
		val reloadMethodWrapper = Future {
			while(true){
				val reloadMethod: Future[Option[EndpointsContainer]] = Future {
					reloadImpl()
				}
				reloadMethod.onComplete {
					case Success(value) => replace(value)
					case Failure(ex) => Log.exception("reload method", ex)
				}
				Thread.sleep(reloadEvery)
			}
		}
		reloadMethodWrapper.onComplete(_ => Log.exception("reload process has ended!"))
	}

	private def reloadImpl(): Option[EndpointsContainer] = {
		actions.actionResolver match {
			case "Http" =>
				val next: RoundRobinDTO = destFunc()
				val response = resolver.resolve(StringUtils.replace(next.destination, actions.mappedParams), next.connectionTimeout, next.commandTimeout)
				val container: EndpointsContainer = generateEndpointsContainer(next, response)
				Some(container)
		}
	}

	private def generateEndpointsContainer(next: RoundRobinDTO, response: Option[String]): EndpointsContainer = {
		val endpoints: Endpoints = endpointsGenerator.generateEndpoints(next.connectionName, region, getSplittedResult(response))
		new EndpointsContainer(Map(region -> endpoints))
	}

	private def getSplittedResult(result: Option[String]): List[String] = {
		result match {
			case Some(v) => v.split(",").toList
			case _ => List.empty
		}
	}

	private def replace(other: Option[EndpointsContainer]) = {
		_lock.acquire()
		resolvedEndpoints = other
		_lock.release()
	}
}
