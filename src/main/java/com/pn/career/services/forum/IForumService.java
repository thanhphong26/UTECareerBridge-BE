package com.pn.career.services.forum;

import com.pn.career.dtos.Forum.ForumDTO;
import com.pn.career.models.Forum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface IForumService {
    Page<Forum> getAllForums(PageRequest pageRequest);
    Page<Forum> getActiveForums(PageRequest pageRequest);
    Optional<Forum> getForumById(Integer id);
    Forum createForum(ForumDTO forum);
    Forum updateForum(Integer id, ForumDTO forum);
    void deleteForum(Integer id);
    Page<Forum> searchForums(String name, PageRequest pageRequest);
}
