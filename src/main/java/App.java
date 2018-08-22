import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.typesafe.config.Config;
import model.DemoGraphicInformationMessage;
import operators.DemographicEnrichmentService;
import operators.MessageConverter;
import operators.MessageFilter;
import operators.MessageSplitter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;



public class App {
    private MessageFilter messageFilter;
    private MessageConverter messageConverter;
    private DemographicEnrichmentService demographicEnrichmentService;
    private MessageSplitter messageSplitter;

    public App() {
      this.messageFilter  = new MessageFilter();
      this.messageConverter   = new MessageConverter();
      this.messageSplitter = new MessageSplitter();
      this.demographicEnrichmentService = new DemographicEnrichmentService();
    }

    public static void main(String args[]){
        new  App().run();
    }

    private void run(){
        final ActorSystem system = ActorSystem.create("QuickStart");


        final Materializer materializer = ActorMaterializer.create(system);

        final ConsumerSettings<String, byte[]> consumerSettings = getKafkaConsumerSettings(system);

        Consumer.Control control = Consumer
                                        .atMostOnceSource(consumerSettings, Subscriptions.topics("test2"))
                                        .map(message->new String(message.value()))
                                        .filter(messageFilter)
                                        .map(messageConverter)
                                        .mapConcat(messageSplitter)
                                        .map(demographicEnrichmentService)
                                       // .collect(collectMessage)
                                        .to(Sink.foreach(it -> System.out.println("Done with " + it)))
                                        .run(materializer);
    }


    private static ConsumerSettings<String, byte[]> getKafkaConsumerSettings(ActorSystem system) {
        final Config config = system.settings().config().getConfig("akka.kafka.consumer");
        return ConsumerSettings.create(config, new StringDeserializer(), new ByteArrayDeserializer())
                .withBootstrapServers("localhost:9092")
                .withGroupId("group1")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }
}
