package org.example.controller;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.common.Result;
import org.example.entity.Post;
import org.example.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {
    @Resource
    private PostService postService;

    // 新增帖子
    @PostMapping("/add")
    public Result add(@RequestBody Post post) {
        postService.add(post);
        return Result.success();
    }

    // 更新帖子
    @PutMapping("/update")
    public Result update(@RequestBody Post post) {
        postService.update(post);
        return Result.success();
    }

    // 删除帖子
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        postService.deleteById(id);
        return Result.success();
    }

    // 查询所有帖子（带条件）
    @GetMapping("/selectAll")
    public Result selectAll(Post post) {
        List<Post> list = postService.selectAll(post);
        return Result.success(list);
    }

    // 查询帖子详情（并增加浏览次数）
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id, @RequestParam(required = false) Integer userId) {
        Post post = postService.getPostDetail(id, userId);
        return Result.success(post);
    }

    // 分页查询帖子（带条件）
    @GetMapping("/selectPage")
    public Result selectPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Post post
    ) {
        PageInfo<Post> pageInfo = postService.selectPage(post, pageNum, pageSize);
        return Result.success(pageInfo);
    }
    
    // 点赞或取消点赞
    @PostMapping("/toggleLike")
    public Result toggleLike(@RequestParam Integer postId, @RequestParam Integer userId) {
        postService.toggleLike(postId, userId);
        return Result.success();
    }
    
    // 查询用户发布的帖子
    @GetMapping("/selectByAuthorId/{authorId}")
    public Result selectByAuthorId(@PathVariable Integer authorId) {
        List<Post> list = postService.selectByAuthorId(authorId);
        return Result.success(list);
    }
} 