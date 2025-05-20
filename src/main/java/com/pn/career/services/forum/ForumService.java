package com.pn.career.services.forum;

import com.pn.career.dtos.Forum.ForumDTO;
import com.pn.career.models.Forum;
import com.pn.career.repositories.forum.ForumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ForumService implements IForumService{
    private final ForumRepository forumRepository;

    @Override
    public Page<Forum> getAllForums(PageRequest pageRequest) {
        return forumRepository.findAll(pageRequest);
    }

    @Override
    public Page<Forum> getActiveForums(PageRequest pageRequest) {
        return forumRepository.findByIsActiveTrueOrderByCreatedAt(pageRequest);
    }

    @Override
    public Optional<Forum> getForumById(Integer id) {
        return forumRepository.findById(id);
    }

    @Override
    public Forum createForum(ForumDTO forum) {
        Forum newForum = Forum.builder()
                .name(forum.getName())
                .description(forum.getDescription())
                .image(forum.getImage())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return forumRepository.save(newForum);
    }

    @Override
    public Forum updateForum(Integer id, ForumDTO forum) {
        return forumRepository.findById(id)
                .map(existingForum -> {
                    existingForum.setName(forum.getName());
                    existingForum.setDescription(forum.getDescription());
                    existingForum.setUpdatedAt(LocalDateTime.now());
                    existingForum.setImage(forum.getImage());
                    return forumRepository.save(existingForum);
                }).orElseGet(() -> {
                    Forum newForum = Forum.builder()
                            .forumId(id)
                            .name(forum.getName())
                            .description(forum.getDescription())
                            .image(forum.getImage())
                            .isActive(true)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return forumRepository.save(newForum);
                });
    }

    @Override
    public void deleteForum(Integer id) {
        forumRepository.findById(id)
                .ifPresent(forum -> {
                    forum.setActive(false);
                    forum.setUpdatedAt(LocalDateTime.now());
                    forumRepository.save(forum);
                });
    }

    @Override
    public Page<Forum> searchForums(String name, PageRequest pageRequest) {
        return forumRepository.findByNameContainingIgnoreCase(name, pageRequest);
    }
}
