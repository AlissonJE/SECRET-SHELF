package com.secretshelf.auth;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="users", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class UserAccount {
    @Id private String id = UUID.randomUUID().toString();
    @Column(nullable=false, unique=true) private String email;
    @Column(nullable=false) private String name;
    @Column(nullable=false) private String passwordHash;
    protected UserAccount() {}
    public UserAccount(String name,String email,String passwordHash){this.name=name;this.email=email.toLowerCase().trim();this.passwordHash=passwordHash;}
    public String getId(){return id;} public String getEmail(){return email;} public String getName(){return name;} public String getPasswordHash(){return passwordHash;}
}
