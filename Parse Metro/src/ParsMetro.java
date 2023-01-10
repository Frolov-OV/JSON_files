import com.fasterxml.jackson.databind.ObjectMapper;
import metroCore.Lines;
import metroCore.Station;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.lang.annotation.ElementType;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import java.util.stream.Collectors;

public class ParsMetro {
    private static StationIndex stationIndex;

    private static Station station;

    public static List<Lines> parsMetroLines (String UrlPath) {
        List<Lines> lines = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(UrlPath).maxBodySize(0).get();
            Elements metroElements = doc.getElementsByClass("js-toggle-depend");
            Elements stationElement =
                    doc.getElementsByClass("js-metro-stations");
            for (Element metroDoc : metroElements) {
                Elements lineNumber = metroDoc.getElementsByAttribute("data-line");
                String id = lineNumber.attr("data-line");
                String name = metroDoc.text();
                lines.add(new Lines(id, name));
                }
            for(Lines line : lines) {
                List<String> stationList = stationElement.stream().filter(s -> s.attr("data-line")
                                .equals(line.getNumber()))
                        .map(s -> s.select("span.name"))
                        .flatMap(Collection::stream).map(Element::text)
                        .toList();
                for (String station : stationList) {
                    line.addStation(new Station(station, line));
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("Some Exception, you must have to catch it");
        }
        return lines;
    }


    public static Map<String, Map<String, List<String>>> parsMetroConnection (String UrlPath) {
        // TODO: * Пропарсите и записывайте в JSON-файл переходы между станциями в дополнение
        //  к линиям и станциям (коллекции имя станции, номер линии, между которым есть переходы).
        Map<String,  Map<String, List<String>>> metroConnections = new TreeMap<>();
        try {
            Document doc = Jsoup.connect(UrlPath).maxBodySize(0).get();
            Elements metroElements = doc.getElementsByClass("js-metro-stations");
            for (Element metroDoc : metroElements) {
                Elements lineNumber = metroDoc.getElementsByAttribute("data-line");
                String id = lineNumber.attr("data-line");
                String name = metroDoc.text();
                Lines lines = new Lines(id, name);
                Map<String, List<String>> connections = new TreeMap<>();
                List<String> stationList = new ArrayList<>();
                Elements connectStation = metroDoc.getElementsByClass("t-icon-metroln");
                    if (id.equals(lines.getNumber())){
                        for (Element conn : connectStation) {
                            String idConnect = conn.attr("class")
                                    .replaceAll("t-icon-metroln ln-", "");
                            stationList.add(idConnect + " " + conn.attr("title")
                                    .substring(conn.attr("title")
                                            .indexOf("«") + 1, conn.attr("title").indexOf("»")));
                            }
                        }

                connections.put("Connection_station", stationList);
                metroConnections.put("Line_number: " + id, connections);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("Some Exception, you must have to catch it");
        }
        return metroConnections;
    }

    public void setJsonFile (String jsonFilePath, String urlLink) throws Exception {
        // TODO: 3. Создаёт и записывает на диск JSON-файл
        //  со списком станций по линиям и списком линий по формату JSON-файла из проекта SPBMetro (файл map.json).
        Document doc = Jsoup.connect(urlLink).maxBodySize(0).get();

        Elements stationElement = doc.getElementsByClass("js-metro-stations");
        Map<String, List<String>> lineMap = stationElement.stream()
                .collect(Collectors.toMap(key -> key.attr("data-line"),
                        val -> val.getElementsByClass("name").stream()
                                .map(Element::text).collect(Collectors.toList())));

        JSONObject jsonFileObj = new JSONObject();
        JSONArray stationArray = new JSONArray();
            for (String nameKey : lineMap.keySet()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Stations", lineMap.get(nameKey));
                jsonObject.put("Line", nameKey);
/*                for (String nameLine : nameLinesList) {
                    jsonObject.put("Line", nameLine);
                }*/
                stationArray.add(jsonObject);
            }
            jsonFileObj.put("Lines", stationArray);
            jsonFileObj.put("Connection", parsMetroConnection(urlLink));

        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonFileObj);
        Files.write(Paths.get(jsonFilePath), str.getBytes());
    }

}
