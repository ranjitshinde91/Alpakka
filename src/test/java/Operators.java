import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.common.EntityStreamingSupport;
import akka.http.javadsl.common.JsonEntityStreamingSupport;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.japi.function.Function;
import akka.protobuf.ByteString;
import akka.stream.ActorMaterializer;
import akka.stream.Graph;
import akka.stream.SourceShape;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.SubSource;
import model.DemoGraphicInformationMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Operators {
    final ActorSystem system = ActorSystem.create();
    final ActorMaterializer materializer = ActorMaterializer.create(system);


    @Test
    public void testHttpCall(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://jbxgn47x88.execute-api.ap-south-1.amazonaws.com/dev/getdemographics")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }



    @Test
    public void testForEachSink(){
        Source<Integer, NotUsed> source = Source.range(1, 100);

        source.runForeach(element->System.out.println(element), materializer);

        source.runWith(Sink.foreach(element -> System.out.println(element)),materializer);

    }

    @Test
    public void testFlatConcat(){
        Source<String, NotUsed> source = Source.from(Arrays.asList("ABC", "S", "GSHFSRT"));

        source.mapConcat(new Function<String, Iterable<String>>() {
            @Override
            public Iterable<String> apply(String param) throws Exception {
                List<String> list = new ArrayList<String>();
                list.add("A");
                list.add("B");

                return list;
            }
        }).runForeach(element->System.out.println(element), materializer);

    }

    @Test
    public void testGrouped(){
        Source<String, NotUsed> source = Source.from(Arrays.asList("ABC", "S", "GSHFSRT"));

        source.grouped(2).runForeach(element->System.out.println(element), materializer);

    }


    @Test
    public  void testFlatMapMerge(){
        Source<String, NotUsed> source = Source.from(Arrays.asList("ABC", "S", "GSHFSRT"));

//        SubSource<String, NotUsed> streams = source.groupBy(10, new Function<String, Object>() {
//            @Override
//            public Object apply(String param) throws Exception {
//                return param;
//            }
//        }).flatMapMerge(1, new Function<String, Graph<SourceShape<String>, ? extends Object>>() {
//            @Override
//            public Graph<SourceShape<String>, String> apply(String param) throws Exception {
//                return null;
//            }
//        });

        Source.from(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                .groupBy(3, elem -> elem % 3)
                .map(element -> {System.out.println(element);return  element;})
                .mergeSubstreams()
                .runWith(Sink.foreach(element -> System.out.println(element)),materializer);


    }

    @Test
    public  void testThrottle() throws InterruptedException {
        Source.from(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                .throttle(1, Duration.ofSeconds(1))
                .map(element -> {System.out.println(element + "-"+Thread.currentThread().getId());return  element;})
                .runWith(Sink.foreach(element -> System.out.println(element)),materializer);
        Thread.sleep(200000);
    }
    

}
