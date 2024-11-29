/*
 * Copyright 2020-2022 Pavel Ponec, https://github.com/pponec/demo-ajax
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
import org.jetbrains.annotations.Nullable;
import org.ujorm.tools.web.Element;
import org.ujorm.tools.web.Html;
import org.ujorm.tools.web.HtmlElement;
import org.ujorm.tools.web.ajax.JavaScriptWriter;
import org.ujorm.tools.web.ao.HttpParameter;
import org.ujorm.tools.web.json.JsonBuilder;
import org.ujorm.tools.web.request.RContext;
import org.ujorm.tools.xml.config.HtmlConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.time.Month;
import java.util.logging.Logger;

import static net.ponec.demo.servlet.ComboBoxServlet.Attrib.MONTH;
import static net.ponec.demo.servlet.ComboBoxServlet.Attrib.TEXT;
import static net.ponec.demo.servlet.ComboBoxServlet.Constants.*;

/**
 * A live example of the HtmlElement inside a Servlet powered by a ujo-web library.
 *
 * @author Pavel Ponec
 * @see <a href=https://github.com/pponec/demo-ajax">github.com/pponec/demo-ajax</a>
 */
@WebServlet("/combo-box")
public class ComboBoxServlet extends AbstractServlet {
    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(ComboBoxServlet.class.getName());

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param context servlet request context
     */
    @Override
    public void doGet(RContext context) {

        try (HtmlElement html = HtmlElement.of(context, HtmlConfig.ofTitle("Combo-box tester"))) {
            html.addCssLink("/css/regexp.css");
            writeJavaScript(html, AJAX_ENABLED);
            Message msg = createResultMessage(context);
            try (Element body = html.addBody()) {
                body.addHeading(html.getTitle());
                body.addDiv(SUBTITLE_CSS).addText(AJAX_ENABLED ? AJAX_READY_MSG : "");
                try (Element form = body.addForm()
                        .setMethod(Html.V_POST).setAction("?")) {
                    createComboBox(form, MONTH, Month.class, MONTH.of(context, "[month]"));
                    form.addTextArea(CONTROL_CSS)
                            .setName(TEXT)
                            .setAttribute(Html.A_PLACEHOLDER, "Plain Text")
                            .addText(TEXT.of(context));
                    form.addDiv().addButton("btn", "btn-primary").addText("Evaluate");
                    form.addDiv(CONTROL_CSS, OUTPUT_CSS).addRawText(msg);
                }
            }
        }
    }

    protected <V extends Enum<V>> void createComboBox(
            @NotNull Element parent,
            @NotNull HttpParameter parameter,
            @NotNull Class<V> type,
            @Nullable String value
    ) {
        final Element select = parent.addSelect("form-select", "form-select-lg", "mb-3")
                .setName(parameter)
                .setAttribute("onchange", "f1.process(null)");
        for (Enum enumItem : type.getEnumConstants()) {
            select.addOption()
                    .setValue(enumItem.name())
                    .setAttribute(Html.A_SELECTED, enumItem.name().equals(value) ? Html.A_SELECTED : null)
                    .addText(enumItem.name());
        }
    }

    @NotNull
    protected JsonBuilder doAjax(RContext context, JsonBuilder output)
            throws IOException {
            final Message msg = createResultMessage(context);
            output.writeClass(OUTPUT_CSS, e -> e.addElementIf(msg.isError(), Html.SPAN, "error")
                    .addRawText(msg));
            output.writeClass(SUBTITLE_CSS, AJAX_READY_MSG);
            return output;
    }

    /** Build a HTML result message */
    protected Message createResultMessage(RContext context) {
        return Message.of(
                MONTH.of(context, "[month]") + ":",
                TEXT.of(context, "?"));
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
