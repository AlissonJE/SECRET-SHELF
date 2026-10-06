package com.secretshelf.location;
import jakarta.persistence.*;
@Entity @Table(name="bookshops")
public class Bookshop {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String name;
    @Column(nullable=false) private String address;
    @Column(nullable=false) private String city;
    @Column(nullable=false) private double latitude;
    @Column(nullable=false) private double longitude;
    @Column(nullable=false) private String phone;
    protected Bookshop(){}
    public Bookshop(String name,String address,String city,double latitude,double longitude,String phone){this.name=name;this.address=address;this.city=city;this.latitude=latitude;this.longitude=longitude;this.phone=phone;}
    public Long getId(){return id;}public String getName(){return name;}public String getAddress(){return address;}public String getCity(){return city;}public double getLatitude(){return latitude;}public double getLongitude(){return longitude;}public String getPhone(){return phone;}
}
