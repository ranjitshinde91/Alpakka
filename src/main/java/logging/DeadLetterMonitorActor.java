package logging;

import akka.actor.*;

public class DeadLetterMonitorActor extends AbstractActor {

    public static Props props() {
        return Props.create(DeadLetterMonitorActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(DeadLetter.class, message -> System.out.println("Received message "+ message))
                .build();
    }
}
