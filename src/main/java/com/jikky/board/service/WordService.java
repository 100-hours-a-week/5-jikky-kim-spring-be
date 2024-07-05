package com.jikky.board.service;

import com.jikky.board.model.Word;

import java.util.List;

public interface WordService {
    List<Word> getAllWord(String keyword);
}
