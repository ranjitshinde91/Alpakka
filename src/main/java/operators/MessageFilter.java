package operators;


import akka.event.Logging;
import akka.japi.function.Predicate;
import model.AccountCreationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


public class MessageFilter implements Predicate<String> {


    @Override
    public boolean test(String message) {
        System.out.println("Filtering message "+message);
        if(Objects.isNull(message) || "".equals(message)){
            return false;
        }
        return true;
    }
}
