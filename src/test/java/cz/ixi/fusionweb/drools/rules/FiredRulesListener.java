package cz.ixi.fusionweb.drools.rules;

import java.util.HashMap;
import java.util.Map;

import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.RuleFlowGroupActivatedEvent;
import org.drools.event.rule.RuleFlowGroupDeactivatedEvent;

public class FiredRulesListener implements AgendaEventListener {

    private Map<String, Integer> firedRules = new HashMap<String, Integer>();

    public boolean isRuleFired(String rule) {
	return firedRules.containsKey(rule);
    }

    public int howManyTimesIsRuleFired(String rule) {
	if (!firedRules.containsKey(rule)) {
	    return 0;
	}
	return firedRules.get(rule);
    }

    public void clear() {
	firedRules.clear();
    }

    @Override
    public void afterActivationFired(AfterActivationFiredEvent event) {
	String rule = event.getActivation().getRule().getName();
	if (firedRules.containsKey(rule)) {
	    firedRules.put(rule, firedRules.get(rule) + 1);
	} else {
	    firedRules.put(rule, 1);
	}

    }

    @Override
    public void activationCreated(ActivationCreatedEvent event) {
	// TODO Auto-generated method stub

    }

    @Override
    public void activationCancelled(ActivationCancelledEvent event) {
	// TODO Auto-generated method stub

    }

    @Override
    public void beforeActivationFired(BeforeActivationFiredEvent event) {
	// TODO Auto-generated method stub

    }

    @Override
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
	// TODO Auto-generated method stub

    }

    @Override
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
	// TODO Auto-generated method stub

    }

    @Override
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
	// TODO Auto-generated method stub

    }

    @Override
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
	// TODO Auto-generated method stub

    }

    @Override
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
	// TODO Auto-generated method stub

    }

    @Override
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
	// TODO Auto-generated method stub

    }

}