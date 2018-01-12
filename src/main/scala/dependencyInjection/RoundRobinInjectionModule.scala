package dependencyInjection

import cache.ConnectionsCache
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule
import repositories._
import services.{ConfigurationService, ConnectionService, PointsService, WeightService}
import utils.FileSystemService

class RoundRobinInjectionModule extends AbstractModule with ScalaModule {
  def configure(): Unit = {
    bindConfigurationService
    bindWeightService
    bindConnectionService
  }

  private def bindWeightService = {
    bind(classOf[PointsService]).annotatedWith(Names.named("points_service")).to(classOf[PointsService])
    bind(classOf[WeightRepository]).annotatedWith(Names.named("weight_repository")).toInstance(new InMemoryWeightRepository(ConnectionsCache.weightCache))
    bind(classOf[WeightService]).annotatedWith(Names.named("weight_service")).to(classOf[WeightService])
  }

  private def bindConfigurationService = {
    bind[ConfigurationRepository].annotatedWith(Names.named("config_repository")).toInstance(new FileConfigurationRepository(new FileSystemService))
    bind(classOf[ConfigurationService]).annotatedWith(Names.named("config_service")).to(classOf[ConfigurationService])
  }

  private def bindConnectionService = {
    bind(classOf[ConnectionRepository]).annotatedWith(Names.named("connection_repository")).toInstance(new ConnectionRepository(ConnectionsCache.connectionCache))
    bind(classOf[ConnectionService]).annotatedWith(Names.named("connection_service")).to(classOf[ConnectionService])
  }
}
