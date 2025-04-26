package com.pn.career.services.forum;

import com.pn.career.dtos.Forum.PostDTO;
import com.pn.career.responses.forum.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.Optional;

public interface IPostService {
    Page<PostResponse> getAllPosts(PageRequest pageRequest);
    Optional<PostResponse> getPostById(Integer id);
    Page<PostResponse> getPostsByTopicId(Integer topicId, PageRequest pageRequest);
    Page<PostResponse> getPostsByUserId(Integer userId, PageRequest pageRequest);
    PostResponse createPost(PostDTO post, Integer userId);
    PostResponse updatePost(Integer id, PostDTO post, Integer userId);
    void deletePost(Integer id, Integer userId);
    Page<PostResponse> searchPosts(String content, PageRequest pageRequest);
}
