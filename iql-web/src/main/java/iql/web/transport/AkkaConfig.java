package iql.web.transport;

import akka.actor.ActorSystem;
import iql.common.utils.AkkaUtils;
import com.typesafe.config.Config;
import iql.common.utils.ZkUtils;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class AkkaConfig {


    @Value("${standAlone:true}")
    private String standAlone;
    @Value("${standAloneActorPath:127.0.0.1:18889_actor1}")
    private String standAloneActorPath;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SpringExtension springExtension;
    @Autowired
    private Environment env;

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem actorSystem = ActorSystem.create("IQLServiceSystem", getConfig());
        springExtension.initialize(applicationContext);
        return actorSystem;
    }

    @Bean
    public ZkClient getZkClient() {
        String zkServers = env.getProperty("zkServers");
        return StringUtils.isNotEmpty(zkServers)
                ? ZkUtils.getZkClient(zkServers)
                : null;
    }

    @Bean
    public Config getConfig() {
        return !Boolean.parseBoolean(getStandAlone())
                ? AkkaUtils.getConfig(getZkClient())
                : AkkaUtils.getWebStandAloneConfig()
                ;
    }

    public String getStandAlone() {
        return standAlone;
    }

    public String getStandAloneActorPath() {
        return standAloneActorPath;
    }
}
