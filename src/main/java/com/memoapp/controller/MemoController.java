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
import java.time.LocalDateTime;
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

}
