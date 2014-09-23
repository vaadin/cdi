package com.vaadin.cdi.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class LoginPane extends VerticalLayout {
    public static final String USER_ID = "user";
    public static final String PASSWORD_ID = "password";
    public static final String LOGIN_ID = "login";
    public static final String CURRENT_USER_ID = "currentUser";

    private Label currentUserLabel = new Label();

    private TextField userField = new TextField("User (admin or demo)");
    private PasswordField passwordField = new PasswordField(
            "Password (same as user)");
    private Button loginButton = new Button("Login");

    public LoginPane() {
        setSizeUndefined();

        currentUserLabel.setCaption("Current user:");
        currentUserLabel.setId(CURRENT_USER_ID);

        userField.setId(USER_ID);
        passwordField.setId(PASSWORD_ID);
        loginButton.setId(LOGIN_ID);

        addComponents(currentUserLabel, userField, passwordField, loginButton);
        updateCurrentUser();

        loginButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                login(userField.getValue(), passwordField.getValue());
                passwordField.setValue("");
                updateCurrentUser();
            }
        });
    }

    private void updateCurrentUser() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        currentUserLabel
                .setValue(principal != null ? principal.toString() : "");
    }

    private void login(String user, String password) {
        if (!"".equals(user)) {
            UsernamePasswordToken token = new UsernamePasswordToken(user,
                    password);
            token.setRememberMe(true);
            try {
                SecurityUtils.getSubject().login(token);
            } catch (AuthenticationException ae) {
                // normally would show notification, here just update user name
                // Notification.show("Authentication failed",
                // Notification.Type.ERROR_MESSAGE);
            }
        } else {
            // this invalidates the session
            SecurityUtils.getSubject().logout();
        }
    }
}
