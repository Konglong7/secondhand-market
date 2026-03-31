package com.secondhand.market.controller;

import com.secondhand.market.common.Result;
import com.secondhand.market.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadFile(file);
        return Result.success(url);
    }

    @PostMapping("/upload/batch")
    public Result<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = fileService.uploadFile(file);
            urls.add(url);
        }
        return Result.success(urls);
    }
}
