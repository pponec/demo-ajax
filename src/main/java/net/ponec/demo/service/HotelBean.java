package net.ponec.demo.service;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import net.ponec.demo.model.Hotel;

/**
 * See a <a href="https://www.primefaces.org/showcase/ui/data/datatable/field.xhtml?jfwid=0837a">JSF Tutorial</a>
 * @author Pavel Ponec
 */
@ManagedBean
@ViewScoped
public class HotelBean {

    private int rowLimit = 100;

    private List<Hotel> hotels = new ArrayList<>();

    @PostConstruct
    private void postConstruct() {
        try {
            new HotelProvider().getHotels()
                    .limit(rowLimit)
                    .forEach(hotel -> hotels.add(hotel));
        } catch (IOException ex) {
            throw new IllegalStateException();
        }
    }

    public List<Hotel> getHotelList() {
        return hotels;
    }
}
