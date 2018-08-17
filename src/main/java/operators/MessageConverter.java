package operators;

import akka.japi.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.AccountCreationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageConverter implements Function<String, AccountCreationObject> {
    Logger logger = LoggerFactory.getLogger(AccountCreationObject.class);

    private transient ObjectMapper objectMapper;

    public MessageConverter() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AccountCreationObject apply(String message) throws Exception {
        try{
            return this.objectMapper.readValue(message, AccountCreationObject.class);
        }
       catch (Exception e){
            logger.error("Error converting message "+e.getMessage());
       }
       return null;
    }
}
