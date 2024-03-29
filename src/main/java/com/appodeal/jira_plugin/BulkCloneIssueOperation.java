package com.appodeal.jira_plugin;

import com.atlassian.jira.bulkedit.operation.BulkOperationException;
import com.atlassian.jira.bulkedit.operation.ProgressAwareBulkOperation;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.task.context.Context;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.BulkEditBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class BulkCloneIssueOperation implements ProgressAwareBulkOperation {
    private static final Logger log = LoggerFactory.getLogger(BulkCloneIssueOperation.class);

    private final IssueManager issueManager;
    private final IssueTypeManager issueTypeManager;
    private final IssueFactory issueFactory;
    private final IssueLinkManager issueLinkManager;
    private final IssueLinkTypeManager issueLinkTypeManager;


    public BulkCloneIssueOperation(IssueManager issueManager, IssueTypeManager issueTypeManager, IssueFactory issueFactory, IssueLinkManager issueLinkManager, IssueLinkTypeManager issueLinkTypeManager) {
        log.debug("BulkCloneIssueOperation INIT");
        this.issueManager = issueManager;
        this.issueTypeManager = issueTypeManager;
        this.issueFactory = issueFactory;
        this.issueLinkManager = issueLinkManager;
        this.issueLinkTypeManager = issueLinkTypeManager;
    }

    @Override
    public String getOperationName() {
        log.debug("getOperationName");
        return "BulkCloneIssue";
    }

    @Override
    public String getCannotPerformMessageKey() {
        log.debug("getCannotPerformMessageKey");
        return "bulk.clone.issue.cannotperform";
    }

    @Override
    public boolean canPerform(BulkEditBean bulkEditBean, ApplicationUser applicationUser) {
        log.debug("canPerform");
        List<Issue> selectedIssues = bulkEditBean.getSelectedIssues();
        PermissionManager permissionManager = ComponentAccessor.getPermissionManager();
        Iterator var5 = selectedIssues.iterator();

        Issue issue;
        do {
            if (!var5.hasNext()) {
                return true;
            }

            issue = (Issue) var5.next();
        } while (permissionManager.hasPermission(Permissions.CREATE_ISSUE, issue, applicationUser));

        return false;
    }

    @Override
    public void perform(BulkEditBean bulkEditBean, ApplicationUser applicationUser, Context taskContext) throws BulkOperationException {
        log.debug("perform");
        List<Issue> selectedIssues = bulkEditBean.getSelectedIssues();
        Context.Task task;

        String type = (String) bulkEditBean.getFieldValues().get("bulk.clone.issue.operation.field.type");
        String prefix = (String) bulkEditBean.getFieldValues().get("bulk.clone.issue.operation.field.prefix");
        String link = (String) bulkEditBean.getFieldValues().get("bulk.clone.issue.operation.field.link");
        log.debug("get type: " + type);
        log.debug("get prefix: " + prefix);
        log.debug("get link: " + link);
        IssueLinkType issueLinkType = issueLinkTypeManager.getIssueLinkType(Long.parseLong(link));

        log.debug("issues: " + selectedIssues.size());
        for (Iterator iterator = selectedIssues.iterator(); iterator.hasNext(); task.complete()) {
            Issue issue = (Issue) iterator.next();
            task = taskContext.start(issue);
            if (issueManager.getIssueObject(issue.getId()) != null) {
                try {
                    MutableIssue newIssue = issueFactory.cloneIssueWithAllFields(issue);
                    if (prefix != null && !prefix.isEmpty()) {
                        newIssue.setSummary(prefix + " - " + newIssue.getSummary());
                    }
                    if (type != null && !type.isEmpty() && !type.equals("-1")) {
                        log.debug("type is not empty: " + type);
                        IssueType issueType = issueTypeManager.getIssueType(type);
                        newIssue.setIssueType(issueType);
                    }
                    Issue clonedIssueObject = issueManager.createIssueObject(applicationUser, newIssue);
                    log.debug("clonedIssueObject: " + clonedIssueObject.getKey());

                    issueLinkManager.createIssueLink(issue.getId(), clonedIssueObject.getId(), issueLinkType.getId(), 1L, applicationUser);
                } catch (Exception var11) {
                    log.debug(var11.toString());
                    throw new BulkOperationException(var11);
                }
            } else {
                log.debug("Not cloning issue with id '" + issue.getId() + "' and key '" + issue.getKey() + "' as it does not exist in the database (it could have been deleted earlier as it might be a subtask).");
            }
        }
    }

    @Override
    public int getNumberOfTasks(BulkEditBean bulkEditBean) {
        log.debug("getNumberOfTasks");
        return bulkEditBean.getSelectedIssues().size();
    }


    @Override
    public String getNameKey() {
        log.debug("getNameKey");

        return "bulk.clone.issue.operation.name";
    }

    @Override
    public String getDescriptionKey() {
        log.debug("getDescriptionKey");

        return "bulk.clone.issue.operation.description";
    }
}
