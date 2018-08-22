import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.*;
import akka.stream.javadsl.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class Experiment {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("QuickStart");

        ActorMaterializerSettings matSettings  = ActorMaterializerSettings.create(system).withDebugLogging(true);

        ActorMaterializer mat = ActorMaterializer.create(matSettings, system);

        Flow<Integer, Integer, NotUsed> printFlow  = Flow.of(Integer.class).map( s -> {
            System.out.println("Processing: " + s);
            return s;
        });

        Flow<Integer, Integer, NotUsed> completePrintFlow  = Flow.of(Integer.class).map( (Integer s) -> {
            System.out.println("Completed: " + s);
            return s;
        });

        Sink<Object, CompletionStage<Done>> ignore = Sink.ignore();
        List<Integer> numbers = Arrays.asList(3, 3);

        RunnableGraph<CompletionStage<Done>> mainGraph = RunnableGraph.fromGraph(GraphDSL.create(ignore, (b, out) -> {
            SourceShape<Integer> s = b.add(Source.from(numbers));
            FlowShape<Integer, Integer> printer = b.add(printFlow);
            FlowShape<Integer, Integer> completePrinter = b.add(completePrintFlow);
            UniformFanOutShape<Integer, Integer> partition = b.add(Partition.create(2, (Integer x) -> (x == 0) ? 0 : 1 ));
            FlowShape<Integer, Integer> decrement = b.add(Flow.of(Integer.class).map( (x) -> x - 1));

            // MergedPreferredShape seems only to be in scaladsl
            UniformFanInShape<Integer, Integer> mergePreferred = b.add(MergePreferred.create(1));

            // s ~> mergedPreferred.in(0)                           ~> partition ~> completePrinter ~> out
            //      mergedPreferred.preferred <~ decFlow <~ printer <~ partition

            b.from(s).toInlet(mergePreferred.in(0));
            b.from(mergePreferred.out()).viaFanOut(partition);
            b.from(partition.out(0)).via(completePrinter).to(out);
            b.to(mergePreferred.in(1)).via(decrement).via(printer).fromFanOut(partition);

            return ClosedShape.getInstance();
        }));
        mainGraph.run(mat).whenComplete((out, error) -> {
            system.terminate();
        });
    }
}