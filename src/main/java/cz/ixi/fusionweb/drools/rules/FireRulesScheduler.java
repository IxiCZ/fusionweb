package cz.ixi.fusionweb.drools.rules;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Scheduler which fires rules,
 */
@Startup
@Singleton
@DependsOn({"StartupDBConfigBean","DroolsResourcesBean"})
public class FireRulesScheduler {

    @Inject 
    private DroolsResourcesBean drools;
    
    @PostConstruct
    @Schedule(hour="*", minute="*/5", second="0", persistent=false)
    public void init(){
	drools.fireAllRules();
    }    
  
}
