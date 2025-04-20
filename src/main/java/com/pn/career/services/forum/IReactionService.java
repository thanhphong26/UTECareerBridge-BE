package com.pn.career.services.forum;

import com.pn.career.models.Reaction;
import com.pn.career.models.ReactionType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IReactionService {
    List<Reaction> getAllReactions();
    Optional<Reaction> getReactionById(Integer id);
    List<Reaction> getReactionsByPostId(Integer postId);
    List<Reaction> getReactionsByUserId(Integer userId);
    Optional<Reaction> getUserReactionOnPost(Integer postId, Integer userId);
    Reaction addOrUpdateReaction(Reaction reaction);
    void removeReaction(Integer id);
    void removeUserReactionOnPost(Integer postId, Integer userId);
    Map<ReactionType, Long> getReactionCountsByPostId(Integer postId);
}
