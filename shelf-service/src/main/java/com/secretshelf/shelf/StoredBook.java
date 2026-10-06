package com.secretshelf.shelf;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="shelf_books",uniqueConstraints=@UniqueConstraint(columnNames={"shelf_id","volume_id"}))
public class StoredBook {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="shelf_id",nullable=false) private String shelfId;
    @Column(name="volume_id",nullable=false) private String volumeId;
    @Column(nullable=false,length=300) private String title;
    @Column(length=500) private String authors;
    @Column(length=1000) private String thumbnail;
    @Column(nullable=false) private Instant addedAt=Instant.now();
    protected StoredBook(){}
    public StoredBook(String shelfId,String volumeId,String title,String authors,String thumbnail){this.shelfId=shelfId;this.volumeId=volumeId;this.title=title;this.authors=authors;this.thumbnail=thumbnail;}
    public String getVolumeId(){return volumeId;} public String getTitle(){return title;} public String getAuthors(){return authors;} public String getThumbnail(){return thumbnail;} public Instant getAddedAt(){return addedAt;}
}
