package actors;

import actors.Worker;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;
import datatype.*;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sunweicong on 15-11-27.
 */

@Slf4j
public class Master extends UntypedActor {

    private final ActorRef workerRouter;
    private final ActorRef producer;

    private final static int MAX_LOAD = 30;

    private int currentActiveItemCount = 0;

    private int totalItemCount = 0;

    private int completedItemCount = 0;

    private boolean producerDone = false;

    public Master(){

        this.workerRouter = getContext().actorOf(new RoundRobinPool(10).props(Props.create(Worker.class)));
        this.producer = getContext().actorOf(Props.create(Producer.class));


    }

    public void start(){
        producer.tell(new FetchData(MAX_LOAD), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {


        if(message instanceof StartFlag){
            start();
        }
        else if ( message instanceof Reqs) {
            Reqs reqs = (Reqs) message;
            if(reqs.isEmpty()){
                log.info("producer has been done. total items={}", totalItemCount);
                producerDone = true;
                // stop
            }else {
                for (Req req : reqs) {
                    this.workerRouter.tell(req, getSelf());
                    currentActiveItemCount++;
                    totalItemCount++;
                }
            }

        }else if(message instanceof Resp){
            this.completedItemCount++;
            this.currentActiveItemCount--;
            this.getSelf().tell(new Continue(), getSelf());

        }else if(message instanceof Continue){
            if(!producerDone){
                int diff = MAX_LOAD - currentActiveItemCount;
                if(diff > 10){
                    this.producer.tell(new FetchData(MAX_LOAD - currentActiveItemCount), getSelf());
                }
            }else if(completedItemCount == totalItemCount){
                log.info("worker has been done. completed item cnt={}", completedItemCount);
                getSelf().tell(new StopMaster(), getSelf());
            }

        }else if(message instanceof StopMaster){
            log.info("shut down master");
            getContext().system().shutdown();
            
        }else if(message instanceof  Error){
            log.error("failed", (Error)message);
            currentActiveItemCount--;
            completedItemCount++;
            this.self().tell(new Continue(), getSelf());
        }
    }

}
