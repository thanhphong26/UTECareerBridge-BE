package com.pn.career.services.forum;

import com.pn.career.dtos.Forum.PostDTO;
import com.pn.career.models.*;
import com.pn.career.repositories.EmployerRepository;
import com.pn.career.repositories.StudentRepository;
import com.pn.career.repositories.UserRepository;
import com.pn.career.repositories.forum.CommentRepository;
import com.pn.career.repositories.forum.PostRepository;
import com.pn.career.repositories.forum.ReactionRepository;
import com.pn.career.responses.forum.PostResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final StudentRepository studentRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;

    @Override
    public Page<PostResponse> getAllPosts(PageRequest pageRequest) {
        Page<Post> posts = postRepository.findAll(pageRequest);
        return posts.map(this::mapTopicToResponse);
    }

    @Override
    public Optional<PostResponse> getPostById(Integer id) {
        return postRepository.findById(id)
                .map(this::mapTopicToResponse);
    }

    @Override
    public Page<PostResponse> getPostsByTopicId(Integer topicId, PageRequest pageRequest) {
        Page<Post> posts = postRepository.findByTopicId(topicId, pageRequest);
        return posts.map(this::mapTopicToResponse);
    }

    @Override
    public Page<PostResponse> getPostsByUserId(Integer userId, PageRequest pageRequest) {
        Page<Post> posts = postRepository.findByUserId(userId, pageRequest);
        return posts.map(this::mapTopicToResponse);
    }

    @Override
    public PostResponse createPost(PostDTO post, Integer userId) {
        Post newPost = Post.builder()
                .content(post.getContent())
                .topicId(post.getTopicId())
                .userId(userId)
                .active(true)
                .build();
        Post savePost = postRepository.save(newPost);
        return mapTopicToResponse(savePost);
    }

    @Override
    @Transactional
    public PostResponse updatePost(Integer id, PostDTO post, Integer userId) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        //check authorize for update
        if (existingPost.getUserId() != userId || existingPost.getUserId() != existingPost.getUserId()) {
            throw new AuthorizationServiceException("Bạn không có quyền sửa bài viết này");
        }
        existingPost.setContent(post.getContent());
        existingPost.setTopicId(post.getTopicId());
        existingPost.setUpdatedAt(LocalDateTime.now());
        Post updatedPost = postRepository.save(existingPost);
        return mapTopicToResponse(updatedPost);
    }

    @Override
    public void deletePost(Integer id, Integer userId) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        //check authorize for delete
        if (existingPost.getUserId() != userId || existingPost.getUserId() != existingPost.getUserId()) {
            throw new AuthorizationServiceException("Bạn không có quyền xóa bài viết này");
        }
        postRepository.deleteById(id);
    }

    @Override
    public Page<PostResponse> searchPosts(String content, PageRequest pageRequest) {
        Page<Post> posts = postRepository.findByContentContainingIgnoreCase(content, pageRequest);
        return posts.map(this::mapTopicToResponse);
    }
    private PostResponse mapTopicToResponse(Post post) {
        PostResponse postResponse = PostResponse.fromPost(post);
        postResponse.setReactionCount(reactionRepository.countByPostId(post.getPostId()));
        postResponse.setCommentCount(commentRepository.countByPostId(post.getPostId()));
        User user = userRepository.findById(postResponse.getUserId()).orElse(null);
        if (user != null) {
            String roleName = user.getRole().getRoleName();
            postResponse.setRoleName(roleName.toUpperCase());

            if ("student".equals(roleName)) {
                Student student = studentRepository.findById(user.getUserId()).orElse(null);
                if (student != null) {
                    postResponse.setUserName(student.getLastName() + " " + student.getFirstName());
                    postResponse.setAvatar(student.getProfileImage());
                } else {
                    setDefaultUserInfo(postResponse, "Unknown");
                }
            } else if ("employer".equals(roleName)) {
                Employer employer = employerRepository.findById(user.getUserId()).orElse(null);
                if (employer != null) {
                    postResponse.setUserName(employer.getCompanyName());
                    postResponse.setAvatar(employer.getCompanyLogo());
                } else {
                    setDefaultUserInfo(postResponse, "Unknown");
                }
            } else {
                setDefaultUserInfo(postResponse, roleName);
            }
        } else {
            setDefaultUserInfo(postResponse, "Unknown");
        }

        return postResponse;
    }

    private void setDefaultUserInfo(PostResponse postResponse, String userName) {
        postResponse.setUserName(userName);
        postResponse.setAvatar("https://res.cloudinary.com/utejobhub/image/upload/v1745056474/UTE-removebg-preview_dz3ykb.png");
    }
}
