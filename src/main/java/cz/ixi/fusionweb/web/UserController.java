package cz.ixi.fusionweb.web;
import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cz.ixi.fusionweb.ejb.UserBean;
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
		HttpServletRequest request = (HttpServletRequest) context
				.getExternalContext().getRequest();
		try {
			if (request.getUserPrincipal() != null) {
				request.logout();
			}
			request.login(this.username, this.password);
			System.out.println("Logged " + username);;
			this.user = this.users.getUserByUsername(this.username);
			JsfUtil.addSuccessMessage("Login was successful.");
			return "/index";
		} catch (ServletException ex) {
		    
			JsfUtil.addErrorMessage("Login was unsuccessful.");
			System.out.println("Not logged, attempt was " + this.username + " "
					+ this.password);
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
		HttpServletRequest request = (HttpServletRequest) context
				.getExternalContext().getRequest();

		try {
			this.user = null;
			request.logout();
			// clear the session
			((HttpSession) context.getExternalContext().getSession(false))
					.invalidate();
			JsfUtil.addSuccessMessage("Logout was successful.");

		} catch (ServletException ex) {
			JsfUtil.addErrorMessage("Logout was unsuccessful.");
		}
		return "/index";

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
}
