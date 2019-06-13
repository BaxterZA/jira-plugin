package com.appodeal.jira_plugin;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.inject.Named;

@SuppressWarnings("unused")
@Named("BulkCloneIssuePluginListener")
public class BulkCloneIssuePluginListener implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(BulkCloneIssuePluginListener.class);
    private final EventPublisher eventPublisher;

    @SuppressWarnings("unused")
    public BulkCloneIssuePluginListener(EventPublisher eventPublisher) {
        log.debug("INIT");
        this.eventPublisher=eventPublisher;
    }

    @Override
    public void afterPropertiesSet() {
        log.debug("afterPropertiesSet");
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
        log.debug("onPluginEnabled");
    }
}
