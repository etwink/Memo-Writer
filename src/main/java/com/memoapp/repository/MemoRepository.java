package com.memoapp.repository;

import com.memoapp.model.Memo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoRepository extends MongoRepository<Memo, String> {
}
