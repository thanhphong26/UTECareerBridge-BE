package com.pn.career.services.forum;

import com.pn.career.dtos.Forum.CommentDTO;
import com.pn.career.responses.forum.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.Optional;

public interface ICommentService {
    Page<CommentResponse> getAllComments(PageRequest pageRequest);
    Optional<CommentResponse> getCommentById(Integer id);
    Page<CommentResponse> getCommentsByPostId(Integer postId, PageRequest pageRequest);
    Page<CommentResponse> getRootCommentsByPostId(Integer postId, PageRequest pageRequest);
    Page<CommentResponse> getChildComments(Integer parentCommentId, PageRequest pageRequest);
    Page<CommentResponse> getCommentsByUserId(Integer userId, PageRequest pageRequest);
    CommentResponse createComment(CommentDTO comment);
    CommentResponse updateComment(Integer id, CommentDTO comment, Integer userId);
    void deleteComment(Integer id, Integer userId);
    Integer countCommentsByPostId(Integer postId);
}
