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
import net.ponec.demo.service.RegexpService;
import org.jetbrains.annotations.NotNull;
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
import java.util.logging.Logger;
import static net.ponec.demo.servlet.RegexpServlet.Attrib.REGEXP;
import static net.ponec.demo.servlet.RegexpServlet.Attrib.TEXT;
import static net.ponec.demo.servlet.RegexpServlet.Constants.*;

/**
 * A live example of the HtmlElement inside a Servlet using a ujo-web library.
 *
 * @author Pavel Ponec
 * @see <a href=https://github.com/pponec/demo-ajax">github.com/pponec/demo-ajax</a>
 */
@WebServlet("/regexp")
public class RegexpServlet extends AbstractServlet {
    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(RegexpServlet.class.getName());
    /** A service */
    private final RegexpService service = new RegexpService();

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param context servlet request
     */
    @Override
    protected void doGet(RContext context) {

        try (HtmlElement html = HtmlElement.of(context, HtmlConfig.ofTitle("Regular expression tester"))) {
            html.addCssLink("/css/regexp.css");
            writeJavaScript(html, AJAX_ENABLED);
            Message msg = highlight(context);
            try (Element body = html.addBody()) {
                body.addHeading(html.getTitle());
                body.addDiv(SUBTITLE_CSS).addText(AJAX_ENABLED ? AJAX_READY_MSG : "");
                try (Element form = body.addForm()
                        .setMethod(Html.V_POST).setAction("?")) {
                    form.addInput(CONTROL_CSS)
                            .setNameValue(REGEXP, REGEXP.of(context))
                            .setAttribute(Html.A_PLACEHOLDER, "Regular expression");
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

    @NotNull
    protected JsonBuilder doAjax(RContext context, JsonBuilder output) throws IOException {
            final Message msg = highlight(context);
            output.writeClass(OUTPUT_CSS, e -> e.addElementIf(msg.isError(), Html.SPAN, "error")
                    .addRawText(msg));
            output.writeClass(SUBTITLE_CSS, AJAX_READY_MSG);
            return output;
    }

    /** Build a HTML result */
    protected Message highlight(RContext input) {
        return service.highlight(
                TEXT.of(input, ""),
                REGEXP.of(input, ""));
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
        REGEXP,
        TEXT;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
