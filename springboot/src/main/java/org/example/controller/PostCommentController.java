package org.example.controller;

import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.PostComment;
import org.example.service.PostCommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/postComment")
public class PostCommentController {
    @Resource
    private PostCommentService postCommentService;

    // 添加评论
    @PostMapping("/add")
    public Result add(@RequestBody PostComment comment) {
        postCommentService.add(comment);
        return Result.success();
    }

    // 删除评论
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        postCommentService.deleteById(id);
        return Result.success();
    }

    // 查询帖子的评论
    @GetMapping("/selectByPostId/{postId}")
    public Result selectByPostId(@PathVariable Integer postId) {
        List<PostComment> list = postCommentService.selectByPostId(postId);
        return Result.success(list);
    }
} 