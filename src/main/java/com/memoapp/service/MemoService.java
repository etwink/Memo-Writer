package com.memoapp.service;

import com.memoapp.model.Memo;
import com.memoapp.repository.MemoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MemoService {

    @Autowired
    private MemoRepository memoRepository;

    public List<Memo> getAllMemos() {
        return memoRepository.findAll();
    }

    public Optional<Memo> getMemoById(String id) {
        return memoRepository.findById(id);
    }

    public Memo saveMemo(Memo memo) {
        if (memo.getId() == null) {
            memo.setCreatedAt(LocalDateTime.now());
        }
        memo.setUpdatedAt(LocalDateTime.now());
        return memoRepository.save(memo);
    }

    public void deleteMemo(String id) {
        memoRepository.deleteById(id);
    }

}
