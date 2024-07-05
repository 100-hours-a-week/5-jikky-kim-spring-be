package com.jikky.board.repository;

import com.jikky.board.model.Word;

import java.util.List;

public interface WordRepository {
    List<Word> findAllWord(String keyword);
}
