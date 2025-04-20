package com.pn.career.services.forum;

import com.pn.career.dtos.Forum.TopicTagDTO;
import com.pn.career.responses.forum.TopicResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ITopicService {
    Page<TopicResponse> getAllTopics(PageRequest pageRequest);
    Optional<TopicResponse> getTopicById(Integer id);
    Page<TopicResponse> getTopicsByForumId(Integer forumId, Pageable pageable);
    Page<TopicResponse> getTopicsByUserId(Integer userId, Pageable pageable);

    TopicResponse createTopic(TopicTagDTO topic);
    TopicResponse updateTopic(Integer id, TopicTagDTO topic);
    void deleteTopic(Integer id);
    Page<TopicResponse> searchTopics(String keyword, List<Integer> tagIds, PageRequest pageRequest);
    void pinTopic(Integer topicId);
}
