package com.memoapp.repository;

import com.memoapp.model.Memo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MemoRepository extends MongoRepository<Memo, String> {
    // Search by title using case-insensitive regex for whole word matching
    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Memo> searchByTitle(String titlePattern);
    
    // Search by fromSender (writer) using case-insensitive regex for whole word matching
    @Query("{'fromSender': {$regex: ?0, $options: 'i'}}")
    List<Memo> searchByWriter(String writerPattern);
    
    // Search by date range
    @Query("{'createdAt': {$gte: ?0, $lte: ?1}}")
    List<Memo> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
