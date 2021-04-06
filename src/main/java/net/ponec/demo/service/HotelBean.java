package net.ponec.demo.service;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import net.ponec.demo.model.Hotel;

@ManagedBean
@ViewScoped
public class HotelBean {

    private int rowLimit = 12;

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
