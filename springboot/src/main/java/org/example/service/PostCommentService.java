package org.example.service;

import jakarta.annotation.Resource;
import org.example.entity.PostComment;
import org.example.mapper.PostCommentMapper;
import org.example.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostCommentService {
    @Resource
    private PostCommentMapper postCommentMapper;
    
    @Resource
    private PostMapper postMapper;

    public List<PostComment> selectByPostId(Integer postId) {
        return postCommentMapper.selectByPostId(postId);
    }

    public PostComment selectById(Integer id) {
        return postCommentMapper.selectById(id);
    }

    @Transactional
    public void add(PostComment comment) {
        postCommentMapper.insert(comment);
        // 更新帖子评论数
        postMapper.incrementCommentCount(comment.getPostId());
    }

    public void deleteById(Integer id) {
        postCommentMapper.deleteById(id);
    }
} 