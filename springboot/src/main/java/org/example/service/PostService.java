package org.example.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.example.entity.Post;
import org.example.entity.PostLike;
import org.example.mapper.PostLikeMapper;
import org.example.mapper.PostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    @Resource
    private PostMapper postMapper;
    
    @Resource
    private PostLikeMapper postLikeMapper;

    public List<Post> selectAll(Post post) {
        return postMapper.selectAll(post);
    }

    public Post selectById(Integer id) {
        return postMapper.selectById(id);
    }

    public PageInfo<Post> selectPage(Post post, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Post> list = postMapper.selectAll(post);
        return new PageInfo<>(list);
    }

    public void add(Post post) {
        // 设置默认值
        if (post.getViewCount() == null) {
            post.setViewCount(0);
        }
        if (post.getLikeCount() == null) {
            post.setLikeCount(0);
        }
        if (post.getCommentCount() == null) {
            post.setCommentCount(0);
        }
        postMapper.insert(post);
    }

    public void update(Post post) {
        postMapper.updateById(post);
    }

    public void deleteById(Integer id) {
        postMapper.deleteById(id);
    }
    
    /**
     * 查询帖子并增加浏览次数
     */
    public Post getPostDetail(Integer id, Integer currentUserId) {
        // 增加浏览次数
        postMapper.incrementViewCount(id);
        
        // 查询帖子
        Post post = postMapper.selectById(id);
        if (post != null && currentUserId != null) {
            // 判断当前用户是否已点赞
            int liked = postLikeMapper.checkLiked(id, currentUserId);
            post.setHasLiked(liked > 0);
        }
        
        return post;
    }
    
    /**
     * 点赞或取消点赞
     */
    @Transactional
    public void toggleLike(Integer postId, Integer userId) {
        // 检查是否已点赞
        int liked = postLikeMapper.checkLiked(postId, userId);
        
        if (liked > 0) {
            // 已点赞，取消点赞
            postLikeMapper.deleteByPostIdAndUserId(postId, userId);
            postMapper.decrementLikeCount(postId);
        } else {
            // 未点赞，添加点赞
            PostLike postLike = new PostLike();
            postLike.setPostId(postId);
            postLike.setUserId(userId);
            postLikeMapper.insert(postLike);
            postMapper.incrementLikeCount(postId);
        }
    }
    
    /**
     * 查询用户发布的帖子
     */
    public List<Post> selectByAuthorId(Integer authorId) {
        return postMapper.selectByAuthorId(authorId);
    }
} 