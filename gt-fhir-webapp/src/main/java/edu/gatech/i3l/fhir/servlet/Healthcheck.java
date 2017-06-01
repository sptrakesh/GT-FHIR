package edu.gatech.i3l.fhir.servlet;

import edu.gatech.i3l.omop.dao.DAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

import static java.lang.String.format;

/**
 * Created by rakesh.vidyadharan on 6/1/17.
 */
public class Healthcheck extends HttpServlet {
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doOptions(req,resp);
        resp.addHeader("Allow", "GET,HEAD,OPTIONS");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        check(req.getServletPath(), resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        check(req.getServletPath(), resp);
    }

    private void check(final String path, final HttpServletResponse resp) throws ServletException, IOException {
        final String statement = "select now()";

        try
        {
            final Query query = DAO.getInstance().getEntityManager().createNativeQuery(statement);
            final String date = query.getSingleResult().toString();
            resp.setStatus(HttpServletResponse.SC_OK);
            output(path, date, resp);
        }
        catch (final Throwable throwable)
        {
            logger.warn(format("Error executing statement (%s)", statement), throwable);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            output(path, throwable.getMessage(), resp);
        }
    }

    private void output(final String path, final String message, final HttpServletResponse resp) throws ServletException, IOException {
        final Writer writer = resp.getWriter();

        switch (path) {
            case "/healthcheck":
                resp.setContentType("text/plain");
                writer.append(message).append("\r\n");
                break;
            default:
                resp.setContentType("text/html");
                writer.append("<html>")
                        .append("<head>").append("<title>FHIR Server</title>").append("</head>")
                        .append("<body>")
                        .append("<h1>Tempus</h1>")
                        .append("<h2>FHIR Server</h2>")
                        .append("<h3>Database time: ").append(message).append("</h3>")
                        .append("</body>")
                        .append("</html>");
        }

        writer.close();
    }

    private static final Logger logger = LoggerFactory.getLogger(Healthcheck.class);
}
