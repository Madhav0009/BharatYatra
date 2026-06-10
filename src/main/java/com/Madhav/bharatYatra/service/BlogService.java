package com.Madhav.bharatYatra.service;

import com.Madhav.bharatYatra.dto.BlogDTO;
import com.Madhav.bharatYatra.dto.BlogRequest;
import com.Madhav.bharatYatra.exception.ResourceNotFoundException;
import com.Madhav.bharatYatra.model.Blog;
import com.Madhav.bharatYatra.model.TouristPlace;
import com.Madhav.bharatYatra.model.User;
import com.Madhav.bharatYatra.repository.BlogRepository;
import com.Madhav.bharatYatra.repository.TouristPlaceRepository;
import com.Madhav.bharatYatra.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// BLOG SERVICE


@Service
@RequiredArgsConstructor
@Slf4j
public class BlogService {

    private final BlogRepository blogRepo;
    private final TouristPlaceRepository placeRepo;
    private final UserRepository userRepo;

    // CREATE BLOG
    @Transactional
    public BlogDTO createBlog(BlogRequest req, String userEmail) {

        // Find tourist place
        TouristPlace place = placeRepo.findById(req.getPlaceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Place not found"));

        // Find logged in user
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        // Create blog object
        Blog blog = Blog.builder()
                .place(place)
                .user(user)
                .title(req.getTitle())
                .category(req.getCategory())
                .content(req.getContent())
                .emoji(req.getEmoji())
                .published(true)
                .build();

        // Save blog
        blog = blogRepo.save(blog);

        log.info("Blog created successfully by user: {}", userEmail);

        return toDTO(blog);
    }

    // GET BLOGS FOR PLACE
    public Page<BlogDTO> getBlogsForPlace(
            Long placeId,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return blogRepo.findByPlaceIdAndPublished(
                placeId,
                true,
                pageable
        ).map(this::toDTO);
    }

    // CONVERT ENTITY TO DTO
    private BlogDTO toDTO(Blog blog) {

        return BlogDTO.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .category(blog.getCategory())
                .content(blog.getContent())
                .emoji(blog.getEmoji())
                .bgGradient(blog.getBgGradient())
                .thumbnailUrl(blog.getThumbnailUrl())
                .authorName(blog.getUser().getFullName())
                .createdAt(blog.getCreatedAt())
                .build();
    }
}