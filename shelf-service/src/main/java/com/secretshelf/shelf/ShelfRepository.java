package com.secretshelf.shelf;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ShelfRepository extends JpaRepository<ReadingShelf,String>{List<ReadingShelf> findByOwnerIdOrderByCreatedAtDesc(String ownerId);Optional<ReadingShelf> findByIdAndOwnerId(String id,String ownerId);}
