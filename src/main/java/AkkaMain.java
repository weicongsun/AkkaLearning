import actors.Master;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import datatype.StartFlag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.impl.SimpleLogger;

/**
 * Created by sunweicong on 15-11-29.
 */
@Slf4j
public class AkkaMain {

    public static void main(String[] args) {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "WARN");
        ActorSystem actorSystem = ActorSystem.create("Test");

        ActorRef master =
                actorSystem.actorOf(Props.create(Master.class), "master");

        master.tell(new StartFlag(), ActorRef.noSender());

        actorSystem.awaitTermination();

        log.warn("system done");

    }
}
