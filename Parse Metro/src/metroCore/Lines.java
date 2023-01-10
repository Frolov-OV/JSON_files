package metroCore;

import java.util.ArrayList;
import java.util.List;

public class Lines implements Comparable<Lines>{
    private String number;
    private String name;
    private List<Station> stations;

    public Lines(String number, String name){
        this.number = number;
        this.name = name;
        stations = new ArrayList<>();
    }

    public String getNumber(){
        return number;
    }

    public String getName(){
        return name;
    }

    public void addStation(Station station){
        stations.add(station);
    }

    public List<Station> getStations(){
        return stations;
    }

    @Override
    public int compareTo(Lines lines){
        return Integer.compare(Integer.parseInt(number), Integer.parseInt(lines.getNumber()));
    }

    @Override
    public boolean equals(Object obj){
        return compareTo((Lines) obj) == 0;
    }
}
