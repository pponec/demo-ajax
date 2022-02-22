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

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ujorm.tools.web.Element;
import org.ujorm.tools.web.HtmlElement;
import org.ujorm.tools.web.ao.HttpParameter;
import static net.ponec.demo.servlet.FormServlet.Attrib.NOTE;

/**
 * A simple form element powered by ujo-web library.
 *
 * @author Pavel Ponec
 */
@WebServlet("/form-servlet")
public class FormServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try (HtmlElement html = HtmlElement.niceOf(response, "/css/regexp.css")) {
            try (Element body = html.addBody()) {
                body.addHeading("Simple form");
                try (Element form = body.addForm("form-inline")) {
                    form.addLabel("control-label").addText("Note:");
                    form.addInput("form-control", "col-lg-1")
                            .setNameValue(NOTE, NOTE.of(request));
                    form.addSubmitButton("btn", "btn-primary")
                            .addText("Submit");
                }
            }
        }
    }

    /** Servlet attributes */
    enum Attrib implements HttpParameter {
        NOTE;
        @Override public String toString() {
            return name().toLowerCase();
        }
    }
}
