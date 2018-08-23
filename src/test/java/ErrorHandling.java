import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.sse.ServerSentEvent;
import akka.http.javadsl.unmarshalling.sse.EventStreamUnmarshalling;
import akka.japi.pf.PFBuilder;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.RestartSource;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;

public class ErrorHandling {

    final ActorSystem system = ActorSystem.create();
    final ActorMaterializer materializer = ActorMaterializer.create(system);


    @Test
    public void testErrorLogging(){
        Source.from(Arrays.asList(-1, 0, 1))
                .map(x -> 1 / x) //throwing ArithmeticException: / by zero
                .log("error logging")
                .runForeach(element->System.out.println(element), materializer);
    }


    @Test
    public void testRecover(){
        Source.from(Arrays.asList(0, 1, 2, 3, 4, 5, 6)).map(n -> {
            if (n < 5) return n.toString();
            else throw new RuntimeException("Boom!");
        }).recover(new PFBuilder()
                .match(RuntimeException.class, ex -> "stream truncated")
                .build()
        ).runForeach(System.out::println, materializer);
    }


    @Test
    public void testRecoverWithRetry(){

        Source<String, NotUsed> planB = Source.from(Arrays.asList("five", "six", "seven", "eight"));

        Source.from(Arrays.asList(0, 1, 2, 3, 4, 5, 6)).map(n -> {
            if (n < 5) return n.toString();
            else {System.out.println("Emitting 5");throw new RuntimeException("Boom!");}
        }).recoverWithRetries(
                3, // max attempts
                new PFBuilder()
                        .match(RuntimeException.class, ex -> planB)
                        .build()
        ).runForeach(System.out::println, materializer);
    }

    @Test
    public void testExponentialBackoff() throws InterruptedException {

        Source<ServerSentEvent, NotUsed> eventStream = RestartSource.withBackoff(
                Duration.ofSeconds(3), // min backoff
                Duration.ofSeconds(30), // max backoff
                0.2, // adds 20% "noise" to vary the intervals slightly
                20, // limits the amount of restarts to 20
                () ->
                        // Create a source from a future of a source
                        Source.fromSourceCompletionStage(
                                // Issue a GET request on the event stream
                                Http.get(system).singleRequest(HttpRequest.create("https://jbxgn47x88.execute-api.ap-south-1.amazonaws.com/dev/getdemographics"))
                                        .thenCompose(response ->{
                                                // Unmarshall it to a stream of ServerSentEvents
                                                System.out.println("unmarshalling "+ System.currentTimeMillis());
                                                return EventStreamUnmarshalling.fromEventStream().unmarshal(response.entity(), materializer);
                                                }
                                        )
                        )
        );

        eventStream.runForeach(System.out::println, materializer);

        Thread.sleep(100000);
    }
}
