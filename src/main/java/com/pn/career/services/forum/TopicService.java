package com.pn.career.services.forum;

import com.pn.career.dtos.Forum.TopicTagDTO;
import com.pn.career.models.*;
import com.pn.career.repositories.EmployerRepository;
import com.pn.career.repositories.StudentRepository;
import com.pn.career.repositories.UserRepository;
import com.pn.career.repositories.forum.TagRepository;
import com.pn.career.repositories.forum.TopicRepository;
import com.pn.career.repositories.forum.TopicTagRepository;
import com.pn.career.responses.forum.TagResponse;
import com.pn.career.responses.forum.TopicResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicService implements ITopicService {
    private final TopicRepository topicRepository;
    private final TopicTagRepository topicTagRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final EmployerRepository employerRepository;

    @Override
    public Page<TopicResponse> getAllTopics(PageRequest pageRequest) {
        Page<Topic> topics = topicRepository.findAll(pageRequest);
        return topics.map(this::mapTopicToResponse);
    }

    @Override
    public Optional<TopicResponse> getTopicById(Integer id) {
        return topicRepository.findById(id).map(this::mapTopicToResponse);
    }

    @Override
    public Page<TopicResponse> getTopicsByForumId(Integer forumId, Pageable pageable) {
        Page<Topic> topics = topicRepository.findByForumId(forumId, pageable);
        return topics.map(this::mapTopicToResponse);
    }

    @Override
    public Page<TopicResponse> getTopicsByUserId(Integer userId, Pageable pageable) {
        Page<Topic> topics = topicRepository.findByUserId(userId, pageable);
        return topics.map(this::mapTopicToResponse);
    }

    @Override
    @Transactional
    public TopicResponse createTopic(TopicTagDTO topic) {
        // Save the topic first
        Topic newTopic = Topic.builder()
                .title(topic.getTitle())
                .content(topic.getContent())
                .forumId(topic.getForumId())
                .userId(topic.getUserId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isClose(false)
                .isPinned(false)
                .status(TopicStatus.ACTIVE)
                .build();
        Topic savedTopic = topicRepository.save(newTopic);

        List<TopicTag> topicTags = topic.getTags().stream()
                .map(tagId -> {
                    TopicTagId topicTagId = new TopicTagId(savedTopic.getTopicId(), tagId);
                    Tag tag = tagRepository.findById(tagId)
                            .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
                    return TopicTag.builder()
                            .id(topicTagId)
                            .topic(savedTopic)
                            .tag(tag)
                            .build();
                })
                .toList();
        topicTagRepository.saveAll(topicTags);

        User user = userRepository.findById(savedTopic.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String roleName = user.getRole().getRoleName();

        String userName;
        String avatar;

        if ("student".equals(roleName)) {
            Student student = studentRepository.findById(user.getUserId()).orElse(null);
            userName = student != null ? student.getLastName() + " " + student.getFirstName() : "Unknown";
            avatar = student != null ? student.getProfileImage() : getDefaultAvatar();
        } else if ("employer".equals(roleName)) {
            Employer employer = employerRepository.findById(user.getUserId()).orElse(null);
            userName = employer != null ? employer.getCompanyName() : "Unknown";
            avatar = employer != null ? employer.getCompanyLogo() : getDefaultAvatar();
        } else {
            userName = roleName;
            avatar = getDefaultAvatar();
        }

        // Build and return the response
        return TopicResponse.builder()
                .topicId(savedTopic.getTopicId())
                .title(savedTopic.getTitle())
                .content(savedTopic.getContent())
                .createdAt(savedTopic.getCreatedAt())
                .updatedAt(savedTopic.getUpdatedAt())
                .isPinned(savedTopic.isPinned())
                .isClose(savedTopic.isClose())
                .userId(savedTopic.getUserId())
                .userName(userName)
                .avatar(avatar)
                .roleName(roleName.toUpperCase())
                .forumId(savedTopic.getForumId())
                .tags(topicTags.stream()
                        .map(topicTag -> TagResponse.builder()
                                .id(topicTag.getTag().getTagId())
                                .name(topicTag.getTag().getName())
                                .description(topicTag.getTag().getDescription())
                                .build())
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public TopicResponse updateTopic(Integer id, TopicTagDTO topic) {
        //check authorization for update except admin
        return topicRepository.findById(id)
                .map(existingTopic -> {
                    // Check if the user is authorized to update the topic
                    User user = userRepository.findById(topic.getUserId())
                            .orElseThrow(() -> new IllegalArgumentException("User not found"));
                    if (!"admin".equals(user.getRole().getRoleName()) && !existingTopic.getUserId().equals(topic.getUserId())) {
                        throw new SecurityException("Bạn không có quyền sửa chủ đề này");
                    }
                    existingTopic.setTitle(topic.getTitle());
                    existingTopic.setContent(topic.getContent());
                    existingTopic.setUpdatedAt(LocalDateTime.now());
                    Topic updatedTopic = topicRepository.save(existingTopic);
                    topicTagRepository.deleteByTopic_TopicId(updatedTopic.getTopicId());

                    List<TopicTag> topicTags = topic.getTags().stream()
                            .map(tagId -> {
                                TopicTagId topicTagId = new TopicTagId(updatedTopic.getTopicId(), tagId);
                                Tag tag = tagRepository.findById(tagId)
                                        .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
                                return TopicTag.builder()
                                        .id(topicTagId)
                                        .topic(updatedTopic)
                                        .tag(tag)
                                        .build();
                            })
                            .toList();
                    topicTagRepository.saveAll(topicTags);
                    TopicResponse response = mapTopicToResponse(updatedTopic);
                    // Override tags with the new ones we just created
                    response.setTags(topicTags.stream()
                            .map(topicTag -> TagResponse.builder()
                                    .id(topicTag.getTag().getTagId())
                                    .name(topicTag.getTag().getName())
                                    .description(topicTag.getTag().getDescription())
                                    .build())
                            .toList());

                    return response;
                }).orElse(null);
    }

    @Override
    public void deleteTopic(Integer id) {
        topicRepository.findById(id)
                .ifPresent(topic -> {
                    topic.setClose(false);
                    topic.setUpdatedAt(LocalDateTime.now());
                    topicRepository.save(topic);
                });
    }

    @Override
    public Page<TopicResponse> searchTopics(String keyword, List<Integer> tagIds, PageRequest pageRequest) {
        if (tagIds == null || tagIds.isEmpty()) {
            Page<Topic> topics = topicTagRepository.findByTitleContainingOrContentContaining(
                    keyword != null ? keyword : "",
                    pageRequest);
            return topics.map(this::mapTopicToResponse);
        }

        // Nếu có cả keyword và tagIds
        int tagCount = tagIds.size();
        Page<Topic> topics = topicTagRepository.findByNameContainingAndTagsIn(
                keyword, tagIds, tagCount, pageRequest);
        return topics.map(this::mapTopicToResponse);
    }

    @Override
    public void pinTopic(Integer topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
        topic.setPinned(!topic.isPinned());
        topicRepository.save(topic);
    }
    private String getDefaultAvatar() {
        return "https://res.cloudinary.com/utejobhub/image/upload/v1745056474/UTE-removebg-preview_dz3ykb.png";
    }

    private TopicResponse mapTopicToResponse(Topic topic) {
        TopicResponse topicResponse = new TopicResponse();
        topicResponse.setTopicId(topic.getTopicId());
        topicResponse.setTitle(topic.getTitle());
        topicResponse.setContent(topic.getContent());
        topicResponse.setCreatedAt(topic.getCreatedAt());
        topicResponse.setUpdatedAt(topic.getUpdatedAt());
        topicResponse.setPinned(topic.isPinned());
        topicResponse.setClose(topic.isClose());
        topicResponse.setUserId(topic.getUserId());
        topicResponse.setForumId(topic.getForumId());
        topicResponse.setViewCount(topic.getViewCount());
        topicResponse.setStatus(topic.getStatus().toString());

        // Fetch user information once
        User user = userRepository.findById(topic.getUserId()).orElse(null);
        if (user != null) {
            String roleName = user.getRole().getRoleName();
            topicResponse.setRoleName(roleName.toUpperCase());

            // Get user name and avatar based on role
            if ("student".equals(roleName)) {
                Student student = studentRepository.findById(user.getUserId()).orElse(null);
                if (student != null) {
                    topicResponse.setUserName(student.getLastName() + " " + student.getFirstName());
                    topicResponse.setAvatar(student.getProfileImage());
                } else {
                    setDefaultUserInfo(topicResponse, "Unknown");
                }
            } else if ("employer".equals(roleName)) {
                Employer employer = employerRepository.findById(user.getUserId()).orElse(null);
                if (employer != null) {
                    topicResponse.setUserName(employer.getCompanyName());
                    topicResponse.setAvatar(employer.getCompanyLogo());
                } else {
                    setDefaultUserInfo(topicResponse, "Unknown");
                }
            } else {
                setDefaultUserInfo(topicResponse, roleName);
            }
        } else {
            setDefaultUserInfo(topicResponse, "Unknown");
        }

        // Get topic tags if needed
        List<TopicTag> topicTags = topicTagRepository.findByTopic_TopicId(topic.getTopicId());
        if (topicTags != null && !topicTags.isEmpty()) {
            topicResponse.setTags(topicTags.stream()
                    .map(topicTag -> TagResponse.builder()
                            .id(topicTag.getTag().getTagId())
                            .name(topicTag.getTag().getName())
                            .description(topicTag.getTag().getDescription())
                            .build())
                    .toList());
        }

        return topicResponse;
    }

    private void setDefaultUserInfo(TopicResponse topicResponse, String userName) {
        topicResponse.setUserName(userName);
        topicResponse.setAvatar("https://res.cloudinary.com/utejobhub/image/upload/v1745056474/UTE-removebg-preview_dz3ykb.png");
    }
}