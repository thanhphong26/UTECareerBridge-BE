package com.pn.career.repositories.forum;

import com.pn.career.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findByTopicId(Integer topicId, Pageable pageable);
    Page<Post> findByUserId(Integer userId, Pageable pageable);
    Page<Post> findByContentContainingIgnoreCase(String content, Pageable pageable);
    Integer countByTopicIdAndActive(Integer topicId, Boolean isActive);
    void deleteAllByTopicId(Integer topicId);
}
