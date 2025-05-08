package org.example.controller;

import org.example.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.url:http://localhost:8533/uploads}")
    private String uploadUrl;

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 图片URL
     */
    @PostMapping("/image")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("400", "上传的文件不能为空");
        }

        try {
            // 确保上传目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成日期子目录
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String datePath = dateFormat.format(new Date());
            File dateDir = new File(uploadDir, datePath);
            if (!dateDir.exists()) {
                dateDir.mkdirs();
            }

            // 原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 生成新的文件名
            String newFileName = UUID.randomUUID().toString() + extension;

            // 保存文件
            File destFile = new File(dateDir, newFileName);
            file.transferTo(destFile);

            // 返回可访问的URL
            String imageUrl = uploadUrl + "/" + datePath + "/" + newFileName;
            return Result.success(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("500", "图片上传失败: " + e.getMessage());
        }
    }
} 