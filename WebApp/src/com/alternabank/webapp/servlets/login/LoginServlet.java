package com.alternabank.webapp.servlets.login;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.webapp.util.WebAppUtils;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private String loggedInAdminName = null;

    private boolean isUserAlreadyLoggedIn(HttpServletRequest req) {
        return WebAppUtils.getUsername(req) != null;
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        String adminLoginFromParameter = req.getParameter("admin");
        boolean adminLogin = false;
        if (adminLoginFromParameter != null && !adminLoginFromParameter.isEmpty())
            adminLogin = Boolean.parseBoolean(adminLoginFromParameter);
        if(!isUserAlreadyLoggedIn(req)) {
            CustomerManager customerManager = WebAppUtils.getCustomerManager(getServletContext());
            String usernameFromParameter = req.getParameter("username");
            if(usernameFromParameter == null || usernameFromParameter.isEmpty())
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            else {
                usernameFromParameter = usernameFromParameter.trim();
                synchronized (this) {
                    if (adminLogin && loggedInAdminName != null) {
                        String errorMessage = "An admin is already logged in.";
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        resp.getOutputStream().print(errorMessage);
                    }
                    else if (customerManager.customerExists(usernameFromParameter) || usernameFromParameter.equals(loggedInAdminName)) {
                        String errorMessage = String.format("Username %s already exists. Please enter a different username.", usernameFromParameter);
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        resp.getOutputStream().print(errorMessage);
                    }
                    else {
                        if (!adminLogin)
                            customerManager.createCustomer(usernameFromParameter);
                        else {
                            req.getSession(true).setAttribute("admin", true);
                            loggedInAdminName = usernameFromParameter;
                        }
                        req.getSession(true).setAttribute("username", usernameFromParameter);
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            }
        }
        else {
            resp.setStatus(HttpServletResponse.SC_OK);
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
