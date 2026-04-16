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
import org.ujorm.tools.converter.HtmlToJavaConverter;
import org.ujorm.tools.web.AbstractHtmlElement;
import org.ujorm.tools.web.Element;
import org.ujorm.tools.web.Html;
import org.ujorm.tools.web.HtmlElement;
import org.ujorm.tools.web.ajax.JavaScriptWriter;
import org.ujorm.tools.web.ao.HttpParameter;
import org.ujorm.tools.web.json.JsonBuilder;
import org.ujorm.tools.web.request.HttpContext;

import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.logging.Logger;

import static net.ponec.demo.servlet.ElementConverterServlet.Attrib.HTML_TEXT;
import static net.ponec.demo.servlet.ElementConverterServlet.Constants.*;
import static org.ujorm.tools.common.StringUtils.formatSeparator;

/**
 * The Main Menu.
 * @author Pavel Ponec
 */
@WebServlet({"/element", "/gen", "/g"})
public class ElementConverterServlet extends AbstractServlet {
    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(ElementConverterServlet.class.getName());
    /** A service */
    private final HtmlToJavaConverter service = new HtmlToJavaConverter();
    /** Max length of the text area */
    private final int inputMaxLength = 100_000;
    /** Default HTML value */
    private final String htmlDefault = "<html/>";

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param context servlet request
     */
    @Override
    protected void doGet(HttpContext context) {
        var title = "HTML to Java Elements Converter";
        try (HtmlElement html = AbstractHtmlElement.niceOf(title, context, "/css/regexp.css")) {
            writeJavaScript(html, AJAX_ENABLED);
            final boolean inlineStyle = context.getParameter(INLINE, true, Boolean::parseBoolean);
            final Message msg = highlight(context, inlineStyle);
            try (Element body = html.addBody()) {
                body.addHeading(html.getTitle());
                printAjaxWarningMessage(body);
                body.addDiv(SUBTITLE_CSS).addText(AJAX_ENABLED ? AJAX_READY_MSG : "");
                try (Element form = body.addForm().setMethod(Html.V_POST).setAction("?")) {
                    form.addTextArea(CONTROL_CSS)
                            .setName(HTML_TEXT)
                            .setAttribute(Html.A_PLACEHOLDER, "HTML Text")
                            .setAttribute(Html.A_MAXLENGTH, inputMaxLength + 1)
                            .addText(HTML_TEXT.of(context, htmlDefault));
                    form.addLabel(INLINE).addText("Inline style:")
                            .addCheckBox(INLINE)
                            .setCheckBoxValue(inlineStyle);
                    try(Element btnBar = form.addDiv()) {
                        btnBar.addButton("btn", "btn-primary")
                                .setAttribute(Html.A_TITLE, "Convert HTML code to Java Elements")
                                .setAttribute(Html.A_TYPE, Html.V_SUBMIT)
                                .addText("⚙️ Convert to Elements");
                        btnBar.addButton("btn", "copy")
                                .setAttribute(Html.A_TITLE, "Copy to Clipborad")
                                .setAttribute(Html.A_TYPE, "button")
                                .setAttribute(Html.A_ONCLICK, "navigator.clipboard.writeText(document.querySelector('.%s').innerText);alert('Copied!')"
                                        .formatted(RESULT_CSS))
                                .addText("📋 Copy");
                    }
                    form.addDiv(CONTROL_CSS, RESULT_CSS)
                            .addDiv(msg.isError() ? ERROR_CSS : OUTPUT_CSS).addText(msg);
                }

            }
        }
    }

    @NotNull
    protected JsonBuilder doAjax(HttpContext context, JsonBuilder output) throws IOException {
        final boolean inlineStyle = context.getParameter(INLINE, true, Boolean::parseBoolean);
        final Message msg = highlight(context, inlineStyle);
        output.writeClass(CONTROL_CSS, e -> e
                .addDiv(msg.isError() ? ERROR_CSS : OUTPUT_CSS).addText(msg));
        output.writeClass(SUBTITLE_CSS, AJAX_READY_MSG);
        return output;
    }

    /** Build a HTML result */
    protected Message highlight(HttpContext input, boolean inlineStyle) {
        var htmlInput = HTML_TEXT.of(input, htmlDefault);
        if (htmlInput.length() > inputMaxLength) {
            return Message.of(new IllegalArgumentException(getLengthWarningMessage()));
        }
        try {
            return Message.of(service.convertHtmlToJavaElements(htmlInput, !inlineStyle));
        } catch (Exception ex) {
            return Message.of(ex);
        }
    }

    private String getLengthWarningMessage() {
        return """
                On this page, the HTML code must not exceed %s characters.
                To convert longer texts, please use the %s class from the Ujorm framework.
                """.formatted(formatSeparator(inputMaxLength), service.getClass().getSimpleName());
    }

    private void printAjaxWarningMessage(Element element) {
        if (!AJAX_ENABLED) {
            String msg = """
                    AJAX is temporarily disabled to reduce server load.
                    Please use the submit button.
                    """;
            element.addDiv("warning").addText(msg);
        }
    }

    /** Write a Javascript to a header */
    protected void writeJavaScript(@NotNull final HtmlElement html, final boolean enabled) {
        if (enabled) {
            new JavaScriptWriter()
                    .setSubtitleSelector("." + SUBTITLE_CSS)
                    .write(html.getHead());
        }
    }

    /** CSS constants and identifiers */
    static class Constants {
        /** Bootstrap form control CSS class name */
        static final String CONTROL_CSS = "form-control";
        /** ID + NAME + CSS for the inline checkbox */
        static final String INLINE = "box-style";
        /** Bootstrap form control CSS class name */
        static final String RESULT_CSS = "result";
        /** CSS class name for the output box */
        static final String OUTPUT_CSS = "out";
        /** CSS class name for the error case */
        static final String ERROR_CSS = "error";
        /** Generate block style or fluent style */
        static final String BLOCK_ID = "block-style";
        /** CSS class name for the output box */
        static final String SUBTITLE_CSS = "subtitle";
        /** Enable AJAX feature */
        static final boolean AJAX_ENABLED = false;
        /** AJAX ready message */
        static final String AJAX_READY_MSG = "AJAX ready";
    }

    /** Servlet attributes */
    enum Attrib implements HttpParameter {
        HTML_TEXT;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
}
