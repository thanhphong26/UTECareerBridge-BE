package com.pn.career.services.forum;

import com.pn.career.dtos.Forum.ReactionDTO;
import com.pn.career.models.*;
import com.pn.career.repositories.EmployerRepository;
import com.pn.career.repositories.StudentRepository;
import com.pn.career.repositories.UserRepository;
import com.pn.career.repositories.forum.ReactionRepository;
import com.pn.career.responses.forum.ReactionCountResponse;
import com.pn.career.responses.forum.ReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionService implements IReactionService{
    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final EmployerRepository employerRepository;

    @Override
    public ReactionResponse createReaction(ReactionDTO reaction, Integer userId, Integer postId) {
        if (checkReactionExists(postId, userId)) {
            throw new AuthorizationServiceException("Bạn đã thả tương tác cho bài viết này");
        }
        Reaction newReaction = Reaction.builder()
                .postId(postId)
                .userId(userId)
                .type(reaction.getReactionType())
                .createdAt(LocalDateTime.now())
                .build();

        Reaction savedReaction = reactionRepository.save(newReaction);
        return mapTopicToResponse(savedReaction);
    }
    private boolean checkReactionExists(Integer postId, Integer userId) {
        return reactionRepository.existsByPostIdAndUserId(postId, userId);
    }
    @Override
    public void removeReaction(Integer postId, Integer userId) {
        Reaction reaction = reactionRepository.findByPostIdAndUserId(postId, userId).orElseThrow(
                () -> new AuthorizationServiceException("Reaction not found")
        );
        if (reaction != null) {
            reactionRepository.delete(reaction);
        } else {
            throw new AuthorizationServiceException("Reaction not found");
        }
    }

    @Override
    public ReactionResponse getUserReaction(Integer postId, Integer userId) {
        Optional<Reaction> reaction  = reactionRepository.findByPostIdAndUserId(postId, userId);
        if (reaction.isEmpty()) {
            return null;
        }
        return mapTopicToResponse(reaction.orElse(null));
    }

    @Override
    public Page<ReactionResponse> getReactionsByPostId(Integer postId, PageRequest pageRequest) {
        Page<Reaction> reactions = reactionRepository.findByPostId(postId, pageRequest);
        ReactionCountResponse reactionCount = getReactionCountByPostId(postId);
        return reactions.map(reaction -> {
            ReactionResponse response = mapTopicToResponse(reaction);
            response.setReactionCount(reactionCount);
            return response;
        });
    }

    @Override
    public ReactionCountResponse getReactionCountByPostId(Integer postId) {
        ReactionCountResponse reactionCountResponse = new ReactionCountResponse();
        long likeCount = reactionRepository.countByPostIdAndType(postId, ReactionType.LIKE);
        long dislikeCount = reactionRepository.countByPostIdAndType(postId, ReactionType.DISLIKE);
        long loveCount = reactionRepository.countByPostIdAndType(postId, ReactionType.LOVE);
        long hahaCount = reactionRepository.countByPostIdAndType(postId, ReactionType.HAHA);
        long wowCount = reactionRepository.countByPostIdAndType(postId, ReactionType.WOW);
        long sadCount = reactionRepository.countByPostIdAndType(postId, ReactionType.SAD);
        long angryCount = reactionRepository.countByPostIdAndType(postId, ReactionType.ANGRY);
        long totalCount = likeCount + dislikeCount + loveCount + hahaCount + wowCount + sadCount + angryCount;
        reactionCountResponse.setPostId(postId);
        reactionCountResponse.setTotalCount(totalCount);
        reactionCountResponse.setHahaCount(hahaCount);
        reactionCountResponse.setWowCount(wowCount);
        reactionCountResponse.setSadCount(sadCount);
        reactionCountResponse.setAngryCount(angryCount);
        reactionCountResponse.setLikeCount(likeCount);
        reactionCountResponse.setDislikeCount(dislikeCount);
        reactionCountResponse.setLoveCount(loveCount);

        return reactionCountResponse;
    }
    private ReactionResponse mapTopicToResponse(Reaction reaction) {
        ReactionResponse reactionResponse = new ReactionResponse();
        reactionResponse.setReactionId(reaction.getReactionId());
        reactionResponse.setPostId(reaction.getPostId());
        reactionResponse.setUserId(reaction.getUserId());
        reactionResponse.setCreatedAt(reaction.getCreatedAt());
        reactionResponse.setType(reaction.getType());

        // Fetch user information once
        User user = userRepository.findById(reaction.getUserId()).orElse(null);
        if (user != null) {
            String roleName = user.getRole().getRoleName();
            reactionResponse.setRoleName(roleName.toUpperCase());

            // Get user name and avatar based on role
            if ("student".equals(roleName)) {
                Student student = studentRepository.findById(user.getUserId()).orElse(null);
                if (student != null) {
                    reactionResponse.setUserName(student.getLastName() + " " + student.getFirstName());
                    reactionResponse.setAvatar(student.getProfileImage());
                } else {
                    setDefaultUserInfo(reactionResponse, "Unknown");
                }
            } else if ("employer".equals(roleName)) {
                Employer employer = employerRepository.findById(user.getUserId()).orElse(null);
                if (employer != null) {
                    reactionResponse.setUserName(employer.getCompanyName());
                    reactionResponse.setAvatar(employer.getCompanyLogo());
                } else {
                    setDefaultUserInfo(reactionResponse, "Unknown");
                }
            } else {
                setDefaultUserInfo(reactionResponse, roleName);
            }
        } else {
            setDefaultUserInfo(reactionResponse, "Unknown");
        }

        return reactionResponse;
    }

    private void setDefaultUserInfo(ReactionResponse reactionResponse, String userName) {
        reactionResponse.setUserName(userName);
        reactionResponse.setAvatar("https://res.cloudinary.com/utejobhub/image/upload/v1745056474/UTE-removebg-preview_dz3ykb.png");
    }
}
