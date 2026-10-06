package com.secretshelf.location;

import java.util.Comparator;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping({"", "/"})
public class BookshopController {
    private final BookshopRepository shops;
    public BookshopController(BookshopRepository shops){this.shops=shops;}
    public record ShopResult(Long id,String name,String address,String city,double latitude,double longitude,String phone,double distanceKm,int sampleAvailability,String inventoryNote){}
    @Bean CommandLineRunner seedBookshops(){return args->{
        add("Librería El Faro","Calle 72 #10-34","Bogotá",4.6584,-74.0575,"+57 601 555 0101");
        add("La Página Secreta","Carrera 7 #45-20","Bogotá",4.6283,-74.0645,"+57 601 555 0102");
        add("Casa de Letras","Calle 93 #13-18","Bogotá",4.6767,-74.0481,"+57 601 555 0103");
    };}
    private void add(String n,String a,String c,double lat,double lon,String p){if(!shops.existsByName(n))shops.save(new Bookshop(n,a,c,lat,lon,p));}
    @GetMapping public List<ShopResult> nearby(@RequestParam(name="lat",defaultValue="4.7110") double lat,@RequestParam(name="lon",defaultValue="-74.0721") double lon,@RequestParam(name="q",defaultValue="") String q){
        return shops.findAll().stream().map(s->{double d=distance(lat,lon,s.getLatitude(),s.getLongitude());return new ShopResult(s.getId(),s.getName(),s.getAddress(),s.getCity(),s.getLatitude(),s.getLongitude(),s.getPhone(),Math.round(d*10.0)/10.0,2,"Disponibilidad de demostración; confirme existencias directamente con la librería.");}).filter(s->q.isBlank()||s.name().toLowerCase().contains(q.toLowerCase())||s.city().toLowerCase().contains(q.toLowerCase())).sorted(Comparator.comparingDouble(ShopResult::distanceKm)).toList();
    }
    private double distance(double a,double b,double c,double d){double r=6371, x=Math.toRadians(c-a), y=Math.toRadians(d-b);double h=Math.sin(x/2)*Math.sin(x/2)+Math.cos(Math.toRadians(a))*Math.cos(Math.toRadians(c))*Math.sin(y/2)*Math.sin(y/2);return 2*r*Math.atan2(Math.sqrt(h),Math.sqrt(1-h));}
}
