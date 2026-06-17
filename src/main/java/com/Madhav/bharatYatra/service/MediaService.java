package com.Madhav.bharatYatra.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.Madhav.bharatYatra.dto.MediaDTO;
import com.Madhav.bharatYatra.exception.ResourceNotFoundException;
import com.Madhav.bharatYatra.model.MediaUpload;
import com.Madhav.bharatYatra.model.TouristPlace;
import com.Madhav.bharatYatra.model.User;
import com.Madhav.bharatYatra.repository.MediaUploadRepository;
import com.Madhav.bharatYatra.repository.TouristPlaceRepository;
import com.Madhav.bharatYatra.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MediaService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.base-url}")
    private String baseUrl;

    private final MediaUploadRepository mediaRepo;
    private final TouristPlaceRepository placeRepo;
    private final UserRepository userRepo;

    // MANUAL CONSTRUCTOR
    public MediaService(
            MediaUploadRepository mediaRepo,
            TouristPlaceRepository placeRepo,
            UserRepository userRepo
    ) {
        this.mediaRepo = mediaRepo;
        this.placeRepo = placeRepo;
        this.userRepo = userRepo;
    }

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "video/mp4",
            "video/quicktime",
            "video/x-msvideo"
    );

    private static final long MAX_SIZE = 500L * 1024 * 1024;

    @Transactional
    public MediaDTO upload(
            Long placeId,
            MultipartFile file,
            String caption,
            String userEmail
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a file"
            );
        }

        if (file.getContentType() == null ||
                !ALLOWED_TYPES.contains(file.getContentType())) {

            throw new IllegalArgumentException(
                    "Invalid file type"
            );
        }

        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException(
                    "File exceeds 500MB"
            );
        }

        TouristPlace place = placeRepo.findById(placeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Place not found"
                        )
                );

        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );

        String fileName =
                UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path uploadPath =
                Paths.get(uploadDir, String.valueOf(placeId));

        Files.createDirectories(uploadPath);

        Files.copy(
                file.getInputStream(),
                uploadPath.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING
        );
        
        log.info("Saved file at: {}",
                uploadPath.resolve(fileName).toAbsolutePath());

        String fileUrl =
                baseUrl + placeId + "/" + fileName;

        boolean isVideo =
                file.getContentType().startsWith("video");

        MediaUpload media = MediaUpload.builder()
                .place(place)
                .user(user)
                .fileUrl(fileUrl)
                .fileName(file.getOriginalFilename())
                .fileSizeBytes(file.getSize())
                .mediaType(
                        isVideo
                                ? MediaUpload.MediaType.VIDEO
                                : MediaUpload.MediaType.PHOTO
                )
                .caption(caption)
                .approved(true)
                .build();

        media = mediaRepo.save(media);

        log.info(
                "Media uploaded successfully by user: {}",
                userEmail
        );

        return toDTO(media);
    }

    @Transactional(readOnly = true)
    public Page<MediaDTO> getMedia(
            Long placeId,
            String type,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("uploadedAt").descending()
        );

        Page<MediaUpload> uploads;

        if (type != null && !type.isBlank()) {

            MediaUpload.MediaType mediaType =
                    MediaUpload.MediaType.valueOf(
                            type.toUpperCase()
                    );

            uploads = mediaRepo.findByPlaceIdAndMediaType(
                    placeId,
                    mediaType,
                    pageable
            );

        } else {

            uploads = mediaRepo.findByPlaceId(
                    placeId,
                    pageable
            );
        }

        return uploads.map(this::toDTO);
    }
    
    private MediaDTO toDTO(MediaUpload media) {

        return MediaDTO.builder()
                .id(media.getId())
                .fileUrl(media.getFileUrl())
                .fileName(media.getFileName())
                .mediaType(media.getMediaType().name())
                .caption(media.getCaption())
                .uploaderName(media.getUser().getFullName())
                .uploadedAt(media.getUploadedAt())
                .build();
    }
}