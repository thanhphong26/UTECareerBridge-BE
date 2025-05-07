package com.pn.career.repositories.forum;

import com.pn.career.models.Topic;
import com.pn.career.models.TopicTag;
import com.pn.career.models.TopicTagId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicTagRepository extends JpaRepository<TopicTag, Integer> {
    //delete by topic id
    void deleteByTopic_TopicId(Integer topicId);
    // find topic tag by topic id
    List<TopicTag> findByTopic_TopicId(Integer topicId);
    void deleteById(TopicTagId id);
    // find topis by keyword, sort by, and list tag ids
//    @Query("SELECT DISTINCT t FROM Topic t " +
//            "LEFT JOIN TopicTag tt ON t.topicId = tt.topic.topicId " +
//            "WHERE (t.title LIKE %:name% OR t.content LIKE %:name%) " +
//            "AND (:#{#tagIds.isEmpty()} = true OR tt.tag.tagId IN :tagIds) " +
//            "GROUP BY t.topicId " +
//            "HAVING (:#{#tagIds.isEmpty()} = true OR COUNT(DISTINCT tt.tag.tagId) >= :tagCount)")
//    Page<Topic> findByNameContainingAndTagsIn(String name, List<Integer> tagIds, int tagCount, Pageable pageable);
//    @Query("SELECT t FROM Topic t WHERE (:keyword IS NULL OR :keyword = '' OR t.title LIKE %:keyword% OR t.content LIKE %:keyword%)")
//    Page<Topic> findByTitleContainingOrContentContaining(String keyword, Pageable pageable);
    @Query("SELECT DISTINCT t FROM Topic t " +
            "LEFT JOIN TopicTag tt ON t.topicId = tt.topic.topicId " +
            "WHERE t.forumId = :forumId " +
            "AND t.isClose = false " +
            "AND (t.title LIKE %:name% OR t.content LIKE %:name%) " +
            "AND (:#{#tagIds.isEmpty()} = true OR tt.tag.tagId IN :tagIds) " +
            "GROUP BY t.topicId " +
            "HAVING (:#{#tagIds.isEmpty()} = true OR COUNT(DISTINCT tt.tag.tagId) >= :tagCount) " +
            "ORDER BY t.isPinned DESC, t.updatedAt DESC")
    Page<Topic> findByNameContainingAndTagsIn(String name, List<Integer> tagIds, int tagCount, Integer forumId, Pageable pageable);

    @Query("SELECT t FROM Topic t " +
            "WHERE t.forumId = :forumId " +
            "AND t.isClose = false " +
            "AND (:keyword IS NULL OR :keyword = '' OR t.title LIKE %:keyword% OR t.content LIKE %:keyword%) " +
            "ORDER BY t.isPinned DESC, t.updatedAt DESC")
    Page<Topic> findByTitleContainingOrContentContaining(String keyword, Integer forumId, Pageable pageable);
}
