package com.auth0.example;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class FilesStorageService {

    private final Path uploadDir;
    private Path rootLocation;

    public FilesStorageService() {
        this.uploadDir = Path.of("uploads");
        initialize();
    }

    public void initialize() {
        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize file storage", e);
        }
    }

    public void save(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String storedLocation;
        rootLocation = Paths.get(uploadDir.toUri());
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file: " + filename);
            }
            //Path targetLocation = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename),StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + filename, e);
        }
    }

    public List<String> getAllFiles() {
        List<String> files = new ArrayList<>();
        try {
            Files.walk(uploadDir)
                    .filter(Files::isRegularFile)
                    .forEach(file -> files.add(file.getFileName().toString()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve files from storage", e);
        }
        return files;
    }

    public File getFile(String filename) throws IOException {
        File my_file = new File(uploadDir.resolve(filename).toUri());

        String outputFile = filename;

        try (BufferedReader br = new BufferedReader(new FileReader(my_file));
             FileWriter writer = new FileWriter(outputFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                for (String data : row) {
                    writer.append(data);
                    writer.append(",");
                }
                writer.append("\n");
            }
        }
        return new File(outputFile);
    }

}


