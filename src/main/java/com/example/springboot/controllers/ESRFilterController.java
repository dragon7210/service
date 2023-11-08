package com.example.springboot.controllers;

import com.example.springboot.models.VegModel;
import com.example.springboot.payload.UploadFileResponse;
import com.example.springboot.repositories.VegRepository;
import com.example.springboot.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
public class ESRFilterController {

    @Autowired
    VegRepository peopleRepository;
    @Autowired
    private FileStorageService fileStorageService;
    @GetMapping("/showSql")
    public List<VegModel> getAllEmployees() {
        return peopleRepository.findAll();
    }
    @PostMapping("/uploadFile")//
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file, "uploadFile");
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

}