import java.io.FileReader
import java.util.{Collections, Date, Optional, Properties}
import org.apache.kafka.clients.producer._
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.apache.kafka.common.errors.TopicExistsException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import scala.util.Try

object Producer extends App {
  val configFileName = "src/main/resources/kafka.config"
  val topicName = "payment-ingestion"
  val MAPPER = new ObjectMapper
  val props = buildProperties(configFileName)
  createTopic(topicName, props)
  val producer = new KafkaProducer[String, JsonNode](props)

  val callback = new Callback {
    override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
      Option(exception) match {
        case Some(err) => println(s"Failed to produce: $err")
        case None =>  println(s"Produced record at $metadata")
      }
    }
  }

  val bufferedSource = scala.io.Source.fromFile("src/main/resources/payments_incoming.csv")
  for (line <- bufferedSource.getLines) {
    val cols = line.split(",").map(_.trim)
    println(s"${cols(0)}|${cols(1)}|${cols(2)}")
    val paymentRecord: PaymentRecord =
      new PaymentRecord("PAY-" + cols(0), cols(1), getDate(cols(2)))
    val key: String = "PAY-" + cols(0)
    val value: JsonNode = MAPPER.valueToTree(paymentRecord)
    val record = new ProducerRecord[String, JsonNode](topicName, key, value)
    producer.send(record, callback)
  }
  bufferedSource.close

  producer.flush()
  producer.close()
  println("Wrote ten records to " + topicName)

  def buildProperties(configFileName: String): Properties = {
    val properties: Properties = new Properties
    properties.load(new FileReader(configFileName))
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonSerializer")
    properties
  }

  def createTopic(topic: String, clusterConfig: Properties): Unit = {
    val newTopic = new NewTopic(topic, Optional.empty[Integer](), Optional.empty[java.lang.Short]());
    val adminClient = AdminClient.create(clusterConfig)
    Try (adminClient.createTopics(Collections.singletonList(newTopic)).all.get).recover {
      case e :Exception =>
        // Ignore if TopicExistsException, which may be valid if topic exists
        if (!e.getCause.isInstanceOf[TopicExistsException]) throw new RuntimeException(e)
    }
    adminClient.close()
  }

  def getDate(dateString: String): Date = {
    import java.text.SimpleDateFormat
    import java.util.Locale
    val formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
    val date = formatter.parse(dateString)
    date
  }
}
