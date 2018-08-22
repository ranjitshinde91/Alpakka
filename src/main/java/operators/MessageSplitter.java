package operators;

import akka.japi.function.Function;
import model.AccountCreationObject;
import model.MemberMessage;

import java.util.LinkedList;
import java.util.List;

public class MessageSplitter implements Function<AccountCreationObject,Iterable<MemberMessage>> {

    @Override
    public Iterable<MemberMessage> apply(AccountCreationObject aoObject) throws Exception {
        List<MemberMessage> list = new LinkedList<>();

        for(String memberId : aoObject.getMemberIds()){
           MemberMessage memberMessage = new MemberMessage(aoObject.getApplicationId(), aoObject.getRequestId(), memberId);
           list.add(memberMessage);
        }
        return list;
    }
}
