package com.auth0.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

@Service
public class CSVToWordConverter {

    public void convertCSVToWord(String csvFilePath, String outputFilePath) {


        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            XWPFDocument document = new XWPFDocument();
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (String value : values) {
                    run.setText(value);
                    run.addCarriageReturn();
                }
            }


            String replacedExtensionOutputFilePath = replaceExtension(outputFilePath, "doc");

            FileOutputStream outputStream = new FileOutputStream(replacedExtensionOutputFilePath);
            document.write(outputStream);
            outputStream.close();
            document.close();

            System.out.println("CSV file converted to Word successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static String replaceExtension(String filePath, String newExtension) {
        int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return filePath.substring(0, lastDotIndex) + "." + newExtension;
        }
        return filePath;
    }


}

