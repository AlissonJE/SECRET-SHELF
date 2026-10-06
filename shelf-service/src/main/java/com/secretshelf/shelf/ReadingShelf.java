package com.secretshelf.shelf;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="reading_shelves")
public class ReadingShelf {
    @Id private String id=UUID.randomUUID().toString();
    @Column(nullable=false) private String ownerId;
    @Column(nullable=false,length=80) private String name;
    @Column(nullable=false) private Instant createdAt=Instant.now();
    protected ReadingShelf(){}
    public ReadingShelf(String ownerId,String name){this.ownerId=ownerId;this.name=name;}
    public String getId(){return id;} public String getOwnerId(){return ownerId;} public String getName(){return name;} public void setName(String name){this.name=name;} public Instant getCreatedAt(){return createdAt;}
}
