package com.appodeal.jira_plugin;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.issue.worklog.TimeTrackingConfiguration;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.task.TaskManager;
import com.atlassian.jira.user.UserIssueHistoryManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.jira.web.action.issue.bulkedit.AbstractBulkOperationDetailsAction;
import com.atlassian.jira.web.bean.BulkEditBeanSessionHelper;


public class BulkCloneIssueDetails extends AbstractBulkOperationDetailsAction {


//    public BulkCloneIssueDetails(SearchService searchService, BulkEditBeanSessionHelper bulkEditBeanSessionHelper) {
//        super(searchService, bulkEditBeanSessionHelper);
//    }
//    public BulkCloneIssueDetails(SearchService searchService, BulkEditBeanSessionHelper bulkEditBeanSessionHelper) {
//        this((IssueManager) ComponentAccessor.getComponent(IssueManager.class), (CustomFieldManager)ComponentAccessor.getComponent(CustomFieldManager.class), (AttachmentManager)ComponentAccessor.getComponent(AttachmentManager.class), (ProjectManager)ComponentAccessor.getComponent(ProjectManager.class), (PermissionManager)ComponentAccessor.getComponent(PermissionManager.class), (VersionManager)ComponentAccessor.getComponent(VersionManager.class), (UserIssueHistoryManager)ComponentAccessor.getComponent(UserIssueHistoryManager.class), (TimeTrackingConfiguration)ComponentAccessor.getComponent(TimeTrackingConfiguration.class));
//
//    }

    public BulkCloneIssueDetails() {
        super((SearchService) ComponentAccessor.getComponent(SearchService.class), (BulkEditBeanSessionHelper) ComponentAccessor.getComponent(BulkEditBeanSessionHelper.class), (TaskManager) ComponentAccessor.getComponent(TaskManager.class), (I18nHelper) ComponentAccessor.getComponent(I18nHelper.class));
        System.out.println("ZA----------BulkCloneIssueDetails INIT---------ZA");
    }
//
//    public BulkCloneIssueDetails(SearchService searchService, BulkEditBeanSessionHelper bulkEditBeanSessionHelper, TaskManager taskManager, I18nHelper i18nHelper) {
//        super(searchService, bulkEditBeanSessionHelper, taskManager, i18nHelper);
//        System.out.println("ZA----------BulkCloneIssueDetails INIT---------ZA");
//    }


    public String doDetails() throws Exception {
        System.out.println("ZA----------doDetails---------ZA");
        return INPUT;
    }

    @Override
    public String doDetailsValidation() throws Exception {
        System.out.println("ZA----------doDetailsValidation---------ZA");
        return null;
    }

    @Override
    public String doPerform() throws Exception {
        System.out.println("ZA----------doPerform---------ZA");
        return null;
    }

    @Override
    public String doDefault() throws Exception {
        System.out.println("ZA----------doDefault---------ZA");
        return INPUT;
    }

    @Override
    protected String doExecute() throws Exception {
        System.out.println("ZA----------doExecute---------ZA");
        return INPUT;
    }

    @Override
    protected void doValidation() {
        System.out.println("ZA----------doValidation---------ZA");
        super.doValidation();
    }
}
