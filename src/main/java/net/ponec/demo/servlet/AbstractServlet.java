/*
 * Copyright 2020-2024 Pavel Ponec, https://github.com/pponec/demo-ajax
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ponec.demo.servlet;

import org.jetbrains.annotations.NotNull;
import org.ujorm.tools.web.HtmlElement;
import org.ujorm.tools.web.json.JsonBuilder;
import org.ujorm.tools.web.request.RContext;
import org.ujorm.tools.xml.config.HtmlConfig;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ujorm.tools.web.ajax.JavaScriptWriter.DEFAULT_AJAX_REQUEST_PARAM;

/**
 * A live example of the HtmlElement inside a Servlet using a ujo-web library.
 *
 * @author Pavel Ponec
 */
public abstract class AbstractServlet extends HttpServlet {
    /** Logger */
    protected static final Logger LOGGER = Logger.getLogger(AbstractServlet.class.getName());

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected final void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response) throws IOException {
        try {
            doGet(RContext.ofServlet(request, response));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            int httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            String message = String.format("%s: %s",  e.getClass().getSimpleName(), e.getMessage());

            response.setStatus(httpStatus);
            response.setHeader("Error-Message", message);
            response.getWriter().write(message);
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param requestContext servlet request and response
     * @throws Exception if a servlet-specific error occurs
     */
    abstract protected void doGet(RContext requestContext);

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param input servlet request
     * @param output servlet response
     * @throws IOException if an I/O error occurs
     */
    protected final void doPost(HttpServletRequest input, HttpServletResponse output) {
        Map<String, String[]> map = input.getParameterMap();
        LOGGER.info("" + map);
        try {
            doPost(RContext.ofServlet(input, output));
        } catch (Exception e) {
            int httpStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            output.setStatus(httpStatus, "Internal error");
            String msg = String.format("%s: %s", e.getCause(), e.getMessage());
            LOGGER.log(Level.SEVERE, msg, e);
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param context servlet response
     * @throws Exception if an I/O error occurs
     */
    protected void doPost(RContext context) throws Exception {
        if (DEFAULT_AJAX_REQUEST_PARAM.of(context, false)) {
            doAjax(context, JsonBuilder.of(context, (HtmlConfig) HtmlConfig.ofEmptyElement()
                    .setNewLine(" ")))
                    .close();
        } else {
            doGet(context);
        }
    }

    @NotNull
    protected JsonBuilder doAjax(RContext context, JsonBuilder output) throws Exception {
        return output;
    }

    /** Create new HTML element */
    protected @NotNull HtmlElement getHtmlElement(
            RContext context,
            HtmlConfig config) throws UnsupportedEncodingException {
        return HtmlElement.of(context, config);
    }

}
