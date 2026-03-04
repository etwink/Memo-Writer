package com.memoapp.controller;

import com.memoapp.model.Memo;
import com.memoapp.service.MemoService;
import com.memoapp.util.MemoDocumentGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/memos")
public class MemoController {

    @Autowired
    private MemoService memoService;

    // Display all memos
    @GetMapping
    public String listMemos(Model model) {
        List<Memo> memos = memoService.getAllMemos();
        model.addAttribute("memos", memos);
        return "memo-list";
    }

    // Show form to create a new memo
    @GetMapping("/new")
    public String showNewMemoForm(Model model) {
        model.addAttribute("memo", new Memo());
        return "memo-form";
    }

    // Save a new memo
    @PostMapping
    public String saveMemo(@ModelAttribute Memo memo) {
        if (memo.getCreatedAt() == null) {
            memo.setCreatedAt(LocalDateTime.now());
        }
        memo.setUpdatedAt(LocalDateTime.now());
        memoService.saveMemo(memo);
        return "redirect:/memos";
    }

    // Show form to edit an existing memo
    @GetMapping("/edit/{id}")
    public String showEditMemoForm(@PathVariable String id, Model model) {
        Optional<Memo> memo = memoService.getMemoById(id);
        if (memo.isPresent()) {
            model.addAttribute("memo", memo.get());
            return "memo-form";
        }
        return "redirect:/memos";
    }

    // Update an existing memo
    @PostMapping("/{id}")
    public String updateMemo(@PathVariable String id, @ModelAttribute Memo memo) {
        Optional<Memo> existingMemo = memoService.getMemoById(id);
        if (existingMemo.isPresent()) {
            memo.setId(id);
            memo.setCreatedAt(existingMemo.get().getCreatedAt());
            memo.setUpdatedAt(LocalDateTime.now());
            memoService.saveMemo(memo);
        }
        return "redirect:/memos";
    }

    // Delete a memo
    @GetMapping("/delete/{id}")
    public String deleteMemo(@PathVariable String id) {
        memoService.deleteMemo(id);
        return "redirect:/memos";
    }

    // View a single memo
    @GetMapping("/{id}")
    public String viewMemo(@PathVariable String id, Model model) {
        Optional<Memo> memo = memoService.getMemoById(id);
        if (memo.isPresent()) {
            model.addAttribute("memo", memo.get());
            return "memo-view";
        }
        return "redirect:/memos";
    }

    // Download memo as .docx file
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadMemo(@PathVariable String id) {
        Optional<Memo> memo = memoService.getMemoById(id);
        if (memo.isPresent()) {
            try {
                byte[] documentBytes = MemoDocumentGenerator.generateMemoDocument(memo.get());
                
                // Generate filename with timestamp
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
                String timestamp = LocalDateTime.now().format(formatter);
                String filename = "Memo_" + memo.get().getTitle().replaceAll("[^a-zA-Z0-9_]", "_") + "_" + timestamp + ".docx";
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDispositionFormData("attachment", filename);
                headers.set("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                
                return new ResponseEntity<>(documentBytes, headers, HttpStatus.OK);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Search memos by title, writer, and date range
    @GetMapping("/search")
    public String searchMemos(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "writer", required = false) String writer,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            Model model) {
        
        List<Memo> searchResults = new java.util.ArrayList<>();
        
        // Only perform search if at least one parameter is provided
        if ((keyword != null && !keyword.trim().isEmpty()) ||
            (writer != null && !writer.trim().isEmpty()) ||
            (startDate != null && !startDate.trim().isEmpty())) {
            
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            
            // Parse date range if provided
            if (startDate != null && !startDate.trim().isEmpty()) {
                try {
                    LocalDate start = LocalDate.parse(startDate);
                    startDateTime = start.atStartOfDay();
                } catch (Exception e) {
                    // Invalid date format, ignore
                }
            }
            
            if (endDate != null && !endDate.trim().isEmpty()) {
                try {
                    LocalDate end = LocalDate.parse(endDate);
                    endDateTime = end.atTime(LocalTime.MAX);
                } catch (Exception e) {
                    // Invalid date format, ignore
                }
            }
            
            // If only one date is provided, set the other
            if (startDateTime != null && endDateTime == null) {
                endDateTime = LocalDateTime.now();
            }
            if (endDateTime != null && startDateTime == null) {
                startDateTime = LocalDateTime.of(2000, 1, 1, 0, 0);
            }
            
            // Perform search
            searchResults = memoService.searchMemos(keyword, writer, startDateTime, endDateTime);
        }
        
        model.addAttribute("memos", searchResults);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("writer", writer != null ? writer : "");
        model.addAttribute("startDate", startDate != null ? startDate : "");
        model.addAttribute("endDate", endDate != null ? endDate : "");
        model.addAttribute("isSearchResults", true);
        
        return "memo-list";
    }

}
