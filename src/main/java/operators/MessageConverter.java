package operators;

import akka.japi.Option;
import akka.japi.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.AccountCreationObject;

import java.util.Optional;


public class MessageConverter implements Function<String, AccountCreationObject> {

    private transient ObjectMapper objectMapper;

    public MessageConverter() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AccountCreationObject apply(String message) throws Exception {
       return this.objectMapper.readValue(message, AccountCreationObject.class);

    }
}
