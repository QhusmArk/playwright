package com.example.playwright.pageObjects;

import com.example.playwright.components.panels.LoginPage;
import com.example.playwright.components.parts.InputField;

public class LoginPO extends CommonPO {

    //    /login
    private static final String INPUT_USERNAME = "//form //input[@name='username']";
    private static final String INPUT_PASSWORD = "//form //input[@name='password']";
    private static final String LOGIN = "//form //input[@name='submit']";

    //    /passreset/
    private static final String INPUT_RESET_EMAIL = "//form //input[@name='email']";
    private static final String REQUEST_RESET_LINK = "//form //input[@name='submit']";

    public void login(String email, String password) {
        // Enter email
        actions().clearAndType("//form //input[@name='username']", email);

        // Enter password
        actions().clearAndType("//form //input[@name='password']", password);

        // Click submit
        actions().makeClick("//form //input[@name='submit']");
    }

    /**
     * Method that lets the two login //forms be filled out.
     *
     * @param username The users e-mail address
     * @param password The users password
     */
    public void enterLoginCredentials(final String username, final String password) {
        actions().clearAndType(INPUT_USERNAME, username);
        actions().clearAndType(INPUT_PASSWORD, password);
    }

    public boolean isSessionExpired() {
        return actions().elementExistAndVisible("//*[contains(text(), 'Your session is expired')]", false);
    }

    public void enterResetUsername(final String username) {
        actions().clearAndType(INPUT_RESET_EMAIL, username);
    }

    public void clickLoginButton() {
        actions().makeClick(LOGIN);
    }

    public void clickRequestResetLink() {
        actions().makeClick(REQUEST_RESET_LINK);
    }

    public String getRefUrlFromLink(String destination) {
        return actions().findOneElementsAttribute("//a[text()='"+destination+"']", "href");
    }

    public LoginPage getLoginPage() {
        LoginPage loginPage = new LoginPage();

        String formPath = "//form[@id='master-form']";

        String emailInputPath = "(" + formPath + " //div[@class='form-control'])[1]/div";
        InputField emailInput = new InputField();
        String emailInputHeader = actions().findOneElementsText(emailInputPath + "/label");
        String emailInputText = actions().findOneElementsText(emailInputPath + "/input");
        emailInput.setHeader(emailInputHeader);
        emailInput.setText(emailInputText);

        loginPage.setEmailField(emailInput);

        String passwordInputPath = "(" + formPath + " //div[@class='form-control'])[1]/div";
        InputField passwordInput = new InputField();
        String passwordInputHeader = actions().findOneElementsText(passwordInputPath + "/label");
        String passwordInputText = actions().findOneElementsText(passwordInputPath + "/input");
        passwordInput.setHeader(passwordInputHeader);
        passwordInput.setText(passwordInputText);

        loginPage.setPasswordField(passwordInput);

        String errorMsgPath = formPath + "//div[@class='error-message']";

        boolean errorMsgExist = actions().elementExistAndVisible(errorMsgPath, false, 2);
        if (errorMsgExist) {
            String errorMessage = actions().findOneElementsText(errorMsgPath + "/p[1]");
            loginPage.setErrorMessage(errorMessage);

            String errorExplanation = actions().findOneElementsText(errorMsgPath + "/p[2]");
            loginPage.setErrorExplanation(errorExplanation);
        }

        return loginPage;
    }
}
