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

import net.ponec.demo.service.RegexpService;
import org.ujorm.tools.web.AbstractHtmlElement;
import org.ujorm.tools.web.Html;
import org.ujorm.tools.web.HtmlElement;
import org.ujorm.tools.web.request.HttpContext;

import jakarta.servlet.annotation.WebServlet;
import java.util.logging.Logger;

/**
 * A live example of the HtmlElement inside a Servlet using a ujo-web library.
 *
 * @author Pavel Ponec
 * @see <a href=https://github.com/pponec/demo-ajax">github.com/pponec/demo-ajax</a>
 */
@WebServlet({"/menu", ""})
public class MenuServlet extends AbstractServlet {
    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(MenuServlet.class.getName());
    /** A service */
    private final RegexpService service = new RegexpService();
    /** Max length of the text area */
    private final boolean showGenerator = false;

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param context servlet request
     */
    @Override
    protected void doGet(HttpContext context) {
        var title = "Menu";
        try (HtmlElement html = AbstractHtmlElement.of(title, context, "/css/regexp.css")) {
            try (var body = html.addBody()) {
                body.addHeading("Simple AJAX demo based on Java");
                try (var ul = body.addElement(Html.UL)) {
                    ul.addElement(Html.LI)
                            .addAnchor("/form-servlet?note=any+idea+...")
                            .addText("Simple form");
                    ul.addElement(Html.LI)
                            .addAnchor("/regexp?regexp=%5Baeiyou%5D&text=%22Write+once%2C+run+anywhere.%22+--+Sun+Microsystems")
                            .addText("Regular expression tester");
                    ul.addElement(Html.LI)
                            .addAnchor("/plainHotels?name=ro&city=gu")
                            .addText("Plain Hotel report");
                    ul.addElement(Html.LI)
                            .addAnchor("/hotels?name=ro&city=gu")
                            .addText("Common Hotel report");
                    ul.addElement(Html.LI)
                            .addAnchor("/combo-box?text=My+description")
                            .addText("Combo-box");
                    ul.addElement(Html.LI)
                            .addAnchor("/tutorial")
                            .addText("Tutorial");
                    if (showGenerator) {
                        ul.addElement(Html.LI)
                                .addAnchor("/element")
                                .addText("Convert HTML to Elements");
                    }
                }
                body.addElement(Html.HR);
                try (var div = body.addDiv("footer")) {
                    div.addText("See the ");
                    div.addAnchor("https://github.com/pponec/demo-ajax")
                            .addText("home page");
                    div.addText(" for more information");
                }
            }
        }
    }
}
