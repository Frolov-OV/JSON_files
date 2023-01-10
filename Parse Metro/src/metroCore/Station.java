package metroCore;

public class Station implements Comparable<Station>{
    private Lines lines;
    private String name;

    public Station(String name, Lines lines){
        this.name = name;
        this.lines = lines;
    }
    public Lines getLine(){
        return lines;
    }
    public String getName(){
        return name;
    }
    @Override
    public int compareTo(Station station){
        int lineComparison = lines.compareTo(station.getLine());
        if(lineComparison != 0) {
            return lineComparison;
        }
        return name.compareToIgnoreCase(station.getName());
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((Station) obj) == 0;
    }

    @Override
    public String toString(){
        return name;
    }

}
