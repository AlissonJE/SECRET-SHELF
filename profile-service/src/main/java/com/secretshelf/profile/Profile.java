package com.secretshelf.profile;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="reader_profiles")
public class Profile {
    @Id private String userId;
    @Column(nullable=false) private String displayName="Lector/a";
    @Column(nullable=false) private String ageRange="adult";
    @Column(nullable=false,length=1000) private String genres="[]";
    @Column(nullable=false) private String theme="light";
    @Column(nullable=false) private String density="grid";
    @Column(nullable=false,length=5) private String language="es";
    protected Profile(){}
    public Profile(String userId){this.userId=userId;}
    public String getUserId(){return userId;} public String getDisplayName(){return displayName;} public void setDisplayName(String v){displayName=v;}
    public String getAgeRange(){return ageRange;} public void setAgeRange(String v){ageRange=v;}
    public String getGenres(){return genres;} public void setGenres(String v){genres=v;}
    public String getTheme(){return theme;} public void setTheme(String v){theme=v;}
    public String getDensity(){return density;} public void setDensity(String v){density=v;}
    public String getLanguage(){return language;} public void setLanguage(String v){language=v;}
}
