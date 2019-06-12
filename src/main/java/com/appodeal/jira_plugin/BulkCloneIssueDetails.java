package com.appodeal.jira_plugin;

import com.atlassian.jira.web.action.JiraWebActionSupport;



public class BulkCloneIssueDetails extends JiraWebActionSupport {


//    public BulkCloneIssueDetails(SearchService searchService, BulkEditBeanSessionHelper bulkEditBeanSessionHelper) {
//        super(searchService, bulkEditBeanSessionHelper);
//    }

    public BulkCloneIssueDetails() {
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

//    @Override
//    public String doDetailsValidation() throws Exception {
//        System.out.println("ZA----------doDetailsValidation---------ZA");
//        return null;
//    }
//
//    @Override
//    public String doPerform() throws Exception {
//        System.out.println("ZA----------doPerform---------ZA");
//        return null;
//    }

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
