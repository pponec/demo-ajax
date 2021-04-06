package net.ponec.demo.service;

import java.io.BufferedReader;
import net.ponec.demo.model.Hotel;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Pavel Ponec
 */
public class HotelProvider {

    /** Logger */
    private static final Logger LOGGER = Logger.getLogger(HotelProvider.class.toString());
    private static final String HOTELS_CSV = "/csv/ResourceHotel.csv";

    private final CityResourceProvider cityService = new CityResourceProvider();

    private List<Hotel> hotels = null;

    public Stream<Hotel> getHotels() throws IOException {
        if (hotels == null) {
            synchronized (this) {
                try (Stream<Hotel> hotelSteam = loadHotelStream()) {
                    hotels = hotelSteam.collect(Collectors.toList());
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Hotel reading fails", e);
                    return Stream.empty();
                }
            }
        }
        return hotels.stream();
    }

    /** Direct stream of data source */
    public Stream<Hotel> loadHotelStream() {
        try {
            return loadHotels(getClass().getResource(HOTELS_CSV));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Return a raw stream
     *
     * ( Hotel.NAME
     * , Hotel.NOTE
     * , Hotel.CITY.add(City.ID) // The value is a foreign key!
     * , Hotel.STREET
     * , Hotel.PHONE
     * , Hotel.STARS
     * , Hotel.HOME_PAGE
     * , Hotel.PRICE
     * , Hotel.ACTIVE
     * @return
     */
    public Stream<Hotel> loadHotels(URL url) throws IOException {
        return rowsOfUrl(url)
                .filter(t -> !t.startsWith("* "))
                .filter(t -> !t.startsWith("NAME;"))
                .map(t -> {
                    Hotel hotel = null;
                    String[] c = t.split(";");
                    if (c.length > 8) {
                        hotel = new Hotel();
                        hotel.setName(c[0]);
                        hotel.setNote(c[1]);
                        hotel.setCity(cityService.getCity(c[2]));
                        hotel.setStreet(c[3]);
                        hotel.setPhone(c[4]);
                        hotel.setStars(Float.parseFloat(c[5]));
                        hotel.setHomePage(c[6]);
                        hotel.setPrice(new BigDecimal(c[7]));
                        hotel.setCurrency("USD");
                        hotel.setActive(Boolean.parseBoolean(c[8]));
                    }
                    return hotel;
                })
                .filter(t -> t != null);
    }

    /** Returns a stream of lines form URL resource
     *
     * @param url An URL link to a resource
     * @return The customer is responsible for closing the stream.
     *         During closing, an IllegalStateException may occur due to an IOException.
     * @throws IOException
     */
    public static Stream<String> rowsOfUrl(final URL url) throws IOException  {
        final InputStream is = url.openStream();
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().onClose(() -> {
            try {
                is.close();
            } catch (IOException e) {
                throw new IllegalStateException("Can't close: " + url, e);
            }
        });
    }

}
