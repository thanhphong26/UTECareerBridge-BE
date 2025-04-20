package com.pn.career.repositories.forum;

import com.pn.career.models.Forum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ForumRepository extends JpaRepository<Forum, Integer> {
    Page<Forum> findByIsActiveTrue(Pageable pageable);
    Page<Forum> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
