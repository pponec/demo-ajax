/*
 * Copyright 2020-2022 Pavel Ponec, https://github.com/pponec
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

import net.ponec.demo.model.Hotel;
import net.ponec.demo.service.HotelService;
import org.ujorm.tools.web.Element;
import org.ujorm.tools.web.Html;
import org.ujorm.tools.web.ao.Column;
import org.ujorm.tools.web.ao.HttpParameter;
import org.ujorm.tools.web.report.ReportBuilder;
import org.ujorm.tools.web.request.RContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.stream.Stream;

import static net.ponec.demo.servlet.HotelServlet.Attrib.*;
import static net.ponec.demo.servlet.HotelServlet.Constants.CSS_INPUT;
import static net.ponec.demo.servlet.HotelServlet.Constants.DEFAULT_ROW_LIMIT;
import static org.ujorm.tools.xml.AbstractWriter.NBSP;

/**
 * An example of the ReportBuilder class of Ujorm framework inside a Servlet.
 *
 * @author Pavel Ponec
 */
@WebServlet("/hotels")
public class HotelServlet extends AbstractServlet {
    /** A hotel service */
    private final HotelService service = new HotelService();

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param context servlet request context
     */
    @Override
    protected void doGet(RContext context) {

        new ReportBuilder<Hotel>("Common Hotel Report")
                .addOrder("Ord.")
                .add(Hotel::getName, "Hotel", NAME).sortable(true)
                .add(hotel -> hotel.getCity().getName(), "City", CITY).sortable()
                .add(Hotel::getStreet, "Street").sortable()
                .add(Hotel::getPrice, "Price").sortable()
                .add(Hotel::getCurrency, "Currency")
                .add(Hotel::getPhone, "Phone")
                .add(starColumn(), "Stars").sortable()
                .addColumn(
                        (e, v) -> e.addLinkedText(v.getHomePage(), "link"), // Data
                        (e) -> e.addText("Home page", " ").addImage(Constants.HELP_IMG, "Help")) // Title
                .setFormItem(e -> e.addTextInp(LIMIT, LIMIT.of(context), "Limit", CSS_INPUT, LIMIT))
                .setFooter(e -> printFooter(e))
                .setHtmlHeader(e -> e.addLink().setHref("/css/hotels.css").setAttr(Html.A_REL, "stylesheet"))
                .setAjaxEnabled(true) // Default
                .build(context, builder -> service.selectHotels(builder,
                                LIMIT.of(context, DEFAULT_ROW_LIMIT),
                                NAME.of(context),
                                CITY.of(context)));
    }

    /** Create a column of hotel stars */
    protected Column<Hotel> starColumn() {
        return new Column<Hotel>() {
            @Override
            public void write(Element e, Hotel hotel) {
                e.setAttribute(Html.A_TITLE, hotel.getStars()).setAttribute(Html.STYLE, "color: Gold");
                Stream.generate(() -> "🟊" + NBSP).limit(Math.round(hotel.getStars()))
                        .forEach(s -> e.addText(s));
            }
            /** Implement it for a sortable column only */
            @Override
            public Float apply(Hotel hotel) {
                return hotel.getStars();
            }
        };
    }

    /**  Data are from hotelsbase.org, see the original license */
    protected void printFooter(final Element body) throws IllegalStateException {
        body.addText("Data are from", " ")
                .addLinkedText(Constants.HOTELBASE, "hotelsbase.org");
        body.addText(", ", "see an original", " ")
                .addLinkedText(Constants.DATA_LICENSE, "license");
    }

    /**
     * HTTP attributes
     */
    enum Attrib implements HttpParameter {
        NAME,
        CITY,
        LIMIT { @Override public String defaultValue() { return DEFAULT_ROW_LIMIT.toString(); }};

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param context servlet request context
     */
    @Override
    protected void doPost(final RContext context) {
        doGet(context);
    }

    /** Servlet constants */
    static class Constants {
        /** Row limit */
        static final Integer DEFAULT_ROW_LIMIT = 15;
        /** CSS for inputs */
        static final String CSS_INPUT = "form-control";
        /** Help image */
        static final String HELP_IMG = "images/help.png";
        /** Data license */
        static final String HOTELBASE = "http://hotelbase.org/";
        /** Data license */
        static final String DATA_LICENSE = "https://web.archive.org/web/20150407085757/"
                + "http://api.hotelsbase.org/documentation.php";
    }

}
