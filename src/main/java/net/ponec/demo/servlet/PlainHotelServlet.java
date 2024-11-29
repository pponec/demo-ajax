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
import org.ujorm.tools.web.ao.HttpParameter;
import org.ujorm.tools.web.report.ReportBuilder;
import org.ujorm.tools.web.request.RContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import static net.ponec.demo.servlet.PlainHotelServlet.Attrib.CITY;
import static net.ponec.demo.servlet.PlainHotelServlet.Attrib.NAME;
import static net.ponec.demo.servlet.PlainHotelServlet.Constants.DEFAULT_ROW_LIMIT;
import static net.ponec.demo.servlet.PlainHotelServlet.Constants.HOTELBASE;

/**
 * A simple example of the ReportBuilder class of Ujorm framework inside a Servlet.
 *
 * @author Pavel Ponec
 */
@WebServlet("/plainHotels")
public class PlainHotelServlet extends AbstractServlet {
    /** A hotel service */
    private final HotelService service = new HotelService();

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param context servlet request context
     */
    @Override
    protected void doGet(RContext context) {

        new ReportBuilder<Hotel>("Simple Hotel Report")
                .add(hotel -> hotel.getName(), "Hotel", NAME).sortable(true)
                .add(hotel -> hotel.getCity().getName(), "City", CITY).sortable()
                .add(hotel -> hotel.getStreet(), "Street").sortable()
                .add(hotel -> hotel.getPrice(), "Price").sortable()
                .add(hotel -> hotel.getCurrency(), "Currency")
                .add(hotel -> hotel.getPhone(), "Phone")
                .add(hotel -> hotel.getStars(), "Stars").sortable()
                .setFooter(e -> e.addText("Data source: ").addLinkedText(HOTELBASE, HOTELBASE))
                .build(context, builder -> service.selectHotels(builder,
                                DEFAULT_ROW_LIMIT,
                                NAME.of(context),
                                CITY.of(context)));
    }

    /**
     * HTTP attributes
     */
    enum Attrib implements HttpParameter {
        NAME,
        CITY;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /** Servlet constants */
    static class Constants {
        /** Row limit */
        static final int DEFAULT_ROW_LIMIT = 10;
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

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param context servlet request context
     */
    @Override
    protected void doPost(RContext context) {
        doGet(context);
    }
}
