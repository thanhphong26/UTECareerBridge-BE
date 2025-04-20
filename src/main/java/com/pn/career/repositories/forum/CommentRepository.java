package com.pn.career.repositories.forum;
import com.pn.career.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findByPostId(Integer postId, Pageable pageable);
    Page<Comment> findByPostIdAndParentCommentIdIsNull(Integer postId, Pageable pageable);
    Page<Comment> findByParentCommentId(Integer parentCommentId, Pageable pageable);
    Page<Comment> findByUserId(Integer userId, Pageable pageable);
    Integer countByPostId(Integer postId);
    Integer countByParentCommentId(Integer parentCommentId);
}
