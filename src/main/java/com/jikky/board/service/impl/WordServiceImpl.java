package com.jikky.board.service.impl;

import com.jikky.board.model.Word;
import com.jikky.board.repository.WordRepository;
import com.jikky.board.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordServiceImpl implements WordService {
    @Autowired
    private WordRepository wordRepository;

    @Override
    public List<Word> getAllWord(String keyword) {
        return wordRepository.findAllWord(keyword);
    }
}
