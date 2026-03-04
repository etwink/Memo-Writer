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

    /**
     * Search memos with keyword matching on title, writer, and optional date range.
     * Uses whole-word matching via regex to avoid partial word matches.
     * 
     * @param keyword The search keyword(s) - can be multiple words
     * @param writer Optional writer/sender name to filter by
     * @param startDate Optional start date for filtering
     * @param endDate Optional end date for filtering
     * @return List of memos matching the search criteria
     */
    public List<Memo> searchMemos(String keyword, String writer, LocalDateTime startDate, LocalDateTime endDate) {
        // Build regex pattern for whole-word matching
        // This creates a pattern that matches any of the keywords as whole words
        String pattern = buildWholeWordPattern(keyword);
        
        List<Memo> results = memoRepository.searchByTitle(pattern);
        
        // Filter by writer if provided
        if (writer != null && !writer.trim().isEmpty()) {
            results = results.stream()
                .filter(memo -> memo.getFromSender() != null && 
                       memo.getFromSender().toLowerCase().contains(writer.toLowerCase()))
                .toList();
        }
        
        // Filter by date range if provided
        if (startDate != null && endDate != null) {
            results = results.stream()
                .filter(memo -> !memo.getCreatedAt().isBefore(startDate) && 
                       !memo.getCreatedAt().isAfter(endDate))
                .toList();
        }
        
        return results;
    }

    /**
     * Build a regex pattern for whole-word matching.
     * Splits the keyword by spaces and creates a pattern that matches
     * any keyword as a whole word (not part of another word).
     * 
     * @param keyword The search keyword(s)
     * @return Regex pattern for whole-word matching
     */
    private String buildWholeWordPattern(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ".*";
        }
        
        // Split keyword by spaces and escape special regex characters
        String[] words = keyword.trim().split("\\s+");
        StringBuilder pattern = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            // Escape special regex characters in each word
            String escapedWord = java.util.regex.Pattern.quote(words[i]);
            
            // Use word boundary \b for whole-word matching
            // This ensures the word is not part of another word
            pattern.append("(?=.*\\b").append(escapedWord).append("\\b)");
            
            if (i < words.length - 1) {
                pattern.append("");
            }
        }
        
        // Add .* at the end to match the entire string
        pattern.append(".*");
        
        return pattern.toString();
    }

}
