package com.pn.career.services.forum;

import com.pn.career.dtos.Forum.ReactionDTO;
import com.pn.career.responses.forum.ReactionCountResponse;
import com.pn.career.responses.forum.ReactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IReactionService {
    ReactionResponse createReaction(ReactionDTO reaction, Integer userId, Integer postId);
    void removeReaction(Integer postId, Integer userId);
    ReactionResponse getUserReaction(Integer postId, Integer userId);
    Page<ReactionResponse> getReactionsByPostId(Integer postId, PageRequest pageRequest);
    ReactionCountResponse getReactionCountByPostId(Integer postId);
}
