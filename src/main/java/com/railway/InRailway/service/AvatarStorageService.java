package com.railway.InRailway.service;

import com.railway.InRailway.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AvatarStorageService {
    private final Path directory;
    public AvatarStorageService(@Value("${app.upload-dir:uploads/avatars}") String directory) { this.directory = Paths.get(directory).toAbsolutePath().normalize(); }
    public String store(MultipartFile file) {
        if (file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("image/")) throw new ApiException(HttpStatus.BAD_REQUEST, "A non-empty image avatar is required");
        String extension = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".") ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')) : ".bin";
        Path target = directory.resolve(UUID.randomUUID() + extension).normalize();
        if (!target.getParent().equals(directory)) throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid avatar filename");
        try { Files.createDirectories(directory); file.transferTo(target); return "/uploads/avatars/" + target.getFileName(); }
        catch (IOException e) { throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Avatar could not be stored"); }
    }
}