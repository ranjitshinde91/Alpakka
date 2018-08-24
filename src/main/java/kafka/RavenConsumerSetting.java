package kafka;

import akka.kafka.ConsumerSettings;
import com.typesafe.config.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import scala.Option;
import scala.collection.immutable.HashMap;
import scala.collection.immutable.Map;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;


class RavenConsumerSetting<K,V> extends ConsumerSettings<K,V> {

    public RavenConsumerSetting(Map<String, String> properties, Option<Deserializer<K>> keyDeserializerOpt, Option<Deserializer<V>> valueDeserializerOpt, FiniteDuration pollInterval, FiniteDuration pollTimeout, FiniteDuration stopTimeout, FiniteDuration closeTimeout, FiniteDuration commitTimeout, FiniteDuration wakeupTimeout, int maxWakeups, Duration commitRefreshInterval, String dispatcher, FiniteDuration commitTimeWarning, boolean wakeupDebug, FiniteDuration waitClosePartition) {
        super(properties, keyDeserializerOpt, valueDeserializerOpt, pollInterval, pollTimeout, stopTimeout, closeTimeout, commitTimeout, wakeupTimeout, maxWakeups, commitRefreshInterval, dispatcher, commitTimeWarning, wakeupDebug, waitClosePartition);
    }

    public RavenConsumerSetting(Config config){
        super(new HashMap<String, String>(),null,null,FiniteDuration.create(10, TimeUnit.SECONDS),FiniteDuration.create(10, TimeUnit.SECONDS),FiniteDuration.create(10, TimeUnit.SECONDS),FiniteDuration.create(10, TimeUnit.SECONDS),FiniteDuration.create(10, TimeUnit.SECONDS),
                FiniteDuration.create(10, TimeUnit.SECONDS), 0,FiniteDuration.create(10, TimeUnit.SECONDS),"akka.kafka.default-dispatcher",
                FiniteDuration.create(10, TimeUnit.SECONDS),false,null);
    }



    @Override
    public org.apache.kafka.clients.consumer.Consumer<K, V> createKafkaConsumer() {
        java.util.HashMap config = new java.util.HashMap ();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "group3");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        KafkaConsumer consumer = new KafkaConsumer(config);
        return consumer;
    }


}