package com.secretshelf.shelf;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
public interface StoredBookRepository extends JpaRepository<StoredBook,Long>{List<StoredBook> findByShelfIdOrderByAddedAtDesc(String shelfId);@Transactional void deleteByShelfIdAndVolumeId(String shelfId,String volumeId);}
