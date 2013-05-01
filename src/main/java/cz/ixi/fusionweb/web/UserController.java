package cz.ixi.fusionweb.web;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cz.ixi.fusionweb.drools.listeners.CustomerLogActionsListener;
import cz.ixi.fusionweb.ejb.UserBean;
import cz.ixi.fusionweb.entities.Role;
import cz.ixi.fusionweb.entities.User;
import cz.ixi.fusionweb.web.util.JsfUtil;

@SessionScoped
@ManagedBean
public class UserController implements Serializable {

    private static final long serialVersionUID = 1L;

    private String password;
    private String username;

    private User user;

    @Inject
    private UserBean users;
    @Inject
    private CustomerLogActionsListener droolsCustomerLogActions;

    /**
     * Constructor
     */
    public UserController() {
    }

    /**
     * Performs login.
     */
    public String login() {

	FacesContext context = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
	try {
	    if (request.getUserPrincipal() != null) {
		request.logout();
	    }
	    request.login(this.username, this.password);
	    Logger.getLogger(UserController.class.getName()).log(Level.INFO, "Logged: " + username);
	    this.user = this.users.getUserByUsername(this.username);
	    JsfUtil.addSuccessMessage("Login was successful.");
	    if (user.getRoles().contains(Role.CUSTOMER)) {
		droolsCustomerLogActions.customerLogIn(user);
	    }

	    return "";
	} catch (ServletException ex) {
	    JsfUtil.addErrorMessage("Login was unsuccessful.");
	    Logger.getLogger(UserController.class.getName()).log(Level.INFO,
		    "Not logged, attempt was " + this.username + " " + this.password);
	    ex.printStackTrace();
	    return "/common/login";
	}
    }

    /**
     * Performs logout.
     * 
     * @return navigation page
     */
    public String logout() {
	FacesContext context = FacesContext.getCurrentInstance();
	HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

	try {
	    request.logout();
	    // clear the session
	    ((HttpSession) context.getExternalContext().getSession(false)).invalidate();
	    JsfUtil.addSuccessMessage("Logout was successful.");
	    if (user.getRoles().contains(Role.CUSTOMER)) {
		droolsCustomerLogActions.customerLogOut(user);
	    }
	    this.user = null;
	} catch (ServletException ex) {
	    JsfUtil.addErrorMessage("Logout was unsuccessful.");
	}
	return "";

    }

    /**
     * Returns true if user is logged.
     * 
     * @return true if user is logged, false otherwise
     */
    public boolean isLogged() {
	return (getUser() == null) ? false : true;
    }

    /**
     * Returns username.
     * 
     * @return username
     */
    public String getUsername() {
	return this.username;
    }

    /**
     * Sets username.
     * 
     * @param username
     */
    public void setUsername(String username) {
	this.username = username;
    }

    /**
     * Returns password.
     * 
     * @return password
     */
    public String getPassword() {
	return this.password;
    }

    /**
     * Sets password.
     * 
     * @param password
     */
    public void setPassword(String password) {
	this.password = password;
    }

    /**
     * Returns user.
     * 
     * @return user
     */
    public User getUser() {
	return this.user;
    }

    // HELPTING METHODS:
    public String loginRick() {
	this.username = "rick";
	this.password = "1234";
	return login();
    }

    public String loginHugo() {
	this.username = "hugo";
	this.password = "1234";
	return login();
    }

}
