package actors;

import akka.actor.UntypedActor;
import datatype.Req;
import datatype.Resp;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sunweicong on 15-11-29.
 */
@Slf4j
public class Worker extends UntypedActor{

    private static final AtomicInteger id = new AtomicInteger(1);

    @Override
    public void preStart() throws Exception {
        super.preStart();

        log.warn(" worker start, id={}", id.getAndIncrement());

    }

    @Override
    public void postStop() throws Exception {
        super.postStop();

        log.warn("worker stop");

    }


    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof Req){
            Thread.sleep(2000);
            Req req = (Req)message;

            try{


                if(req.getId() % 10 == 0 ){
                    throw new  RuntimeException(" req.id failed =" +  req.getId());
                }
                getSender().tell(new Resp(((Req)message).getId()), getSelf());
                log.info("req:{}, done", message);
            }catch(Throwable t){
                log.error("req:{} handle failed,", message, t);
                getSender().tell(new Error(String.valueOf(req.getId())), getSender());
            }

        }

    }
}
