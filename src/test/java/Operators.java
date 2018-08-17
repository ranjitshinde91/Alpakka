import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

public class Operators {
    final ActorSystem system = ActorSystem.create();
    final ActorMaterializer materializer = ActorMaterializer.create(system);

//    Unmarshaller<ByteString, DemoGraphicInformationMessage> unmarshal = Jackson.byteStringUnmarshaller(DemoGraphicInformationMessage.class);
//    JsonEntityStreamingSupport support = EntityStreamingSupport.json();
//
//    @Test
//    public void restApiCall() throws ExecutionException, InterruptedException {
//
//        final CompletionStage<HttpResponse> responseFuture =
//                    Http.get(system)
//                            .singleRequest(HttpRequest.create("https://jbxgn47x88.execute-api.ap-south-1.amazonaws.com/dev/getdemographics"));
//
//        responseFuture.toCompletableFuture().get().entity().getDataBytes().via(support.framingDecoder()) // apply JSON framing
//                .mapAsync(1,
//                        bs -> unmarshal.unmarshal(bs, materializer)
//                ).runForeach(it -> System.out.println(it), materializer);
//    }


    @Test
    public void testForEachSink(){
        Source<Integer, NotUsed> source = Source.range(1, 100);

        source.runForeach(element->System.out.println(element), materializer);

        source.runWith(Sink.foreach(element -> System.out.println(element)),materializer);

    }

    @Test
    public void testFlatConcat(){
        Source<String, NotUsed> source = Source.from(Arrays.asList("ABC", "S", "GSHFSRT"));

        source.mapConcat(element -> Arrays.asList(element.split("")))
                .runForeach(element->System.out.println(element), materializer);

    }


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

}
