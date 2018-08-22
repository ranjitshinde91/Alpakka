import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.common.EntityStreamingSupport;
import akka.http.javadsl.common.JsonEntityStreamingSupport;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.ResponseEntity;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import model.DemoGraphicInformationMessage;
import org.junit.Test;
import scala.compat.java8.FutureConverters;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static java.lang.Thread.*;

public class TestHttp {

    final ActorSystem system = ActorSystem.create();
    final ActorMaterializer materializer = ActorMaterializer.create(system);


    Unmarshaller<ByteString, DemoGraphicInformationMessage> unmarshal = Jackson.byteStringUnmarshaller(DemoGraphicInformationMessage.class);
    JsonEntityStreamingSupport support = EntityStreamingSupport.json();


    @Test
    public void restApiCall() throws ExecutionException, InterruptedException {

        final CompletionStage<HttpResponse> responseFuture =
                Http.get(system)
                        .singleRequest(HttpRequest.create("https://jbxgn47x88.execute-api.ap-south-1.amazonaws.com/dev/getdemographics"));



        Source<Source<CompletionStage<DemoGraphicInformationMessage>, Object>, NotUsed> source = Source.fromCompletionStage(responseFuture)
                .map(response -> response.entity().getDataBytes())
                .map(entity -> {
                    return entity.via(support.framingDecoder())
                            .map(bs -> {
                                CompletionStage<DemoGraphicInformationMessage> value = unmarshal.unmarshal(bs, materializer);
                                return value;
                            });

                });


        source.runForeach(element->System.out.println(element), materializer);


//        System.out.println(responseFuture.toCompletableFuture().get().entity().getDataBytes());

        sleep(10000);
    }


    @Test

    public void testCompletionSource() throws InterruptedException {
        Source<Integer, NotUsed> source = Source.from(Arrays.asList(1, 2, 3, 4, 5));

        final CompletionStage<HttpResponse> responseFuture =
                Http.get(system)
                        .singleRequest(HttpRequest.create("https://jbxgn47x88.execute-api.ap-south-1.amazonaws.com/dev/getdemographics"));


        CompletionStage<ResponseEntity> entity = responseFuture.thenApply(s -> { // 1
            System.out.println(currentThread().getName() + "." + s.entity());
            return s.entity();
        });



        sleep(1000);
    }
}
