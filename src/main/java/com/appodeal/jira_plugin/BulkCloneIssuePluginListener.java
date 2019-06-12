package com.appodeal.jira_plugin;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

//import javax.inject.Inject;
//import javax.inject.Named;

//@Named("BulkCloneIssuePluginListener")
public class BulkCloneIssuePluginListener implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(BulkCloneIssuePluginListener.class);
    private final EventPublisher eventPublisher;

    public BulkCloneIssuePluginListener(EventPublisher eventPublisher) {
        log.info("ZA-------INIT-------ZA");
        System.out.println("ZA------INIT------ZA");
        this.eventPublisher=eventPublisher;
    }

    @Override
    public void afterPropertiesSet() {
        log.info("ZA-------afterPropertiesSet-------ZA");
        System.out.println("ZA------afterPropertiesSet------ZA");
        eventPublisher.register(this);
        ComponentAccessor.getBulkOperationManager().addProgressAwareBulkOperation("bulk.clone.issue.operation.name", BulkCloneIssueOperation.class);
    }

    @Override
    public void destroy() {
        eventPublisher.unregister(this);
    }

    @SuppressWarnings("unused")
    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        System.out.println("ZA------onPluginEnabled------ZA");
    }
}
