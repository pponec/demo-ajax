/*
 * Copyright 2020-2021 Pavel Ponec, https://github.com/pponec/demo-ajax
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

import net.ponec.demo.model.Message;
import org.jetbrains.annotations.NotNull;
import org.ujorm.tools.web.Element;
import org.ujorm.tools.web.Html;
import org.ujorm.tools.web.HtmlElement;
import org.ujorm.tools.web.ajax.JavaScriptWriter;
import org.ujorm.tools.web.ao.HttpParameter;
import org.ujorm.tools.web.json.JsonBuilder;
import org.ujorm.tools.xml.config.HtmlConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Month;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.ponec.demo.servlet.ComboBoxServlet.Attrib.*;
import static net.ponec.demo.servlet.ComboBoxServlet.Constants.*;
import static org.ujorm.tools.web.ajax.JavaScriptWriter.DEFAULT_AJAX_REQUEST_PARAM;

/**
 * A live example of the HtmlElement inside a Servlet using a ujo-web library.
 *
 * @author Pavel Ponec
 * @see <a href=https://github.com/pponec/demo-ajax">github.com/pponec/demo-ajax</a>
 */
@WebServlet("/combo-box")
public class ComboBoxServlet extends HttpServlet {
    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(ComboBoxServlet.class.getName());

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param input servlet request
     * @param output servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(
            final HttpServletRequest input,
            final HttpServletResponse output)
            throws ServletException, IOException {

        try (HtmlElement html = HtmlElement.of(input, output, getConfig("Combo-box tester"))) {
            html.addCssLink("/css/regexp.css");
            writeJavaScript(html, AJAX_ENABLED);
            Message msg = createResultMessage(input);
            try (Element body = html.addBody()) {
                body.addHeading(html.getTitle());
                body.addDiv(SUBTITLE_CSS).addText(AJAX_ENABLED ? AJAX_READY_MSG : "");
                try (Element form = body.addForm()
                        .setMethod(Html.V_POST).setAction("?")) {
                    createComboBox(form, MONTH, Month.JANUARY);
                    form.addTextArea(CONTROL_CSS)
                            .setName(TEXT)
                            .setAttribute(Html.A_PLACEHOLDER, "Plain Text")
                            .addText(TEXT.of(input));
                    form.addDiv().addButton("btn", "btn-primary").addText("Evaluate");
                    form.addDiv(CONTROL_CSS, OUTPUT_CSS).addRawText(msg);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Internal server error", e);
            output.setStatus(500);
        }
    }

    protected <V extends Enum<V>> void createComboBox(
            @NotNull Element parent,
            @NotNull HttpParameter parameter,
            @NotNull V value) {
        final Element select = parent.addSelect()
                .setName(parameter)
                .setAttribute("required", true)
                .setAttribute("onchange", "this.form.submit()");
        for (Enum item : value.getClass().getEnumConstants()) {
            select.addOption()
                    .setValue(item.name())
                    .setAttribute(Html.A_SELECTED, item == value ? Html.A_SELECTED : null)
                    .addText(item.name());
        }
    }

    @Override
    protected void doPost(HttpServletRequest input, HttpServletResponse output)
            throws ServletException, IOException {
        if (DEFAULT_AJAX_REQUEST_PARAM.of(input, false)) {
            doAjax(input, JsonBuilder.of(input, output, getConfig("[month]"))).close();
        } else {
            doGet(input, output);
        }
    }

    @NotNull
    protected JsonBuilder doAjax(HttpServletRequest input, JsonBuilder output)
            throws ServletException, IOException {
            final Message msg = createResultMessage(input);
            output.writeClass(OUTPUT_CSS, e -> e.addElementIf(msg.isError(), Html.SPAN, "error")
                    .addRawText(msg));
            output.writeClass(SUBTITLE_CSS, AJAX_READY_MSG);
            return output;
    }

    /** Build a HTML result message */
    protected Message createResultMessage(HttpServletRequest input) {
        return Message.of(
                MONTH.of(input, "?") + ":",
                TEXT.of(input, "?"));
    }

    /** Write a Javascript to a header */
    protected void writeJavaScript(@NotNull final HtmlElement html,
            final boolean enabled) {
        if (enabled) {
            new JavaScriptWriter(Html.INPUT, Html.TEXT_AREA)
                    .setSubtitleSelector("." + SUBTITLE_CSS)
                    .write(html.getHead());
        }
    }

    /** Create a configuration for a HTML model */
    protected HtmlConfig getConfig(@NotNull String title) {
        return HtmlConfig.ofDefault()
                .setTitle(title)
                .setNiceFormat();
    }

    /** CSS constants and identifiers */
    static class Constants {
        /** Bootstrap form control CSS class name */
        static final String CONTROL_CSS = "form-control";
        /** CSS class name for the output box */
        static final String OUTPUT_CSS = "out";
        /** CSS class name for the output box */
        static final String SUBTITLE_CSS = "subtitle";
        /** Enable AJAX feature */
        static final boolean AJAX_ENABLED = true;
        /** AJAX ready message */
        static final String AJAX_READY_MSG = "AJAX ready";
    }

    /** Servlet attributes */
    enum Attrib implements HttpParameter {
        MONTH,
        TEXT;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

}
