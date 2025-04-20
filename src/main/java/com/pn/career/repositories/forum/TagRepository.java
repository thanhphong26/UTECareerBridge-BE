package com.pn.career.repositories.forum;

import com.pn.career.models.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Page<Tag> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<Tag> findByName(String name);
}
