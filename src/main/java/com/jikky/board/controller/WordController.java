package com.jikky.board.controller;

import com.jikky.board.model.Word;
import com.jikky.board.service.UserService;
import com.jikky.board.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/words")
public class WordController {
    @Autowired
    private WordService wordService;

    @GetMapping("/search")
    public ResponseEntity<?> getAllWord(
            @RequestParam(required = false) String keyword) {
        try {
            List<Word> words = wordService.getAllWord(keyword);
            return ResponseEntity.ok().body(Map.of("message", "success", "words", words));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal Server Error"));
        }
    }
}
