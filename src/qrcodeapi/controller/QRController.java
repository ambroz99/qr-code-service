package qrcodeapi.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import qrcodeapi.service.QRCodeGeneratorService;

import java.awt.image.BufferedImage;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api")
public class QRController {
    private static final int MIN_IMAGE_SIZE = 150;         // Shared with controller
    private static final int MAX_IMAGE_SIZE = 350;

    private static final String DEFAULT_SIZE = "250";
    private static final String DEFAULT_CORRECTION = "L";
    private static final String DEFAULT_TYPE = "png";

    private static final Map<String, MediaType> MEDIA_TYPE_MAP =
            Map.of(
                    "png", MediaType.IMAGE_PNG,
                    "jpeg", MediaType.IMAGE_JPEG,
                    "gif", MediaType.IMAGE_GIF
            );

    private final QRCodeGeneratorService qrCodeGeneratorService;

    public QRController(QRCodeGeneratorService qrCodeGeneratorService) {
        this.qrCodeGeneratorService = qrCodeGeneratorService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/health")
    public void getHealth() {}

    @GetMapping("/qrcode")
    public ResponseEntity<BufferedImage> getImage(
            @RequestParam
            @NotBlank(message = "Contents cannot be null or blank") String contents,

            @RequestParam(defaultValue = DEFAULT_SIZE)
            @Min(value = MIN_IMAGE_SIZE, message = "Image size must be between 150 and 350 pixels")
            @Max(value = MAX_IMAGE_SIZE, message = "Image size must be between 150 and 350 pixels") int size,

            @RequestParam(defaultValue = DEFAULT_CORRECTION)
            @Pattern(regexp = "[LMQH]",
                    message = "Permitted error correction levels are L, M, Q, H") String correction,

            @RequestParam(defaultValue = DEFAULT_TYPE)
            @Pattern(regexp = "png|jpeg|gif",
                    message = "Only png, jpeg and gif image types are supported") String type) {
        BufferedImage bufferedImage = qrCodeGeneratorService.createQRCode(contents, size, correction);

        return ResponseEntity
                .ok()
                .contentType(MEDIA_TYPE_MAP.get(type))
                .body(bufferedImage);
    }
}
