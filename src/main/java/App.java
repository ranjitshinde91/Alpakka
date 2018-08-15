
import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.nio.file.Paths;
import java.math.BigInteger;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;


public class App {

    public static void main2(String args[]){

        System.out.println("Hello");

        final ActorSystem system = ActorSystem.create("QuickStart");
        final Materializer materializer = ActorMaterializer.create(system);

        final Source<Integer, NotUsed> source = Source.range(1, 100);

        final CompletionStage<Done> done = source.runForeach(i -> System.out.println(i), materializer);

        done.thenRun(() -> system.terminate());
    }


    public static void main(String args[]){
        final ActorSystem system = ActorSystem.create("QuickStart");
        final Materializer materializer = ActorMaterializer.create(system);

        final Config config = system.settings().config().getConfig("akka.kafka.consumer");

        final ConsumerSettings<String, byte[]> consumerSettings =
                ConsumerSettings.create(config, new StringDeserializer(), new ByteArrayDeserializer())
                        .withBootstrapServers("localhost:9092")
                        .withGroupId("group1")
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer.Control control = Consumer
                        .atMostOnceSource(consumerSettings, Subscriptions.topics("test2"))
                        .map(message->new String(message.value()))
                        .to(Sink.foreach(it -> System.out.println("Done with " + it)))
                        .run(materializer);


    }
}
