package com.pn.career.repositories.forum;
import com.pn.career.models.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findByPostId(Integer postId, Pageable pageable);
    Page<Comment> findByPostIdAndParentCommentIdIsNullOrderByCreatedAtDesc(Integer postId, Pageable pageable);
    Page<Comment> findByParentCommentId(Integer parentCommentId, Pageable pageable);
    Page<Comment> findByUserId(Integer userId, Pageable pageable);
    Integer countByPostId(Integer postId);
    Integer countByParentCommentId(Integer parentCommentId);

    @Query("SELECT c FROM Comment c WHERE c.postId IN (SELECT p.postId FROM Post p WHERE p.topicId = :topicId)")
    List<Comment> findAllCommentsByTopicId(Integer topicId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.postId IN (SELECT p.postId FROM Post p WHERE p.topicId = :topicId)")
    void deleteAllCommentsByTopicId(Integer topicId);

    List<Comment> findAllByPostId(Integer postId);

    @Modifying
    @Transactional
    void deleteAllByPostId(Integer postId);

}
