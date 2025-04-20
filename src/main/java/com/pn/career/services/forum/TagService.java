package com.pn.career.services.forum;

import com.pn.career.dtos.Forum.TagDTO;
import com.pn.career.exceptions.DuplicateNameException;
import com.pn.career.models.Tag;
import com.pn.career.repositories.forum.TagRepository;
import com.pn.career.repositories.forum.TopicTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService implements ITagService{
    private final TagRepository tagRepository;
    private final TopicTagRepository topicTagRepository;

    @Override
    public Page<Tag> getAllTags(PageRequest pageRequest) {
        return tagRepository.findAll(pageRequest);
    }

    @Override
    public Optional<Tag> getTagById(Integer id) {
        return tagRepository.findById(id);
    }

    @Override
    public Tag createTag(TagDTO tag) {
        Tag newTag = Tag.builder()
                .name(tag.getName())
                .description(tag.getDescription())
                .build();
        //check name of tag exits
        Optional<Tag> existingTag = tagRepository.findByName(tag.getName());
        if (existingTag.isPresent()) {
            throw new DuplicateNameException("Thẻ với " + tag.getName() + " đã tồn tại");
        }
        return tagRepository.save(newTag);
    }

    @Override
    public Tag updateTag(Integer id, TagDTO tag) {
        return tagRepository.findById(id)
                .map(existingTag -> {
                    Optional<Tag> tagWithSameName = tagRepository.findByName(tag.getName());
                    if (tagWithSameName.isPresent() && !tagWithSameName.get().getTagId().equals(id)) {
                        throw new DuplicateNameException("Thẻ với " + tag.getName() + " đã tồn tại");
                    }
                    existingTag.setName(tag.getName());
                    existingTag.setDescription(tag.getDescription());
                    return tagRepository.save(existingTag);
                }).orElseGet(() -> {
                    Tag newTag = Tag.builder()
                            .tagId(id)
                            .name(tag.getName())
                            .description(tag.getDescription())
                            .build();
                    return tagRepository.save(newTag);
                });
    }

    @Override
    public void deleteTag(Integer id) {
        tagRepository.deleteById(id);
    }

    @Override
    public Page<Tag> searchTags(String name, PageRequest pageRequest) {
        return tagRepository.findByNameContainingIgnoreCase(name, pageRequest);
    }

    @Override
    public Page<Tag> getTagsByTopicId(Integer topicId, PageRequest pageRequest) {
//        List<TopicTag> topicTags = topicTagRepository.findByTopicId(topicId);
//        List<Integer> tagIds = topicTags.stream()
//                .map(TopicTag::getTagId)
//                .collect(Collectors.toList());
//        return tagRepository.findAllById(tagIds, pageRequest);
        return null;
    }
}
