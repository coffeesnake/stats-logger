package slogger.services.processing.history

import slogger.services.processing.extraction.DbProvider
import slogger.model.specification.SpecsBundle
import scala.concurrent.Future
import reactivemongo.api.collections.default.BSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import slogger.model.BsonHandlers._
import reactivemongo.bson.BSONDocument
import java.util.UUID
import slogger.model.processing.CalculationResult


trait CalculationResultDao {
  
  def findByBundle(specs: SpecsBundle): Future[Option[CalculationResult]] = {
    findById(specs.id).map { _.flatMap { rec =>
      if (rec.bundle.equalsIgnoreTime(specs)) { 
        Some(rec)
      } else {
        None
      }
    }}
  }

  def save(statsResult: CalculationResult): Future[Unit]
  
  def findById(id: UUID): Future[Option[CalculationResult]]
}


class CalculationResultDaoMongo(dbProvider: DbProvider) extends CalculationResultDao {
  
  val collection: BSONCollection = dbProvider.db.collection("calculationResults")  
      
  override def save(calcRez: CalculationResult): Future[Unit] = collection.save(calcRez).map(foo => Unit)  
  
  def findOne(query: BSONDocument): Future[Option[CalculationResult]] = collection.find(query).cursor[CalculationResult].headOption
  
  override def findById(entityId: UUID): Future[Option[CalculationResult]] = findOne(BSONDocument("_id" -> entityId))
  
}