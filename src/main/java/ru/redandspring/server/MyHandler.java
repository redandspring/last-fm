package ru.redandspring.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.redandspring.config.PeriodCharts;
import ru.redandspring.services.ModificationPage;
import ru.redandspring.services.ServiceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyHandler extends AbstractHandler {

    private static final Logger log = LoggerFactory.getLogger(MyHandler.class);

    private final ModificationPage page = new ModificationPage();

    @Override
    public void handle(final String target, final Request request, final HttpServletRequest sr, final HttpServletResponse response)
            throws IOException {
        log.info("open page {}", target);

        try {
            switch (target){ // NOSONAR
                case "/open": openHandler(request, response, PeriodCharts.CURRENT_MONTH); break;
                case "/open-last": openHandler(request, response, PeriodCharts.PREV_MONTH); break;
                default: response.setStatus(HttpServletResponse.SC_FORBIDDEN); break;
            }
        } catch (ServiceException | IOException e) {
            showError(response, e);
        }
    }

    private void openHandler(final Request request, final HttpServletResponse response, final PeriodCharts period) throws ServiceException, IOException {

        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);

        final String html = page.resultPage(period);
        response.getWriter().println(html);
    }

    private void showError(final HttpServletResponse response, final Throwable e) throws IOException {
        log.error("Error:", e);
        response.getWriter().println("<h1>Что-то пошло не так</h1>" + e.getMessage());
    }
}
