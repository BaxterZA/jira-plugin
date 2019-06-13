package com.appodeal.jira_plugin;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bulkedit.BulkOperationManager;
import com.atlassian.jira.bulkedit.operation.ProgressAwareBulkOperation;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.operation.SpanningOperation;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.task.TaskManager;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.ParameterUtils;
import com.atlassian.jira.web.action.issue.bulkedit.AbstractBulkOperationDetailsAction;
import com.atlassian.jira.web.bean.BulkEditBean;
import com.atlassian.jira.web.bean.BulkEditBeanSessionHelper;
import com.atlassian.jira.config.IssueTypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;


public class BulkCloneIssueAction extends AbstractBulkOperationDetailsAction {
    private static final Logger log = LoggerFactory.getLogger(BulkCloneIssueAction.class);

    private final PermissionManager permissionManager;
    private final IssueTypeManager issueTypeManager;
    private final IssueLinkTypeManager issueLinkTypeManager;


    private Collection<IssueType> types;
    private Collection<IssueLinkType> links;
    private String issueType;
    private String prefix;
    private String linkType;

    private final ProgressAwareBulkOperation bilkCloneIssueOperations;

    public BulkCloneIssueAction() {
        super(ComponentAccessor.getComponent(SearchService.class), ComponentAccessor.getComponent(BulkEditBeanSessionHelper.class), ComponentAccessor.getComponent(TaskManager.class), ComponentAccessor.getComponent(I18nHelper.class));
        log.debug("BulkCloneIssueAction INIT");
        this.permissionManager = ComponentAccessor.getComponent(PermissionManager.class);
        this.issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
        this.issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
        this.links = issueLinkTypeManager.getIssueLinkTypes();
        this.bilkCloneIssueOperations = ComponentAccessor.getComponent(BulkOperationManager.class).getProgressAwareOperation("bulk.clone.issue.operation.name");
        log.debug("BulkCloneIssueAction END");
    }

    @SuppressWarnings("unused")
    public boolean isHasAvailableActions() throws Exception {
        log.debug("isHasAvailableActions");
        return this.getBulkCloneIssueOperation().canPerform(this.getBulkEditBean(), this.getLoggedInUser());
    }

    @SuppressWarnings("unused")
    public String getOperationDetailsActionName() {
        log.debug("getOperationDetailsActionName");
        return this.getBulkCloneIssueOperation().getOperationName() + "Details.jspa";
    }

    private void doPerformValidation() {
        log.debug("doPerformValidation");
        try {
            getDataFromScreen();

            if (!this.permissionManager.hasPermission(Permissions.BULK_CHANGE, this.getLoggedInUser())) {
                this.addErrorMessage(this.getText("bulk.change.no.permission", String.valueOf(this.getBulkEditBean().getSelectedIssues().size())));
            }

            if (!this.getBulkCloneIssueOperation().canPerform(this.getBulkEditBean(), this.getLoggedInUser())) {
                this.addErrorMessage(this.getText("bulk.clone.issue.cannotperform.error", String.valueOf(this.getBulkEditBean().getSelectedIssues().size())));
            }
        } catch (Exception var2) {
            this.addErrorMessage(this.getText("bulk.canperform.error"));
        }

    }

    private void getDataFromScreen() {
        log.debug("getDataFromScreen");
        Map map = getHttpRequest().getParameterMap();
        this.issueType = ParameterUtils.getStringParam(map, "issueType");
        this.linkType = ParameterUtils.getStringParam(map, "linkType");
        this.prefix = ParameterUtils.getStringParam(map, "prefix");
        log.debug("get type:" + this.issueType);
        log.debug("get link:" + this.linkType);
        log.debug("get prefix:" + this.prefix);
        log.debug("getDataFromScreen END");

    }

    @Nullable
    private Long getDefaultCloneIssueLinkType() {
        Long cloneType = null;
        for (IssueLinkType issueLinkType : links) {
            if (issueLinkType.getName().equalsIgnoreCase("Cloners")) {
                cloneType = issueLinkType.getId();
            }
        }
        return cloneType;
    }

    public String doDetails() throws Exception {
        log.debug("doDetails");
        if (this.getBulkEditBean() == null) {
            return this.redirectToStart();
        } else {
            BulkEditBean bulkEditBean = getBulkEditBean();

            Set<Long> projects = issuesProjectKeysFromIssues(bulkEditBean.getSelectedIssues());
            if (projects.size() != 1) {
                addErrorMessage(getText("bulk.clone.issue.error.multiple.project"));
                return ERROR;
            } else {
                ProjectManager projectManager = ComponentAccessor.getComponent(ProjectManager.class);
                Project project = projectManager.getProjectObj(projects.iterator().next());
                this.types = project.getIssueTypes();
            }

            bulkEditBean.clearAvailablePreviousSteps();
            bulkEditBean.addAvailablePreviousStep(1);
            bulkEditBean.addAvailablePreviousStep(2);
            bulkEditBean.setCurrentStep(3);

            return INPUT;
        }
    }

    private Set<Long> issuesProjectKeysFromIssues(List<Issue> issues) {
        Set<Long> projects = new HashSet<>();
        for (Issue issue : issues) {
            projects.add(issue.getProjectId());
        }
        return projects;
    }

    public String doDetailsValidation() {
        log.debug("doDetailsValidation");
        if (this.getBulkEditBean() == null) {
            return this.redirectToStart();
        } else {
            doPerformValidation();
            doInputValidation();
            if (this.invalidInput()) {
                return ERROR;
            }

            BulkEditBean bulkEditBean = this.getBulkEditBean();
            bulkEditBean.clearAvailablePreviousSteps();
            bulkEditBean.addAvailablePreviousStep(1);
            bulkEditBean.addAvailablePreviousStep(2);
            bulkEditBean.addAvailablePreviousStep(3);
            bulkEditBean.setCurrentStep(4);
            return INPUT;
        }
    }

    private void doInputValidation() {
        log.debug("doInputValidation");
        if (this.prefix == null) {
            this.prefix = "";
        }
        if ((this.linkType == null) || (this.linkType.equals("-1"))) {
            Long defaultLinkType = getDefaultCloneIssueLinkType();
            if (defaultLinkType == null) {
                this.addErrorMessage(getText("bulk.clone.issue.link.is.null"));
            } else {
                this.linkType = String.valueOf(defaultLinkType);
            }
        }
    }

    @RequiresXsrfCheck
    public String doPerform() throws Exception {
        log.debug("doPerform");
        if (this.getBulkEditBean() == null) {
            return this.redirectToStart();
        } else {
            doPerformValidation();
            if (invalidInput()) {
                return ERROR;
            }
            BulkEditBean bulkEditBean = this.getBulkEditBean();
            log.debug("set type: " + this.issueType);
            log.debug("set prefix: " + this.prefix);
            log.debug("set link: " + this.linkType);
            bulkEditBean.getFieldValues().put("bulk.clone.issue.operation.field.type", this.issueType);
            bulkEditBean.getFieldValues().put("bulk.clone.issue.operation.field.link", this.linkType);
            bulkEditBean.getFieldValues().put("bulk.clone.issue.operation.field.prefix", this.prefix);

            String taskName = this.getText("bulk.operation.progress.taskname.clone.issue", bulkEditBean.getSelectedIssuesIncludingSubTasks().size());
            return this.submitBulkOperationTask(bulkEditBean, this.getBulkCloneIssueOperation(), taskName, SpanningOperation.builder().type("BULK_CLONE").generatedId().build());
        }
    }

    private ProgressAwareBulkOperation getBulkCloneIssueOperation() {
        log.debug("getBulkCloneIssueOperation");
        return this.bilkCloneIssueOperations;
    }


    @SuppressWarnings("unused")
    public Collection<IssueType> getTypes() {
        log.debug("getTypes");
        return types;
    }

    @SuppressWarnings("unused")
    public void setTypes(Collection<IssueType> types) {
        log.debug("setTypes");
        this.types = types;
    }

    @SuppressWarnings("unused")
    public Collection<IssueLinkType> getLinks() {
        log.debug("getLinks");
        return this.links;
    }

    @SuppressWarnings("unused")
    public void setLinks(Collection<IssueLinkType> links) {
        this.links = links;
    }

    @SuppressWarnings("unused")
    public String getPrefixName() {
        log.debug("getPrefixName: " + this.prefix);
        return this.prefix;
    }

    @SuppressWarnings("unused")
    public String getTypeName() {
        log.debug("getTypeName: " + this.issueType);
        if ((this.issueType != null) && (!this.issueType.equals("-1")) && (!this.issueType.isEmpty())) {
            return this.issueTypeManager.getIssueType(this.issueType).getNameTranslation();
        }
        return "";
    }

    @SuppressWarnings("unused")
    public String getLinkName() {
        log.debug("getLinkName: " + this.linkType);
        if ((this.linkType != null) && (!this.linkType.equals("-1")) && (!this.linkType.isEmpty())) {
            return this.issueLinkTypeManager.getIssueLinkType(Long.parseLong(this.linkType)).getName();
        }
        return "";
    }

    @SuppressWarnings("unused")
    public String getIssueType() {
        return issueType;
    }

    @SuppressWarnings("unused")
    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    @SuppressWarnings("unused")
    public String getPrefix() {
        return prefix;
    }

    @SuppressWarnings("unused")
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @SuppressWarnings("unused")
    public String getLinkType() {
        return linkType;
    }

    @SuppressWarnings("unused")
    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }
}
