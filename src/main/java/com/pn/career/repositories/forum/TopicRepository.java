package com.pn.career.repositories.forum;

import com.pn.career.models.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    Page<Topic> findByForumIdAndIsCloseFalseOrderByIsPinnedDesc(Integer forumId, Pageable pageable);
    Page<Topic> findByForumId(Integer forumId, Pageable pageable);
    Page<Topic> findByUserId(Integer userId, Pageable pageable);
    Page<Topic> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
