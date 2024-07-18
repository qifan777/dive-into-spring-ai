package io.github.qifan777.knowledge.oss;

import io.qifan.infrastructure.oss.service.OSSService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("oss")
@AllArgsConstructor
public class OSSController {
    private final OSSService ossService;

    @PostMapping("upload")
    public String upload(@RequestParam MultipartFile file) {
        return ossService.upload(file);
    }
}
