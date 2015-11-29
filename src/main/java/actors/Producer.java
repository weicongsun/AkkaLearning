package actors;

import akka.actor.UntypedActor;
import datatype.FetchData;
import datatype.Req;
import datatype.Reqs;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sunweicong on 15-11-29.
 */
@Slf4j
public class Producer extends UntypedActor {
    private static AtomicInteger counter = new AtomicInteger(0);
    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof FetchData){
            FetchData data = (FetchData) message;
            int size = data.getSize();
            Reqs reqs = new Reqs();

            if(counter.get() < 30) {
                for (int i = 0; i < size; i++) {
                    if(counter.get()< 30)
                    reqs.add(new Req(counter.getAndIncrement()));

                }

            }

            log.info("send reqs:{}", reqs);

            getSender().tell(reqs, getSelf());
        }

    }
}
