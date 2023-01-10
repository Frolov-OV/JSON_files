package org.perfLab;

import classesJSON.Test;
import classesJSON.Tests;
import classesJSON.Value;
import classesJSON.Values;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static Map<Integer, String> VALUES_MAP = new HashMap<>();
    public static Gson gson;

    public static void main(String[] args) {
        Scanner scanTest = new Scanner(System.in);
        Scanner scanValue = new Scanner(System.in);
        Scanner scanReport = new Scanner(System.in);
        System.out.println("Укажите путь к файлу тестами: ");
        String testJsonPath = scanTest.nextLine();
        System.out.println("Укажите путь к файлу со значениями: ");
        String valueJsonPath = scanValue.nextLine();
        System.out.println("Укажите путь, куда сохранить файл: ");
        String reportJsonPath = scanReport.nextLine() + "\\report.json";

        try (Reader testsReader = new FileReader(testJsonPath);
             Reader valuesReader = new FileReader(valueJsonPath);
             PrintWriter out = new PrintWriter(new FileWriter(reportJsonPath))){

            gson = new Gson();
            Values values = gson.fromJson(valuesReader, Values.class);
            for (Value vl : values.getValues()) {
                VALUES_MAP.put(vl.getId(), vl.getValue());
            }
            Tests tests = gson.fromJson(testsReader, Tests.class);
            for (Test ts : tests.getTests()) {
                setValues(ts);
                gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonString = gson.toJson(ts);
                out.write(jsonString);
            }
            System.out.println("\n" + "Файл успешно сохранен в папке: " + reportJsonPath + "\\report.json");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void setValues (Test test) {

        for (Integer id : Main.VALUES_MAP.keySet()){
            if (test.getId() == id) {
                test.setValue(Main.VALUES_MAP.get(id));
            }
            if (test.getValues() != null) {
                for (Test child : test.getValues()) {
                    setValues(child);
                }
            }
        }
    }

    private static String getJsonFile(String jsonPath) {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(jsonPath));
            lines.forEach(builder::append);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }

}
