package com.secondhand.market.service.impl;

import com.secondhand.market.common.BusinessException;
import com.secondhand.market.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传服务实现
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:/uploads/}")
    private String urlPrefix;

    /**
     * 允许的文件扩展名
     */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    ));

    /**
     * 允许的图片Content-Type
     */
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    ));

    /**
     * 最大文件大小（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 图片文件最小大小（100字节，防止空文件）
     */
    private static final long MIN_FILE_SIZE = 100;

    @Override
    public String uploadFile(MultipartFile file) {
        // 1. 空文件检查
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }

        // 2. 文件大小检查
        long fileSize = file.getSize();
        if (fileSize > MAX_FILE_SIZE) {
            throw new BusinessException(400, "文件大小不能超过10MB");
        }
        if (fileSize < MIN_FILE_SIZE) {
            throw new BusinessException(400, "文件太小，可能不是有效的图片文件");
        }

        // 3. Content-Type验证
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(400, "只支持jpg、jpeg、png、gif、webp格式的图片文件");
        }

        // 4. 文件名验证
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException(400, "文件名不能为空");
        }

        // 5. 扩展名验证
        String extension = getFileExtension(originalFilename);
        if (extension.isEmpty() || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException(400, "文件扩展名不支持，只支持jpg、jpeg、png、gif、webp格式");
        }

        // 6. 生成安全的文件名（UUID + 扩展名）
        String newFilename = UUID.randomUUID().toString() + "." + extension.toLowerCase();

        // 7. 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            try {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    log.error("上传目录创建失败: {}", uploadPath);
                    throw new BusinessException(500, "上传目录创建失败");
                }
            } catch (SecurityException e) {
                log.error("无权限创建上传目录: {}", uploadPath, e);
                throw new BusinessException(500, "无权限创建上传目录");
            }
        }

        // 8. 验证上传目录是否可写
        if (!uploadDir.canWrite()) {
            log.error("上传目录不可写: {}", uploadPath);
            throw new BusinessException(500, "上传目录不可写");
        }

        // 9. 构建目标文件路径（防止路径遍历攻击）
        Path destPath = Paths.get(uploadDir.getAbsolutePath(), newFilename).normalize();
        File destFile = destPath.toFile();

        // 验证目标路径在允许的上传目录内
        if (!destPath.startsWith(Paths.get(uploadDir.getAbsolutePath()).normalize())) {
            log.error("文件路径安全检查失败: 企图路径遍历攻击");
            throw new BusinessException(400, "非法文件路径");
        }

        // 10. 执行文件上传
        try {
            file.transferTo(destFile);

            // 11. 验证文件是否真实上传成功
            if (!destFile.exists() || destFile.length() != fileSize) {
                log.error("文件上传验证失败: expected={}, actual={}", fileSize, destFile.length());
                throw new BusinessException(500, "文件上传验证失败");
            }

            log.info("文件上传成功: filename={}, size={}, contentType={}", newFilename, fileSize, contentType);
            return urlPrefix + newFilename;
        } catch (IOException e) {
            log.error("文件上传IO异常: {}", e.getMessage(), e);
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        } catch (SecurityException e) {
            log.error("文件上传安全异常: {}", e.getMessage(), e);
            throw new BusinessException(500, "无权限写入文件");
        }
    }

    /**
     * 安全获取文件扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }

        String ext = filename.substring(lastDotIndex + 1).toLowerCase();

        // 扩展名安全校验：只允许字母数字，且长度不超过5
        if (!ext.matches("^[a-z0-9]+$") || ext.length() > 5) {
            return "";
        }

        return ext;
    }

    /**
     * 删除上传的文件
     */
    public boolean deleteFile(String filename) {
        try {
            Path filePath = Paths.get(uploadPath, filename).normalize();
            File file = filePath.toFile();

            // 验证文件路径在允许的上传目录内
            if (!filePath.startsWith(Paths.get(uploadPath).normalize())) {
                log.error("文件删除路径安全检查失败: {}", filename);
                return false;
            }

            if (file.exists() && file.isFile()) {
                boolean deleted = Files.deleteIfExists(filePath);
                log.info("文件删除: filename={}, success={}", filename, deleted);
                return deleted;
            }
            return false;
        } catch (IOException e) {
            log.error("文件删除失败: {}", filename, e);
            return false;
        }
    }
}