package com.appodeal.jira_plugin;

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
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.action.JiraWebActionSupport;

import java.util.Collection;

public class SingleCloneIssueAction extends JiraWebActionSupport {

    private final IssueManager issueManager;
    private final IssueFactory issueFactory;
    private final IssueTypeManager issueTypeManager;
    private final IssueLinkManager issueLinkManager;
    private final IssueLinkTypeManager issueLinkTypeManager;
    private final ProjectManager projectManager;

    private String id;
    private String key;
    private Collection<IssueType> types;
    private Collection<IssueLinkType> links;

    private String issueType;
    private String prefix;
    private String linkType;

    public SingleCloneIssueAction(IssueManager issueManager, IssueFactory issueFactory, IssueTypeManager issueTypeManager, IssueLinkManager issueLinkManager, IssueLinkTypeManager issueLinkTypeManager, ProjectManager projectManager) {
        this.issueManager = issueManager;
        this.issueFactory = issueFactory;
        this.issueTypeManager = issueTypeManager;
        this.issueLinkManager = issueLinkManager;
        this.issueLinkTypeManager = issueLinkTypeManager;
        this.projectManager = projectManager;
    }

    @Override
    public String doDefault() throws Exception {
        try {
            Issue originalIssue = this.issueManager.getIssueObject(Long.valueOf(this.id));
            this.key = originalIssue.getKey();

            Project project = projectManager.getProjectObj(originalIssue.getProjectId());
            this.types = project.getIssueTypes();

            this.links = issueLinkTypeManager.getIssueLinkTypes();
        } catch (Exception e) {
            addErrorMessage("Error while getting Issue info");
            return ERROR;
        }
        return INPUT;
    }

    @Override
    protected String doExecute() throws Exception {
        try {
            Issue originalIssue = this.issueManager.getIssueObject(Long.valueOf(this.id));
            MutableIssue newIssue = issueFactory.cloneIssueWithAllFields(originalIssue);
            if (prefix != null && !prefix.isEmpty()) {
                newIssue.setSummary(prefix + " - " + newIssue.getSummary());
            }
            if (issueType != null && !issueType.isEmpty() && !issueType.equals("-1")) {
                log.debug("type is not empty: " + issueType);
                IssueType issueTypeObject = issueTypeManager.getIssueType(this.issueType);
                newIssue.setIssueType(issueTypeObject);
            }
            Issue clonedIssueObject = issueManager.createIssueObject(getLoggedInUser(), newIssue);
            log.debug("clonedIssueObject: " + clonedIssueObject.getKey());

            IssueLinkType issueLinkType = issueLinkTypeManager.getIssueLinkType(Long.parseLong(linkType));

            issueLinkManager.createIssueLink(originalIssue.getId(), clonedIssueObject.getId(), issueLinkType.getId(), 1L, getLoggedInUser());

            return inlineRedirectToIssueWithKey(clonedIssueObject.getKey());
        } catch (Exception var11) {
            log.debug(var11.toString());
            throw new Exception(var11);
        }
    }

    private String inlineRedirectToIssueWithKey(String issueKey) {
        return super.returnCompleteWithInlineRedirect("/browse/" + issueKey);
    }

    @Override
    protected void doValidation() {
        super.doValidation();
    }

    @SuppressWarnings("unused")
    public String getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public void setId(String id) {
        this.id = id;
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

    @SuppressWarnings("unused")
    public String getKey() {
        return key;
    }

    @SuppressWarnings("unused")
    public void setKey(String key) {
        this.key = key;
    }

    public ApplicationUser getLoggedInUser() {
        return ComponentAccessor.getJiraAuthenticationContext().getUser();
    }
}
