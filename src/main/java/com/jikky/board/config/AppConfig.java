package com.jikky.board.config;

import com.jikky.board.mapper.PostRowMapper;
import com.jikky.board.mapper.CommentRowMapper;
import com.jikky.board.mapper.UserRowMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public PostRowMapper postRowMapper() {
        return new PostRowMapper();
    }

    @Bean
    public CommentRowMapper commentRowMapper() {
        return new CommentRowMapper();
    }

    @Bean
    public UserRowMapper userRowMapper() {
        return new UserRowMapper();
    }
}
