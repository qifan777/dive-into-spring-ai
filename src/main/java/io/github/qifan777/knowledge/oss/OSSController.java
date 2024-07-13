package io.github.qifan777.knowledge.oss;

import io.qifan.infrastructure.common.constants.ResultCode;
import io.qifan.infrastructure.common.exception.BusinessException;
import io.qifan.infrastructure.common.model.R;
import io.qifan.infrastructure.oss.service.OSSService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("oss")
@AllArgsConstructor
public class OSSController {
    private final OSSService ossService;

    @PostMapping("upload")
    public R<Map<String, String>> upload(@RequestParam Map<String, MultipartFile> files) {
        List<String> arrayList = new ArrayList<>();
        files.forEach((String key, MultipartFile file) -> {
            try {
                String url = ossService.upload(file);
                arrayList.add(url);
            } catch (Exception e) {
                throw new BusinessException(ResultCode.TransferStatusError, "上传失败");
            }
        });
        String join = String.join(";", arrayList);
        Map<String, String> urlMap = new HashMap<>();
        urlMap.put("url", join);
        return R.ok(urlMap);
    }
}
