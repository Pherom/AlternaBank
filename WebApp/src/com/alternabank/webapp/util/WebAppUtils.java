package com.alternabank.webapp.util;

import com.alternabank.engine.Engine;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.engine.transaction.TransactionManager;
import com.alternabank.engine.xml.XMLFileLoader;
import com.alternabank.engine.xml.XMLLoader;
import com.google.gson.Gson;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class WebAppUtils {

    public static final Gson GSON_INSTANCE = new Gson();

    public static Engine getEngine(ServletContext servletContext) {
        if (servletContext.getAttribute("engine") == null) {
            servletContext.setAttribute("engine", new Engine());
        }
        return (Engine) servletContext.getAttribute("engine");
    }

    public static TimeManager getTimeManager(ServletContext servletContext) {
        return getEngine(servletContext).getTimeManager();
    }

    public static CustomerManager getCustomerManager(ServletContext servletContext) {
        return getEngine(servletContext).getCustomerManager();
    }

    public static LoanManager getLoanManager(ServletContext servletContext) {
        return getEngine(servletContext).getLoanManager();
    }

    public static TransactionManager getTransactionManager(ServletContext servletContext) {
        return getEngine(servletContext).getTransactionManager();
    }

    public static XMLLoader getXMLFileLoader(ServletContext servletContext) {
        return getEngine(servletContext).getXmlFileLoader();
    }

    public static String getUsername(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute("username") : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute("admin") : null;
        return sessionAttribute != null && Boolean.parseBoolean(sessionAttribute.toString());
    }

}
