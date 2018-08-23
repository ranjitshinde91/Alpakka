package operators;

import akka.japi.function.Function2;
import model.DemoGraphicInformationMessage;

public class PayloadGenerator implements Function2<DemoGraphicInformationMessage, DemoGraphicInformationMessage, DemoGraphicInformationMessage> {

    @Override
    public DemoGraphicInformationMessage apply(DemoGraphicInformationMessage arg1, DemoGraphicInformationMessage arg2) throws Exception {
         arg2.setFirstName(arg1.getFirstName()+arg2.getFirstName());
         arg2.setLastName(arg1.getLastName()+arg2.getLastName());
         return arg2;
    }
}
