import metroCore.Lines;
import metroCore.Station;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.*;

public class Main {

    private static final String DATA_FILE = "src/main/resources/metroMap.json";
    private static Scanner scanner;
    private static StationIndex stationIndex;

    static String url = "https://skillbox-java.github.io";

    public static void main(String[] args) throws Exception {

        ParsMetro parsMetro = new ParsMetro();
        parsMetro.setJsonFile(DATA_FILE, url);
        JSONParser parser = new JSONParser();
        try (FileReader fileReader = new FileReader(DATA_FILE)) {
            JSONObject jsonData = (JSONObject) parser.parse(fileReader);
            JSONObject connectObj = (JSONObject) jsonData.get("Connection");
            JSONObject connection = null;
            Map<String, String> metroMap = new TreeMap<>();
            List<String> metroConnect = new ArrayList<>();
            JSONArray linesArray = (JSONArray) jsonData.get("Lines");
            String line = "";
            for (Object jsArr : linesArray) {
                JSONObject jsonList = (JSONObject) jsArr;
                line = (String) jsonList.get("Line");
                connection = (JSONObject) connectObj.get("Line_number: " + line);
                JSONArray connectArray = (JSONArray) connection.get("Connection_station");
                for (Object jsConn : connectArray) {
                    metroConnect.add(String.valueOf(jsConn.toString()));
                }
                metroMap.put("Line: " + line, " Has stations: " + ((JSONArray) jsonList.get("Stations")).size());
            }
            System.out.println(metroMap + "\n");
            System.out.println("In total, there are " + metroConnect.size() + " transfers in the metro");
        }

    }


    private static void parseConnections(JSONArray connectionsArray) {
        connectionsArray.forEach(connectionObject ->
        {
            JSONArray connection = (JSONArray) connectionObject;
            List<Station> connectionStations = new ArrayList<>();
            connection.forEach(item ->
            {
                JSONObject itemObject = (JSONObject) item;
                String lineNumber = (String) itemObject.get("line");
                String stationName = (String) itemObject.get("station");

                Station station = stationIndex.getStation(stationName, lineNumber);
                if (station == null) {
                    throw new IllegalArgumentException("core.Station " +
                            stationName + " on line " + lineNumber + " not found");
                }
                connectionStations.add(station);
            });
            stationIndex.addConnection(connectionStations);
        });
    }

    private static void parseStations(JSONObject stationsObject) {
        stationsObject.keySet().forEach(lineNumberObject ->
        {
            int lineNumber = Integer.parseInt((String) lineNumberObject);
            Lines line = stationIndex.getLine(String.valueOf(lineNumber));
            JSONArray stationsArray = (JSONArray) stationsObject.get(lineNumberObject);
            stationsArray.forEach(stationObject ->
            {
                Station station = new Station((String) stationObject, line);
                stationIndex.addStation(station);
                line.addStation(station);
            });
        });
    }

    private static void parseLines(JSONArray linesArray) {
        linesArray.forEach(lineObject -> {
            JSONObject lineJsonObject = (JSONObject) lineObject;
            Lines line = new Lines(
                    ((String) lineJsonObject.get("number")),
                    (String) lineJsonObject.get("name")
            );
            stationIndex.addLine(line);
        });
    }
}
