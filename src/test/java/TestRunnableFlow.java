import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

public class TestRunnableFlow {

    final ActorSystem system = ActorSystem.create();
    final ActorMaterializer materializer = ActorMaterializer.create(system);


    @Test
    public void testRunnableFlow1(){

            final Source<Integer, NotUsed> source = Source.from(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

            // note that the Future is scala.concurrent.Future
            final Sink<Integer, CompletionStage<Integer>> sink = Sink.<Integer, Integer> fold(0, (aggr, next) -> aggr + next);

             // connect the Source to the Sink, obtaining a RunnableFlow
            final RunnableGraph<CompletionStage<Integer>> runnable = source.toMat(sink, Keep.right());

             final CompletionStage<Integer> sum = runnable.run(materializer);
    }


}
