package com.auth0.example;

import com.opencsv.exceptions.CsvValidationException;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the home page.
 */
@Controller
public class HomeController {


    @Autowired
    CSVToWordConverter csvToWordConverter;

    @Autowired
    CSVtoJSONConverter csvtoJSONConverter;
    private static final String UPLOAD_FOLDER = "uploads/";
    private static final String WORD_FOLDER = "words/";
    private static final String JSON_FOLDER = "json/";

    @GetMapping("/login")
    public String profile() {
        return "login";
    }


    @GetMapping("/success")
    public String success(Model model) {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);
            csvToWordConverter.convertCSVToWord(String.valueOf(path),WORD_FOLDER + file.getOriginalFilename());

            try {
                List<String[]> csvData = csvtoJSONConverter.readCSV(String.valueOf(path));
                JSONArray jsonArray = csvtoJSONConverter.convertToJSON(csvData);
                csvtoJSONConverter.writeJSON(jsonArray, JSON_FOLDER + file.getOriginalFilename());
                System.out.println("CSV to JSON conversion completed successfully.");
            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
            }

            redirectAttributes.addFlashAttribute("message", "File uploaded successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "File upload failed!");
        }
        return "upload";
    }

    @GetMapping("/download/{fileName:.+}")
    public void downloadFile(@PathVariable String fileName, HttpServletResponse response) {
        try {
            Path path = Paths.get(WORD_FOLDER + fileName);
            byte[] data = Files.readAllBytes(path);
            response.setContentType("application/doc");
            response.setContentLength(data.length);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            FileCopyUtils.copy(data, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ModelAttribute("files")
    public List<String> getUploadedFiles() {
        List<String> files = new ArrayList<>();
        try {
            Files.list(Paths.get(WORD_FOLDER)).forEach(file -> files.add(file.getFileName().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    @GetMapping("/downloadJSON/{fileName:.+}")
    public void downloadJsonFile(@PathVariable String fileName, HttpServletResponse response) {
        try {
            Path path = Paths.get(JSON_FOLDER + fileName);
            byte[] data = Files.readAllBytes(path);
            response.setContentType("application/json");
            response.setContentLength(data.length);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            FileCopyUtils.copy(data, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ModelAttribute("jsonFiles")
    public List<String> getJsonUploadedFiles() {
        List<String> jsonFiles = new ArrayList<>();
        try {
            Files.list(Paths.get(JSON_FOLDER)).forEach(file -> jsonFiles.add(file.getFileName().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonFiles;
    }


}