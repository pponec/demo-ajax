/*
 * Copyright 2020-2021 Pavel Ponec, https://github.com/pponec
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

import net.ponec.demo.service.HotelService;
import net.ponec.demo.model.Hotel;
import java.io.IOException;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ujorm.tools.web.ao.HttpParameter;
import org.ujorm.tools.web.report.ReportBuilder;
import static net.ponec.demo.servlet.PlainHotelServlet.Attrib.*;
import static net.ponec.demo.servlet.PlainHotelServlet.Constants.*;
import org.ujorm.tools.web.table.GridBuilder;

/**
 * A simple example of the ReportBuilder class of Ujorm framework inside a Servlet.
 *
 * @author Pavel Ponec
 */
@WebServlet("/plainHotels")
public class PlainHotelServlet extends HttpServlet {
    /** A hotel service */
    private final HotelService service = new HotelService();

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param input servlet request
     * @param output servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(
            final HttpServletRequest input,
            final HttpServletResponse output) throws ServletException, IOException {

        new ReportBuilder<Hotel>("Simple Hotel Report")
                .add(hotel -> hotel.getName(), "Hotel", NAME).sortable(true)
                .add(hotel -> hotel.getCity().getName(), "City", CITY).sortable()
                .add(hotel -> hotel.getStreet(), "Street").sortable()
                .add(hotel -> hotel.getPrice(), "Price").sortable()
                .add(hotel -> hotel.getCurrency(), "Currency")
                .add(hotel -> hotel.getPhone(), "Phone")
                .add(hotel -> hotel.getStars(), "Stars").sortable()
                .setFooter(e -> e.addText("Data source: ").addLinkedText(HOTELBASE, HOTELBASE))
                .build(input, output, builder -> selectHotels(builder,
                                DEFAULT_ROW_LIMIT,
                                NAME.of(input),
                                CITY.of(input)));
    }

    /**
     * The data source.
     *
     * @param builder An instance of ReportBuilder to get some more parameters
     * @param limit Row limit
     * @param namePattern A hotel name pattern
     * @param cityPattern A city name pattern
     */
    private Stream<Hotel> selectHotels(GridBuilder<Hotel> builder,
            int limit,
            @Nonnull String namePattern,
            @Nonnull String cityPattern) {
        final String nameUp = namePattern.toUpperCase();
        final String cityUp = cityPattern.toUpperCase();
        return service.loadHotelStream()
                .filter(t -> nameUp.isEmpty() || t.getName().toUpperCase().contains(nameUp))
                .filter(t -> cityUp.isEmpty() || t.getCityName().toUpperCase().contains(cityUp))
                .sorted(builder.getSortedColumn().getComparator(Hotel::getName))
                .limit(Math.max(0, limit));
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
        static final int DEFAULT_ROW_LIMIT = 15;
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
     * @param input servlet request
     * @param output servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(
            final HttpServletRequest input,
            final HttpServletResponse output) throws ServletException, IOException {
        doGet(input, output);
    }
}
