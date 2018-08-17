package operators;


import akka.japi.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.AccountCreationObject;
import model.DemoGraphicInformationMessage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DemographicEnrichmentService implements Function<AccountCreationObject, DemoGraphicInformationMessage> {

    private ObjectMapper objectMapper;
    private String url = "https://jbxgn47x88.execute-api.ap-south-1.amazonaws.com/dev/getdemographics";
    private OkHttpClient client;

    public DemographicEnrichmentService(){
        this.objectMapper = new ObjectMapper();
        this.client =  new OkHttpClient();
    }


    @Override
    public DemoGraphicInformationMessage apply(AccountCreationObject param) throws Exception {

       try {

           Request request = new Request.Builder()
                   .url(url)
                   .build();

           Response response = client.newCall(request).execute();
           return this.objectMapper.readValue(response.body().string(), DemoGraphicInformationMessage.class);
       }
       catch (Exception e){
           System.out.println(e.getMessage());
       }
       return null;
    }
}
