package com.pn.career.services.forum;

import com.pn.career.models.Reaction;
import com.pn.career.models.ReactionType;
import com.pn.career.repositories.forum.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionService implements IReactionService{
    private final ReactionRepository reactionRepository;

    @Override
    public List<Reaction> getAllReactions() {
        return reactionRepository.findAll();
    }

    @Override
    public Optional<Reaction> getReactionById(Integer id) {
        return reactionRepository.findById(id);
    }

    @Override
    public List<Reaction> getReactionsByPostId(Integer postId) {
        return reactionRepository.findByPostId(postId);
    }

    @Override
    public List<Reaction> getReactionsByUserId(Integer userId) {
        return reactionRepository.findByUserId(userId);
    }

    @Override
    public Optional<Reaction> getUserReactionOnPost(Integer postId, Integer userId) {
        return reactionRepository.findByPostIdAndUserId(postId, userId);
    }

    @Override
    public Reaction addOrUpdateReaction(Reaction reaction) {
        return reactionRepository.save(reaction);
    }

    @Override
    public void removeReaction(Integer id) {
        reactionRepository.deleteById(id);
    }

    @Override
    public void removeUserReactionOnPost(Integer postId, Integer userId) {
        reactionRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(reaction -> reactionRepository.deleteById(reaction.getReactionId()));
    }

    @Override
    public Map<ReactionType, Long> getReactionCountsByPostId(Integer postId) {
        List<Object[]> results = reactionRepository.countReactionsByTypeAndPostId(postId);
        Map<ReactionType, Long> counts = new HashMap<>();

        for (Object[] result : results) {
            ReactionType type = (ReactionType) result[0];
            Long count = (Long) result[1];
            counts.put(type, count);
        }

        return counts;
    }
}
