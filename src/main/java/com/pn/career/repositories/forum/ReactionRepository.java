package com.pn.career.repositories.forum;

import com.pn.career.models.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Integer> {
    List<Reaction> findByPostId(Integer postId);
    List<Reaction> findByUserId(Integer userId);
    Optional<Reaction> findByPostIdAndUserId(Integer postId, Integer userId);

    @Query("SELECT r.type, COUNT(r) FROM Reaction r WHERE r.postId = :postId GROUP BY r.type")
    List<Object[]> countReactionsByTypeAndPostId(Integer postId);
}
