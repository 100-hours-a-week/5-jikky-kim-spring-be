package com.jikky.board.service.impl;

import com.jikky.board.service.PostService;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {
    public int add(int a, int b) {
        return a + b;
    }

}