
import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

import java.nio.file.Paths;
import java.math.BigInteger;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;


public class App {

    public static void main(String args[]){

        System.out.println("Hello");

        final ActorSystem system = ActorSystem.create("QuickStart");
        final Materializer materializer = ActorMaterializer.create(system);

        final Source<Integer, NotUsed> source = Source.range(1, 100);

        final CompletionStage<Done> done = source.runForeach(i -> System.out.println(i), materializer);

        done.thenRun(() -> system.terminate());
    }
}
