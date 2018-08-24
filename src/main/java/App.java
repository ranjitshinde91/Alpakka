import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.typesafe.config.Config;
import logging.DeadLetterMonitorActor;
import operators.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Map;
import java.util.concurrent.CompletionStage;


public class App {
    private MessageFilter messageFilter;
    private MessageConverter messageConverter;
    private DemographicEnrichmentService demographicEnrichmentService;
    private MessageSplitter messageSplitter;
    private PayloadGenerator payloadGenerator;

    private LoggingAdapter logger;

    public App() {
      this.messageFilter  = new MessageFilter();
      this.messageConverter   = new MessageConverter();
      this.messageSplitter = new MessageSplitter();
      this.demographicEnrichmentService = new DemographicEnrichmentService();
      this.payloadGenerator = new PayloadGenerator();
    }

    public static void main(String args[]){
        new  App().run();
    }

    private void run(){
        final ActorSystem system = ActorSystem.create("QuickStart");

        logger =  Logging.getLogger(system, "customLogger");

        logger.info("starting streaming application");

        ActorRef monitor = system.actorOf(DeadLetterMonitorActor.props());

        system.eventStream().subscribe(monitor, DeadLetter.class);

        final Materializer materializer = ActorMaterializer.create(system);

        final ConsumerSettings<String, byte[]> consumerSettings = getKafkaConsumerSettings(system);

        Source<ConsumerRecord<String, byte[]>, Consumer.Control> source = Consumer
                .atMostOnceSource(consumerSettings, Subscriptions.topics("test4"));

        source.log("custom", logger);

        source .map(message->new String(message.value()))
                                        .filter(this.messageFilter)
                                        .map(this.messageConverter)
                                        .splitWhen(a -> true)
                                            .mapConcat(this.messageSplitter)
                                            .map(this.demographicEnrichmentService)
                                            .reduce(this.payloadGenerator)
                                         .mergeSubstreams()
                                       // .collect(collectMessage)
                                        .to(Sink.foreach(it -> logger.info("Done with " + it)))
                                        .run(materializer);

    }


    private static ConsumerSettings<String, byte[]> getKafkaConsumerSettings(ActorSystem system) {
        final Config config = system.settings().config().getConfig("akka.kafka.consumer");
        return ConsumerSettings.create(config, new StringDeserializer(), new ByteArrayDeserializer())
                .withBootstrapServers("localhost:9092")
                .withGroupId("group5")
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }
}
