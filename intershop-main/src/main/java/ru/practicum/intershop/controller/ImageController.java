package ru.practicum.intershop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import jakarta.validation.constraints.Pattern;

@RestController
@Validated
public class ImageController {

    @Value("${image.folder}")
    private String imageFolder;

    @GetMapping("/images/{imgPath}")
    public Mono<ResponseEntity<Resource>> getImage(
            @PathVariable(name = "imgPath") 
            @Pattern(regexp = "^[a-zA-Z0-9._-]+\\.(jpg|jpeg|png|gif)$", 
                     message = "Invalid image file name") 
            String imgPath) {
        return Mono.fromCallable(() -> {
            String path = imageFolder + "/" + imgPath;
            Resource resource = new ClassPathResource(path);

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).<Resource>body(null);
            }

            // Определяем MIME тип
            MediaType mediaType = MediaType.IMAGE_PNG; // по умолчанию
            if (imgPath.toLowerCase().endsWith(".jpg") || imgPath.toLowerCase().endsWith(".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            } else if (imgPath.toLowerCase().endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (imgPath.toLowerCase().endsWith(".gif")) {
                mediaType = MediaType.IMAGE_GIF;
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(resource);
        })
        .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Resource>body(null));
    }
}
