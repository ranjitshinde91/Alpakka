package operators;


import akka.japi.function.Predicate;
import model.AccountCreationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class MessageFilter implements Predicate<String> {

    Logger logger = LoggerFactory.getLogger(AccountCreationObject.class);

    @Override
    public boolean test(String message) {
        logger.info("Filtering message "+message);
        if(Objects.isNull(message) || "".equals(message)){
            return false;
        }
        return true;
    }
}
