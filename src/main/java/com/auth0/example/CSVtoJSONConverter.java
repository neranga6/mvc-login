package com.auth0.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Service
public class CSVtoJSONConverter {

    public List<String[]> readCSV(String filePath) throws IOException, CsvValidationException {
        List<String[]> csvData = new ArrayList<>();
        CSVReader reader = new CSVReader(new FileReader(filePath));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            csvData.add(nextLine);
        }
        reader.close();
        return csvData;
    }

    public JSONArray convertToJSON(List<String[]> csvData) {
        JSONArray jsonArray = new JSONArray();
        String[] headers = csvData.get(0);
        for (int i = 1; i < csvData.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            String[] rowData = csvData.get(i);
            for (int j = 0; j < headers.length; j++) {
                jsonObject.put(headers[j], rowData[j]);
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

      public void writeJSON(JSONArray jsonArray, String filePath) throws IOException {
        String replacedExtensionOutputFilePath = replaceExtension(filePath,"json");
        FileWriter fileWriter = new FileWriter(replacedExtensionOutputFilePath);
        fileWriter.write(jsonArray.toJSONString());
        fileWriter.close();
    }

    public static String replaceExtension(String filePath, String newExtension) {
        int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return filePath.substring(0, lastDotIndex) + "." + newExtension;
        }
        return filePath;
    }
}
