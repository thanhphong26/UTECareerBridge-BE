package com.pn.career.services.forum;

import com.pn.career.dtos.Forum.CommentDTO;
import com.pn.career.exceptions.DataNotFoundException;
import com.pn.career.models.*;
import com.pn.career.repositories.EmployerRepository;
import com.pn.career.repositories.StudentRepository;
import com.pn.career.repositories.UserRepository;
import com.pn.career.repositories.forum.CommentRepository;
import com.pn.career.repositories.forum.PostRepository;
import com.pn.career.responses.forum.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EmployerRepository employerRepository;
    private final StudentRepository studentRepository;
    private final PostRepository postRepository;

    @Override
    public Page<CommentResponse> getAllComments(PageRequest pageRequest) {
        Page<Comment> comments = commentRepository.findAll(pageRequest);
        return comments.map(this::mapTopicToResponse);
    }

    @Override
    public Optional<CommentResponse> getCommentById(Integer id) {
        return commentRepository.findById(id)
                .map(this::mapTopicToResponse);
    }

    @Override
    public Page<CommentResponse> getCommentsByPostId(Integer postId, PageRequest pageRequest) {
        Page<Comment> comments = commentRepository.findByPostId(postId, pageRequest);
        return comments.map(this::mapTopicToResponse);
    }

    @Override
    public Page<CommentResponse> getRootCommentsByPostId(Integer postId, PageRequest pageRequest) {
        return commentRepository.findByPostIdAndParentCommentIdIsNullOrderByCreatedAtDesc(postId, pageRequest)
                .map(this::mapTopicToResponse);
    }

    @Override
    public Page<CommentResponse> getChildComments(Integer parentCommentId, PageRequest pageRequest) {
        return commentRepository.findByParentCommentId(parentCommentId, pageRequest)
                .map(this::mapTopicToResponse);
    }

    @Override
    public Page<CommentResponse> getCommentsByUserId(Integer userId, PageRequest pageRequest) {
        return commentRepository.findByUserId(userId, pageRequest)
                .map(this::mapTopicToResponse);
    }

    @Override
    public CommentResponse createComment(CommentDTO comment, Integer userId) {
        Comment newComment = Comment.builder()
                .postId(comment.getPostId())
                .userId(userId)
                .content(comment.getContent())
                .parentCommentId(comment.getParentId())
                .build();

        Comment savedComment = commentRepository.save(newComment);
        return mapTopicToResponse(savedComment);
    }

    @Override
    public CommentResponse updateComment(Integer id, CommentDTO comment, Integer userId) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông tin thảo luận"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        boolean isAdmin = "admin".equals(user.getRole().getRoleName());
        boolean isCommentCreator = existingComment.getUserId().equals(userId);
        boolean isPostCreator = false;

        // Check if user is the post creator
        Optional<Post> post = postRepository.findById(existingComment.getPostId());
        if (post.isPresent() && post.get().getUserId().equals(userId)) {
            isPostCreator = true;
        }

        if (!isCommentCreator && !isAdmin && !isPostCreator) {
            throw new DataNotFoundException("Bạn không có quyền sửa thông tin thảo luận này");
        }

        existingComment.setContent(comment.getContent());
        existingComment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(existingComment);
        return mapTopicToResponse(updatedComment);
    }

    @Override
    public void deleteComment(Integer id, Integer userId) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy thông tin thảo luận"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        boolean isAdmin = "admin".equals(user.getRole().getRoleName());
        boolean isCommentCreator = existingComment.getUserId().equals(userId);
        boolean isPostCreator = false;

        // Check if user is the post creator
        Optional<Post> post = postRepository.findById(existingComment.getPostId());
        if (post.isPresent() && post.get().getUserId().equals(userId)) {
            isPostCreator = true;
        }

        if (!isCommentCreator && !isAdmin && !isPostCreator) {
            throw new DataNotFoundException("Bạn không có quyền xóa thông tin thảo luận này");
        }

        commentRepository.deleteById(id);
    }

    @Override
    public Integer countCommentsByPostId(Integer postId) {
        return commentRepository.countByPostId(postId);
    }

    private CommentResponse mapTopicToResponse(Comment comment) {
        CommentResponse commentResponse = CommentResponse.fromComment(comment);
        int totalReplyCount = commentRepository.countByParentCommentId(comment.getCommentId());
        commentResponse.setReplyCount(totalReplyCount);
        int loadedRepliesCount = comment.getChildComments() != null ? comment.getChildComments().size() : 0;
        boolean hasMoreReplies = loadedRepliesCount < totalReplyCount;
        commentResponse.setHasMoreReplies(hasMoreReplies);

        User user = userRepository.findById(commentResponse.getUserId()).orElse(null);
        if (user != null) {
            String roleName = user.getRole().getRoleName();
            commentResponse.setRoleName(roleName.toUpperCase());

            if ("student".equals(roleName)) {
                Student student = studentRepository.findById(user.getUserId()).orElse(null);
                if (student != null) {
                    commentResponse.setUserName(student.getLastName() + " " + student.getFirstName());
                    commentResponse.setAvatar(student.getProfileImage());
                } else {
                    setDefaultUserInfo(commentResponse, "Unknown");
                }
            } else if ("employer".equals(roleName)) {
                Employer employer = employerRepository.findById(user.getUserId()).orElse(null);
                if (employer != null) {
                    commentResponse.setUserName(employer.getCompanyName());
                    commentResponse.setAvatar(employer.getCompanyLogo());
                } else {
                    setDefaultUserInfo(commentResponse, "Unknown");
                }
            } else {
                setDefaultUserInfo(commentResponse, roleName);
            }
        } else {
            setDefaultUserInfo(commentResponse, "Unknown");
        }

        return commentResponse;
    }

    private void setDefaultUserInfo(CommentResponse topicResponse, String userName) {
        topicResponse.setUserName(userName);
        topicResponse.setAvatar("https://res.cloudinary.com/utejobhub/image/upload/v1745056474/UTE-removebg-preview_dz3ykb.png");
    }
}
