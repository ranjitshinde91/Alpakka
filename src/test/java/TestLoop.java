import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

public class TestLoop {

    final ActorSystem system = ActorSystem.create();
    final ActorMaterializer materializer = ActorMaterializer.create(system);

    @Test
    public void test(){

        Source<String, NotUsed> source = Source.from(Arrays.asList("1", "2"));

        Flow<String, String, NotUsed> flow = Flow.of(String.class).map(elem -> "Hello " + elem);

        Sink<String, CompletionStage<Done>> sink = Sink.foreach(System.out::println);

        RunnableGraph<NotUsed> workflow = source
                .via(flow)
                .to(sink);


        workflow.run(materializer);


    }
}
