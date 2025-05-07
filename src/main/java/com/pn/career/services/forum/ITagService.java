package com.pn.career.services.forum;

import com.pn.career.dtos.Forum.TagDTO;
import com.pn.career.models.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.Optional;

public interface ITagService {
    Page<Tag> getAllTags(PageRequest pageRequest);
    Optional<Tag> getTagById(Integer id);
    Tag createTag(TagDTO tag);
    Tag updateTag(Integer id, TagDTO tag);
    void deleteTag(Integer id);
    Page<Tag> searchTags(String name, PageRequest pageRequest);
    Page<Tag> getTagsByTopicId(Integer topicId, PageRequest pageRequest);
}
