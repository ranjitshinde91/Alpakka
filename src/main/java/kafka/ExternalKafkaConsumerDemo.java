package kafka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.KafkaConsumerActor;
import akka.kafka.ManualSubscription;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;


public class ExternalKafkaConsumerDemo {

    public static void main(String[] args) {

        final ActorSystem system = ActorSystem.create("QuickStart");
        final Materializer materializer = ActorMaterializer.create(system);

        final Config config = system.settings().config().getConfig("akka.kafka.consumer");

        ConsumerSettings<String, String> consumerSettings =  new RavenConsumerSetting(config);

        System.out.println(consumerSettings);

        ManualSubscription manualSubscription = Subscriptions.assignment(new TopicPartition("test3", 0));

        ActorRef consumer = system.actorOf((KafkaConsumerActor.props(consumerSettings)));

        Source<ConsumerRecord<Object, Object>, Consumer.Control> group1 = Consumer.plainExternalSource(consumer, manualSubscription);

        group1.to(Sink.foreach(it -> System.out.println("Done with " + it)))
                .run(materializer);

    }


}
