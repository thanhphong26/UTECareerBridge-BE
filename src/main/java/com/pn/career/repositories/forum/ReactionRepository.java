package com.pn.career.repositories.forum;

import com.pn.career.models.Reaction;
import com.pn.career.models.ReactionType;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Integer> {
    boolean existsByPostIdAndUserId(Integer postId, Integer userId);
    Integer countByPostId(Integer postId);
    Page<Reaction> findByPostId(Integer postId, Pageable pageable);
    Page<Reaction> findByUserId(Integer userId, Pageable pageable);
    Optional<Reaction> findByPostIdAndUserId(Integer postId, Integer userId);
    @Query("SELECT r.type, COUNT(r) FROM Reaction r WHERE r.postId = :postId GROUP BY r.type")
    Page<Object[]> countReactionsByTypeAndPostId(Integer postId, Pageable pageable);
    void deleteByPostIdAndUserId(Integer postId, Integer userId);
    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.postId = :postId AND r.type = :type")
    long countByPostIdAndType(@Param("postId") Integer postId, @Param("type") ReactionType type);
    @Modifying
    @Transactional
    @Query("DELETE FROM Reaction c WHERE c.postId IN (SELECT p.postId FROM Post p WHERE p.topicId = :topicId)")
    void deleteAllReactionsByTopicId(Integer topicId);
}
