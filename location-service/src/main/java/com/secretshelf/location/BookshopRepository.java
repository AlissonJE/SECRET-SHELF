package com.secretshelf.location;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BookshopRepository extends JpaRepository<Bookshop,Long>{boolean existsByName(String name);}
