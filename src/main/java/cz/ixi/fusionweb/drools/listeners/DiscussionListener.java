package cz.ixi.fusionweb.drools.listeners;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import cz.ixi.fusionweb.drools.model.DiscussionEntryEvent;
import cz.ixi.fusionweb.drools.rules.DroolsResourcesBean;
import cz.ixi.fusionweb.entities.DiscussionEntry;
import cz.ixi.fusionweb.entities.Product;
import cz.ixi.fusionweb.entities.User;

/**
 * Inserts events of discussion entries into drools working memory.
 */
@ManagedBean(name = "discussionListener")
public class DiscussionListener {

    @EJB
    DroolsResourcesBean drools;

    public void newDiscussionEntry(DiscussionEntry entry, Product product, User user) {
	drools.insertFact(new DiscussionEntryEvent(product.getName(), entry.getTitle() + ": " + entry.getText(), user.getUsername()));
    }

}
