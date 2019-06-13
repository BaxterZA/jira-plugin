package com.appodeal.jira_plugin;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bulkedit.BulkOperationManager;
import com.atlassian.jira.bulkedit.operation.ProgressAwareBulkOperation;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.operation.SpanningOperation;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
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
import java.util.Collection;
import java.util.Map;



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
        System.out.println("ZA-------BulkCloneIssueAction INIT-------ZA");
        this.permissionManager = ComponentAccessor.getComponent(PermissionManager.class);
        this.issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
        this.issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
        this.types = issueTypeManager.getIssueTypes();
        this.links = issueLinkTypeManager.getIssueLinkTypes();
        this.bilkCloneIssueOperations = ComponentAccessor.getComponent(BulkOperationManager.class).getProgressAwareOperation("bulk.clone.issue.operation.name");
        System.out.println("ZA-------BulkCloneIssueAction END-------ZA");
    }

    @SuppressWarnings("unused")
    public boolean isHasAvailableActions() throws Exception {
        System.out.println("ZA-------isHasAvailableActions-------ZA");
        return this.getBulkCloneIssueOperation().canPerform(this.getBulkEditBean(), this.getLoggedInUser());
    }

    @SuppressWarnings("unused")
    public String getOperationDetailsActionName() {
        System.out.println("ZA-------getOperationDetailsActionName-------ZA");
        return this.getBulkCloneIssueOperation().getOperationName() + "Details.jspa";
    }

    private void doPerformValidation() {
        System.out.println("ZA-------doPerformValidation-------ZA");
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
        System.out.println("ZA-------getDataFromScreen-------ZA");
        Map map = getHttpRequest().getParameterMap();
        this.issueType = ParameterUtils.getStringParam(map, "issueType");
        this.linkType = ParameterUtils.getStringParam(map, "linkType");
        this.prefix = ParameterUtils.getStringParam(map, "prefix");
        System.out.println("ZA-------" + this.issueType + "-------ZA");
        System.out.println("ZA-------" + this.linkType + "-------ZA");
        System.out.println("ZA-------" + this.prefix + "-------ZA");
        System.out.println("ZA-------getDataFromScreen END-------ZA");

    }

    private void doInputValidation() {
        System.out.println("ZA-------doInputValidation-------ZA");
        if (this.prefix == null) {
            this.prefix = "";
        }
        if ((this.linkType == null) || (this.linkType.equals("-1"))) {
            this.linkType = String.valueOf(getDefaultCloneIssueLinkType());
            if (this.linkType == null) {
                addErrorMessage(getText("bulk.clone.issue.link.is.null"));
            }
        }
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

    public String doDetails() {
        System.out.println("ZA-------doDetails-------ZA");
        if (this.getBulkEditBean() == null) {
            return this.redirectToStart();
        } else {
            BulkEditBean bulkEditBean = getBulkEditBean();
            bulkEditBean.clearAvailablePreviousSteps();
            bulkEditBean.addAvailablePreviousStep(1);
            bulkEditBean.addAvailablePreviousStep(2);
            if (this.isCanDisableMailNotifications()) {
                bulkEditBean.setSendBulkNotification(false);
            } else {
                bulkEditBean.setSendBulkNotification(true);
            }

            bulkEditBean.setCurrentStep(3);
            return INPUT;
        }
    }

    public String doDetailsValidation() {
        System.out.println("ZA-------doDetailsValidation-------ZA");
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

    @RequiresXsrfCheck
    public String doPerform() throws Exception {
        System.out.println("ZA-------doPerform-------ZA");
        if (this.getBulkEditBean() == null) {
            return this.redirectToStart();
        } else {
            doPerformValidation();
            if (invalidInput()) {
                return ERROR;
            }
            BulkEditBean bulkEditBean = this.getBulkEditBean();
            System.out.println("ZA-------set type: " + this.issueType + "-------ZA");
            System.out.println("ZA-------set prefix: " + this.prefix + "-------ZA");
            System.out.println("ZA-------set link: " + this.linkType + "-------ZA");
            bulkEditBean.getFieldValues().put("bulk.clone.issue.operation.field.type", this.issueType);
            bulkEditBean.getFieldValues().put("bulk.clone.issue.operation.field.link", this.linkType);
            bulkEditBean.getFieldValues().put("bulk.clone.issue.operation.field.prefix", this.prefix);

            String taskName = this.getText("bulk.operation.progress.taskname.clone.issue", bulkEditBean.getSelectedIssuesIncludingSubTasks().size());
            return this.submitBulkOperationTask(bulkEditBean, this.getBulkCloneIssueOperation(), taskName, SpanningOperation.builder().type("BULK_CLONE").generatedId().build());
        }
    }

    private ProgressAwareBulkOperation getBulkCloneIssueOperation() {
        System.out.println("ZA-------getBulkCloneIssueOperation-------ZA");
        return this.bilkCloneIssueOperations;
    }


    @SuppressWarnings("unused")
    public Collection<IssueType> getTypes() {
        System.out.println("ZA-------getTypes-------ZA");
        return types;
    }

    @SuppressWarnings("unused")
    public void setTypes(Collection<IssueType> types) {
        System.out.println("ZA-------setTypes-------ZA");
        this.types = types;
    }

    @SuppressWarnings("unused")
    public Collection<IssueLinkType> getLinks() {
        System.out.println("ZA-------getLinks-------ZA");
        return this.links;
    }

    @SuppressWarnings("unused")
    public void setLinks(Collection<IssueLinkType> links) {
        this.links = links;
    }

    @SuppressWarnings("unused")
    public String getPrefixName() {
        System.out.println("ZA-------getPrefixName: " + this.prefix + "-------ZA");
        return this.prefix;
    }

    @SuppressWarnings("unused")
    public String getTypeName() {
        System.out.println("ZA-------getTypeName: " + this.issueType + "-------ZA");
        if ((this.issueType != null) && (!this.issueType.equals("-1")) && (!this.issueType.isEmpty())) {
            return this.issueTypeManager.getIssueType(this.issueType).getNameTranslation();
        }
        return "";
    }

    @SuppressWarnings("unused")
    public String getLinkName() {
        System.out.println("ZA-------getLinkName: " + this.linkType + "-------ZA");
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
