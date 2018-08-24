package kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.AccountCreationObject;
import org.junit.Test;

import java.io.IOException;

public class TestObjectMapper {

    @Test
    public void testObjectMapper() throws IOException {


        String sb = "{\"applicationId\":\"123\",\"requestId\":\"123\",\"memberIds\":[\"1\",\"2\"]}";

        ObjectMapper mapper = new ObjectMapper();

        Object obj = mapper.readValue(sb, AccountCreationObject.class);

        System.out.println(obj);
    }
}
