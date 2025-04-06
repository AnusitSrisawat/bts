import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.io.*;

class App {
    private final static int minPath = 1;
    private static int countNodeRunning = 0;
    private static final Map<String, List<Edge>> edgeList = new HashMap<>();
    private static final LinkedHashMap<String, Station> stationList = new LinkedHashMap<>();
    private static final Map<String, Set<String>> lineConnections = new HashMap<>();
    private static final Map<String, Integer> interchangeCache = new HashMap<>();
    private static final Map<String, Map<String, Map<String, Integer>>> fareCache = new HashMap<>();

    // private static final TreeSet<Path> bestPathsSet = new TreeSet<>((p1, p2) -> {
    // // เปรียบเทียบตามเงื่อนไขหลายอย่าง
    // if (p1.totalDistance != p2.totalDistance) return
    // Integer.compare(p1.totalDistance, p2.totalDistance);
    // if (p1.totalCost != p2.totalCost) return Integer.compare(p1.totalCost,
    // p2.totalCost); // เปรียบเทียบตามค่าใช้จ่าย
    // if (p1.totalTime != p2.totalTime) return Integer.compare(p1.totalTime,
    // p2.totalTime); // เปรียบเทียบตามเวลา
    // if (p1.totalInterChange != p2.totalInterChange) return
    // Integer.compare(p1.totalInterChange, p2.totalInterChange); //
    // เปรียบเทียบตามจำนวน interchange
    // return Integer.compare(p1.nodes.size(), p2.nodes.size()); //
    // เปรียบเทียบตามจำนวนสถานี
    // });
    private static final Map<String, List<Path>> bestPathsMap = new HashMap<>();

    private static final String GREEN_FILE = "D:/jimmy/project/bts/bts/src/btsData - green.csv";
    private static final String BLUE_FILE = "D:/jimmy/project/bts/bts/src/btsData - blue.csv";
    private static final String GOLD_FILE = "D:/jimmy/project/bts/bts/src/btsData - gold.csv";
    private static final String PINK_FILE = "D:/jimmy/project/bts/bts/src/btsData - pink.csv";
    private static final String YELLOW_FILE = "D:/jimmy/project/bts/bts/src/btsData - yellow.csv";
    private static final String PURPLE_FILE = "D:/jimmy/project/bts/bts/src/btsData - purple.csv";

    private static void printEdgeList() {
        for (Map.Entry<String, List<Edge>> entry : edgeList.entrySet()) {
            StringBuilder sb = new StringBuilder();
            List<Edge> edges = entry.getValue();
            if (edges.isEmpty()) {
                sb.append("No edges");
            } else {
                for (Edge edge : edges) {
                    sb.append(entry.getKey()).append(" -> ");
                    sb.append(edge.target).append(" distance:").append(edge.distance).append(" cost:").append(edge.cost)
                            .append(" time:").append(edge.time).append(" ");
                }
            }
            System.out.println(sb.toString());
        }
    }

    private static void printStationList() {
        for (Map.Entry<String, Station> entry : stationList.entrySet()) {
            StringBuilder sb = new StringBuilder();
            Station station = entry.getValue();

            sb.append(station.code).append(" ").append(station.name).append(" ").append(station.line).append(" ");
            sb.append(" connect:");
            for (Station connect : station.connect) {
                sb.append(connect.code + " ");
            }
            sb.append(" \n");

            System.out.println(sb.toString());
        }
    }

    private static void printLineConnectList() {
        for (Map.Entry<String, Set<String>> entry : lineConnections.entrySet()) {
            StringBuilder sb = new StringBuilder();
            Set<String> lineConnect = entry.getValue();

            sb.append("entry key : ").append(getLinePrefix(entry.getKey()));
            sb.append(", connect : ");
            for (String connect : lineConnect) {
                sb.append(getLinePrefix(connect) + " ");
            }
            sb.append(" \n");

            System.out.println(sb.toString());
        }

        System.out.println("");
    }

    static class Station {
        String code;
        String name;
        String line;
        List<Station> connect = new ArrayList<>();

        public Station(String code, String name, String line) {
            this.code = code;
            this.name = name;
            this.line = line;
        }

        private void connect(Station station) {
            if (station != null && !this.connect.contains(station)) {
                this.connect.add(station);
                station.connect(this);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Station{name='").append(name)
                    .append("', code='").append(code)
                    .append("', connect=[");
            for (Station station : connect) {
                sb.append(station.code);
                sb.append(", ");
            }
            sb.append("'}");
            return sb.toString();
        }

    }

    private static void initStation() {

        addStation("N24", "1", "Green (Sukhumvit)", Arrays.asList("N23"));
        addStation("N23", "1", "Green (Sukhumvit)", Arrays.asList("N22", "N24"));
        addStation("N22", "1", "Green (Sukhumvit)", Arrays.asList("N21", "N23"));
        addStation("N21", "1", "Green (Sukhumvit)", Arrays.asList("N20", "N22"));
        addStation("N20", "1", "Green (Sukhumvit)", Arrays.asList("N19", "N21"));
        addStation("N19", "1", "Green (Sukhumvit)", Arrays.asList("N18", "N20"));
        addStation("N18", "1", "Green (Sukhumvit)", Arrays.asList("N17", "N19"));
        addStation("N17", "1", "Green (Sukhumvit)", Arrays.asList("N16", "N18", "PK16"));
        addStation("N16", "1", "Green (Sukhumvit)", Arrays.asList("N15", "N17"));
        addStation("N15", "1", "Green (Sukhumvit)", Arrays.asList("N14", "N16"));
        addStation("N14", "1", "Green (Sukhumvit)", Arrays.asList("N13", "N15"));
        addStation("N13", "1", "Green (Sukhumvit)", Arrays.asList("N12", "N14"));
        addStation("N12", "1", "Green (Sukhumvit)", Arrays.asList("N11", "N13"));
        addStation("N11", "1", "Green (Sukhumvit)", Arrays.asList("N10", "N12"));
        addStation("N10", "1", "Green (Sukhumvit)", Arrays.asList("N9", "N11"));
        addStation("N9", "1", "Green (Sukhumvit)", Arrays.asList("N8", "N10", "BL14"));
        addStation("N8", "1", "Green (Sukhumvit)", Arrays.asList("N7", "N9", "BL13"));
        addStation("N7", "1", "Green (Sukhumvit)", Arrays.asList("N6", "N8"));
        addStation("N6", "1", "Green (Sukhumvit)", Arrays.asList("N5", "N7"));
        addStation("N5", "1", "Green (Sukhumvit)", Arrays.asList("N4", "N6"));
        addStation("N4", "1", "Green (Sukhumvit)", Arrays.asList("N3", "N5"));
        addStation("N3", "1", "Green (Sukhumvit)", Arrays.asList("N2", "N4"));
        addStation("N2", "1", "Green (Sukhumvit)", Arrays.asList("N1", "N3"));
        addStation("N1", "1", "Green (Sukhumvit)", Arrays.asList("N2", "CEN"));

        addStation("E1", "1", "Green (Sukhumvit)", Arrays.asList("E2", "CEN"));
        addStation("E2", "1", "Green (Sukhumvit)", Arrays.asList("E1", "E3"));
        addStation("E3", "1", "Green (Sukhumvit)", Arrays.asList("E2", "E4"));
        addStation("E4", "1", "Green (Sukhumvit)", Arrays.asList("E3", "E5", "BL22"));
        addStation("E5", "1", "Green (Sukhumvit)", Arrays.asList("E4", "E6"));
        addStation("E6", "1", "Green (Sukhumvit)", Arrays.asList("E5", "E7"));
        addStation("E7", "1", "Green (Sukhumvit)", Arrays.asList("E6", "E8"));
        addStation("E8", "1", "Green (Sukhumvit)", Arrays.asList("E7", "E9"));
        addStation("E9", "1", "Green (Sukhumvit)", Arrays.asList("E8", "E10"));
        addStation("E10", "1", "Green (Sukhumvit)", Arrays.asList("E9", "E11"));
        addStation("E11", "1", "Green (Sukhumvit)", Arrays.asList("E10", "E12"));
        addStation("E12", "1", "Green (Sukhumvit)", Arrays.asList("E11", "E13"));
        addStation("E13", "1", "Green (Sukhumvit)", Arrays.asList("E12", "E14"));
        addStation("E14", "1", "Green (Sukhumvit)", Arrays.asList("E13", "E15"));
        addStation("E15", "1", "Green (Sukhumvit)", Arrays.asList("E14", "E16"));
        addStation("E16", "1", "Green (Sukhumvit)", Arrays.asList("E15", "E17"));
        addStation("E17", "1", "Green (Sukhumvit)", Arrays.asList("E16", "E18"));
        addStation("E18", "1", "Green (Sukhumvit)", Arrays.asList("E17", "E19"));
        addStation("E19", "1", "Green (Sukhumvit)", Arrays.asList("E18", "E20"));
        addStation("E20", "1", "Green (Sukhumvit)", Arrays.asList("E19", "E21"));
        addStation("E21", "1", "Green (Sukhumvit)", Arrays.asList("E20", "E22"));
        addStation("E22", "1", "Green (Sukhumvit)", Arrays.asList("E21", "E23"));
        addStation("E23", "1", "Green (Sukhumvit)", Arrays.asList("E22", "E24"));

        addStation("W1", "1", "Green (Silom)", Arrays.asList("CEN"));

        addStation("CEN", "1", "Green", Arrays.asList("W1", "S1", "E1", "N1"));

        addStation("S1", "1", "Green (Silom)", Arrays.asList("S2", "CEN"));
        addStation("S2", "1", "Green (Silom)", Arrays.asList("S1", "S3", "BL26"));
        addStation("S3", "1", "Green (Silom)", Arrays.asList("S2", "S4"));
        addStation("S4", "1", "Green (Silom)", Arrays.asList("S3", "S5"));
        addStation("S5", "1", "Green (Silom)", Arrays.asList("S4", "S6"));
        addStation("S6", "1", "Green (Silom)", Arrays.asList("S5", "S7"));
        addStation("S7", "1", "Green (Silom)", Arrays.asList("S6", "S8", "G1"));
        addStation("S8", "1", "Green (Silom)", Arrays.asList("S7", "S9"));
        addStation("S9", "1", "Green (Silom)", Arrays.asList("S8", "S10"));
        addStation("S10", "1", "Green (Silom)", Arrays.asList("S9", "S11"));
        addStation("S11", "1", "Green (Silom)", Arrays.asList("S10", "S12"));
        addStation("S12", "1", "Green (Silom)", Arrays.asList("S11", "BL34"));

        addStation("G1", "1", "Gold", Arrays.asList("G2", "S7"));
        addStation("G2", "1", "Gold", Arrays.asList("G1", "G3"));
        addStation("G3", "1", "Gold", Arrays.asList("G2"));

        addStation("BL01", "1", "MRT Blue", Arrays.asList("BL02", "BL32", "BL33"));
        addStation("BL02", "1", "MRT Blue", Arrays.asList("BL01", "E24", "BL01"));
        addStation("BL03", "1", "MRT Blue", Arrays.asList("BL02", "BL04"));
        addStation("BL04", "1", "MRT Blue", Arrays.asList("BL03", "BL05"));
        addStation("BL05", "1", "MRT Blue", Arrays.asList("BL04", "BL06"));
        addStation("BL06", "1", "MRT Blue", Arrays.asList("BL05", "BL07"));
        addStation("BL07", "1", "MRT Blue", Arrays.asList("BL06", "BL08"));
        addStation("BL08", "1", "MRT Blue", Arrays.asList("BL07", "BL09"));
        addStation("BL09", "1", "MRT Blue", Arrays.asList("BL08", "BL10"));
        addStation("BL10", "1", "MRT Blue", Arrays.asList("BL09", "BL11"));
        addStation("BL11", "1", "MRT Blue", Arrays.asList("BL10", "BL12"));
        addStation("BL12", "1", "MRT Blue", Arrays.asList("BL11", "BL13"));
        addStation("BL13", "1", "MRT Blue", Arrays.asList("BL12", "BL14", "N8"));
        addStation("BL14", "1", "MRT Blue", Arrays.asList("BL13", "BL15", "N9"));
        addStation("BL15", "1", "MRT Blue", Arrays.asList("BL14", "BL16"));
        addStation("BL16", "1", "MRT Blue", Arrays.asList("BL15", "BL17"));
        addStation("BL17", "1", "MRT Blue", Arrays.asList("BL16", "BL18"));
        addStation("BL18", "1", "MRT Blue", Arrays.asList("BL17", "BL19"));
        addStation("BL19", "1", "MRT Blue", Arrays.asList("BL18", "BL20"));
        addStation("BL20", "1", "MRT Blue", Arrays.asList("BL19", "BL21"));
        addStation("BL21", "1", "MRT Blue", Arrays.asList("BL20", "BL22"));
        addStation("BL22", "1", "MRT Blue", Arrays.asList("BL21", "BL23", "E4"));
        addStation("BL23", "1", "MRT Blue", Arrays.asList("BL22", "BL24"));
        addStation("BL24", "1", "MRT Blue", Arrays.asList("BL23", "BL25"));
        addStation("BL25", "1", "MRT Blue", Arrays.asList("BL24", "BL26"));
        addStation("BL26", "1", "MRT Blue", Arrays.asList("BL25", "BL27", "S2"));
        addStation("BL27", "1", "MRT Blue", Arrays.asList("BL26"));
        addStation("BL27", "1", "MRT Blue", Arrays.asList("BL26", "BL28"));
        addStation("BL28", "1", "MRT Blue", Arrays.asList("BL27", "BL29"));
        addStation("BL29", "1", "MRT Blue", Arrays.asList("BL28", "BL30"));
        addStation("BL30", "1", "MRT Blue", Arrays.asList("BL29", "BL31"));
        addStation("BL31", "1", "MRT Blue", Arrays.asList("BL30", "BL32"));
        addStation("BL32", "1", "MRT Blue", Arrays.asList("BL31", "BL01"));
        addStation("BL33", "1", "MRT Blue", Arrays.asList("BL34", "BL01"));
        addStation("BL34", "1", "MRT Blue", Arrays.asList("BL33", "BL35", "S12"));
        addStation("BL35", "1", "MRT Blue", Arrays.asList("BL34", "BL36"));
        addStation("BL36", "1", "MRT Blue", Arrays.asList("BL35", "BL37"));
        addStation("BL37", "1", "MRT Blue", Arrays.asList("BL36", "BL38"));
        addStation("BL38", "1", "MRT Blue", Arrays.asList("BL37"));

        addStation("PK01", "1", "MRT Pink", Arrays.asList("PK02", "PP11"));
        addStation("PK02", "1", "MRT Pink", Arrays.asList("PK01", "PK03"));
        addStation("PK03", "1", "MRT Pink", Arrays.asList("PK02", "PK04"));
        addStation("PK04", "1", "MRT Pink", Arrays.asList("PK03", "PK05"));
        addStation("PK05", "1", "MRT Pink", Arrays.asList("PK04", "PK06"));
        addStation("PK06", "1", "MRT Pink", Arrays.asList("PK05", "PK07"));
        addStation("PK07", "1", "MRT Pink", Arrays.asList("PK06", "PK08"));
        addStation("PK08", "1", "MRT Pink", Arrays.asList("PK07", "PK09"));
        addStation("PK09", "1", "MRT Pink", Arrays.asList("PK08", "PK10"));
        addStation("PK10", "1", "MRT Pink", Arrays.asList("PK09", "PK11", "MT01"));
        addStation("PK11", "1", "MRT Pink", Arrays.asList("PK10", "PK12"));
        addStation("PK12", "1", "MRT Pink", Arrays.asList("PK11", "PK13"));
        addStation("PK13", "1", "MRT Pink", Arrays.asList("PK12", "PK14"));
        addStation("PK14", "1", "MRT Pink", Arrays.asList("PK13", "PK15"));
        addStation("PK15", "1", "MRT Pink", Arrays.asList("PK14", "PK16"));
        addStation("PK16", "1", "MRT Pink", Arrays.asList("PK15", "PK17", "N17"));
        addStation("PK17", "1", "MRT Pink", Arrays.asList("PK16", "PK18"));
        addStation("PK18", "1", "MRT Pink", Arrays.asList("PK17", "PK19"));
        addStation("PK19", "1", "MRT Pink", Arrays.asList("PK18", "PK20"));
        addStation("PK20", "1", "MRT Pink", Arrays.asList("PK19", "PK21"));
        addStation("PK21", "1", "MRT Pink", Arrays.asList("PK20", "PK22"));
        addStation("PK22", "1", "MRT Pink", Arrays.asList("PK21", "PK23"));
        addStation("PK23", "1", "MRT Pink", Arrays.asList("PK22", "PK24"));
        addStation("PK24", "1", "MRT Pink", Arrays.asList("PK23", "PK25"));
        addStation("PK25", "1", "MRT Pink", Arrays.asList("PK24", "PK26"));
        addStation("PK26", "1", "MRT Pink", Arrays.asList("PK25", "PK27"));
        addStation("PK27", "1", "MRT Pink", Arrays.asList("PK26", "PK28"));
        addStation("PK28", "1", "MRT Pink", Arrays.asList("PK27", "PK29"));
        addStation("PK29", "1", "MRT Pink", Arrays.asList("PK28", "PK30"));
        addStation("PK30", "1", "MRT Pink", Arrays.asList("PK29"));

        addStation("MT01", "1", "MRT Pink", Arrays.asList("MT02", "PK10"));
        addStation("MT02", "1", "MRT Pink", Arrays.asList("MT01"));

        addStation("PP01", "1", "MRT Purple", Arrays.asList("PP02"));
        addStation("PP02", "1", "MRT Purple", Arrays.asList("PP01", "PP03"));
        addStation("PP03", "1", "MRT Purple", Arrays.asList("PP02", "PP04"));
        addStation("PP04", "1", "MRT Purple", Arrays.asList("PP03", "PP05"));
        addStation("PP05", "1", "MRT Purple", Arrays.asList("PP04", "PP06"));
        addStation("PP06", "1", "MRT Purple", Arrays.asList("PP05", "PP07"));
        addStation("PP07", "1", "MRT Purple", Arrays.asList("PP06", "PP08"));
        addStation("PP08", "1", "MRT Purple", Arrays.asList("PP07", "PP09"));
        addStation("PP09", "1", "MRT Purple", Arrays.asList("PP08", "PP10"));
        addStation("PP10", "1", "MRT Purple", Arrays.asList("PP09", "PP11"));
        addStation("PP11", "1", "MRT Purple", Arrays.asList("PP10", "PP12", "PK01"));
        addStation("PP12", "1", "MRT Purple", Arrays.asList("PP11", "PP13"));
        addStation("PP13", "1", "MRT Purple", Arrays.asList("PP12", "PP14"));
        addStation("PP14", "1", "MRT Purple", Arrays.asList("PP13", "PP15"));
        addStation("PP15", "1", "MRT Purple", Arrays.asList("PP14", "PP16"));
        addStation("PP16", "1", "MRT Purple", Arrays.asList("PP15", "BL10"));

        addStation("YL01", "1", "MRT Yellow", Arrays.asList("YL02", "BL15"));
        addStation("YL02", "1", "MRT Yellow", Arrays.asList("YL01", "YL03"));
        addStation("YL03", "1", "MRT Yellow", Arrays.asList("YL02", "YL04"));
        addStation("YL04", "1", "MRT Yellow", Arrays.asList("YL03", "YL05"));
        addStation("YL05", "1", "MRT Yellow", Arrays.asList("YL04", "YL06"));
        addStation("YL06", "1", "MRT Yellow", Arrays.asList("YL05", "YL07"));
        addStation("YL07", "1", "MRT Yellow", Arrays.asList("YL06", "YL08"));
        addStation("YL08", "1", "MRT Yellow", Arrays.asList("YL07", "YL09"));
        addStation("YL09", "1", "MRT Yellow", Arrays.asList("YL08", "YL10"));
        addStation("YL10", "1", "MRT Yellow", Arrays.asList("YL09", "YL11"));
        addStation("YL11", "1", "MRT Yellow", Arrays.asList("YL10", "YL12"));
        addStation("YL12", "1", "MRT Yellow", Arrays.asList("YL11", "YL13"));
        addStation("YL13", "1", "MRT Yellow", Arrays.asList("YL12", "YL14"));
        addStation("YL14", "1", "MRT Yellow", Arrays.asList("YL13", "YL15"));
        addStation("YL15", "1", "MRT Yellow", Arrays.asList("YL14", "YL16"));
        addStation("YL16", "1", "MRT Yellow", Arrays.asList("YL15", "YL17"));
        addStation("YL17", "1", "MRT Yellow", Arrays.asList("YL16", "YL18"));
        addStation("YL18", "1", "MRT Yellow", Arrays.asList("YL17", "YL19"));
        addStation("YL19", "1", "MRT Yellow", Arrays.asList("YL18", "YL20"));
        addStation("YL20", "1", "MRT Yellow", Arrays.asList("YL19", "YL21"));
        addStation("YL21", "1", "MRT Yellow", Arrays.asList("YL20", "YL22"));
        addStation("YL22", "1", "MRT Yellow", Arrays.asList("YL21", "YL23"));
        addStation("YL23", "1", "MRT Yellow", Arrays.asList("YL22", "E15"));

    }

    private static void addStation(String code, String name, String line, List<String> connectStationList) {
        Station newStation = new Station(code, name, line);
        String currentLine = getLinePrefix(code);

        if (connectStationList != null) {
            for (String connectStationCode : connectStationList) {
                String connectLine = getLinePrefix(connectStationCode);

                // 📌 ถ้าเป็นการเชื่อมข้ามสาย ให้บันทึก
                if (!currentLine.equals(connectLine)) {
                    lineConnections.computeIfAbsent(currentLine, k -> new HashSet<>()).add(connectLine);
                    lineConnections.computeIfAbsent(connectLine, k -> new HashSet<>()).add(currentLine);
                }

                Station connectStation = stationList.get(connectStationCode);
                if (connectStation != null) {
                    newStation.connect(connectStation);
                }
            }
        }
        stationList.put(code, newStation);

        for (Station connected : newStation.connect) {

            if (connected == null || connected.code == null) {
                continue;
            }

            edgeList.putIfAbsent(connected.code, new ArrayList<>());
            boolean exists = edgeList.get(connected.code).stream()
                    .anyMatch(e -> e.target.equals(newStation.code));

            if (!exists) {
                boolean isDifferentLine = !getLinePrefix(connected.code).equals(getLinePrefix(newStation.code));
                int additionalTime = isDifferentLine ? 2 : 0;
                addEdge(connected.code, newStation.code, 1, 0, 1 + additionalTime);
                addEdge(newStation.code, connected.code, 1, 0, 1 + additionalTime);
            }
        }
    }

    private static String getLinePrefix(String stationCode) {
        return stationCode.replaceAll("\\d", "");
    }

    static class Edge {
        String target;
        int distance;
        int cost;
        int time;

        Edge(String target, int distance, int cost, int time) {
            this.target = target;
            this.distance = distance;
            this.cost = cost;
            this.time = time;
        }
    }

    private static void addEdge(String from, String to, int distance, int cost, int time) {
        edgeList.computeIfAbsent(from, k -> new ArrayList<>()).add(new Edge(to, distance, cost, time));
    }

    static class Path {
        List<String> nodes;
        int totalDistance;
        int totalCost;
        int totalTime;
        int totalInterChange;

        Path(List<String> nodes, int totalDistance, int totalCost, int totalTime) {
            this.nodes = new ArrayList<>(nodes);
            this.totalDistance = totalDistance;
            this.totalCost = totalCost;
            this.totalTime = totalTime;
        }
    }

    private static int getPrice(List<String> path, List<List<String>> segments) {
        int totalFare = 0;

        for (List<String> segment : segments) {
            if (segment.isEmpty())
                continue;

            String filePath = getFareFile(segment.get(0));
            int segmentFare = getPriceFromFile(segment, filePath);

            if (segmentFare == -1)
                return -1; // Error in reading file
            totalFare += segmentFare;
        }

        return totalFare;
    }

    private static List<List<String>> splitSegments(List<String> path) {
        List<List<String>> segments = new ArrayList<>();
        List<String> currentSegment = new ArrayList<>();
        String currentFile = getFareFile(path.get(0));

        for (String station : path) {
            String file = getFareFile(station);
            if (!file.equals(currentFile)) {
                segments.add(new ArrayList<>(currentSegment));
                currentSegment.clear();
                currentFile = file;
            }
            currentSegment.add(station);
        }
        if (!currentSegment.isEmpty()) {
            segments.add(currentSegment);
        }

        return segments;
    }

    private static String getFareFile(String stationCode) {
        if (stationCode.startsWith("W") ||
                stationCode.startsWith("N") ||
                stationCode.startsWith("S") ||
                stationCode.startsWith("E") ||
                stationCode.startsWith("CEN")) {
            return GREEN_FILE;
        } else if (stationCode.startsWith("BL")) {
            return BLUE_FILE;
        } else if (stationCode.startsWith("PK")) {
            return PINK_FILE;
        } else if (stationCode.startsWith("G")) {
            return GOLD_FILE;
        } else if (stationCode.startsWith("PP")) {
            return PURPLE_FILE;
        } else if (stationCode.startsWith("YL")) {
            return YELLOW_FILE;
        } else {
            return GREEN_FILE;
        }
    }

    // private static int getPriceFromFile(List<String> segment, String filePath) {
    // Map<String, Integer> priceList = new HashMap<>();
    // try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
    // String line;
    // boolean isFirstRow = true;

    // while ((line = br.readLine()) != null) {
    // String[] data = line.split(",");

    // if (isFirstRow) {
    // for (int i = 1; i < data.length; i++) {
    // priceList.put(data[i].trim(), i);
    // }
    // isFirstRow = false;
    // } else {
    // String stationName = data[0].trim();
    // if (stationName.equals(segment.get(0))) {
    // String endStation = segment.get(segment.size() - 1);
    // if (priceList.containsKey(endStation)) {
    // int colIndex = priceList.get(endStation);
    // if (colIndex < data.length) {
    // return Integer.parseInt(data[colIndex].trim());
    // }
    // }
    // }
    // }
    // }
    // } catch (IOException | NumberFormatException e) {
    // e.printStackTrace();
    // }
    // return -1;
    // }

    private static void loadFareFiles() {
        String[] files = { GREEN_FILE, BLUE_FILE, GOLD_FILE, PINK_FILE, YELLOW_FILE, PURPLE_FILE };
        for (String file : files) {
            Map<String, Map<String, Integer>> stationFareMap = new HashMap<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                boolean isFirstRow = true;
                List<String> headers = new ArrayList<>();

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (isFirstRow) {
                        headers.addAll(Arrays.asList(data).subList(1, data.length));
                        isFirstRow = false;
                    } else {
                        String stationName = data[0].trim();
                        Map<String, Integer> fareMap = new HashMap<>();
                        for (int i = 1; i < data.length; i++) {
                            fareMap.put(headers.get(i - 1), Integer.parseInt(data[i].trim()));
                        }
                        stationFareMap.put(stationName, fareMap);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
            fareCache.put(file, stationFareMap);
        }
    }

    private static int getPriceFromFile(List<String> segment, String filePath) {
        Map<String, Map<String, Integer>> stationFareMap = fareCache.get(filePath);
        if (stationFareMap == null)
            return -1;

        String startStation = segment.get(0);
        String endStation = segment.get(segment.size() - 1);

        Map<String, Integer> fareMap = stationFareMap.get(startStation);
        if (fareMap == null || !fareMap.containsKey(endStation))
            return -1;

        return fareMap.get(endStation);
    }

    private static int getInterchangeCount(String target, String nextStation) {
        if (target.isEmpty())
            return 0;

        // สร้าง key สำหรับแคช
        String cacheKey = target + "-" + nextStation;
        if (interchangeCache.containsKey(cacheKey)) {
            return interchangeCache.get(cacheKey); // ดึงค่าจากแคชถ้ามี
        }

        String currentLine = getLinePrefix(target);
        String nextLine = getLinePrefix(nextStation);

        if (currentLine.equals(nextLine)) {
            interchangeCache.put(cacheKey, 0); // อยู่ในสายเดียวกัน ไม่ต้องเปลี่ยน
            return 0;
        }

        Set<String> visited = new HashSet<>();
        int minInterchange = findInterchangeRecursive(currentLine, nextLine, visited,
                isSameGroup(currentLine, nextLine));

        interchangeCache.put(cacheKey, minInterchange); // เก็บผลลัพธ์ลงแคช
        return minInterchange;
    }

    // private static int getInterchangeCount(String target, String nextStation) {
    // // System.out.println("getInterchangeCount : " + target + ", nextStation : "
    // + nextStation);
    // if (target.isEmpty()) return 0;

    // // String currentStation = path.get(path.size() - 1);
    // String currentLine = getLinePrefix(target);
    // String nextLine = getLinePrefix(nextStation);
    // // System.out.println("currentLine : " + target + " ... " + currentLine + ",
    // nextLine : " + nextLine);

    // if (currentLine.equals(nextLine)) {
    // return 0; // อยู่ในสายเดียวกันหรือกลุ่มเดียวกันไม่ต้องเปลี่ยน
    // }

    // Set<String> visited = new HashSet<>();
    // int minInterchange = findInterchangeRecursive(currentLine, nextLine, visited,
    // isSameGroup(currentLine, nextLine));
    // // System.out.println(" :: minInterchange : " + minInterchange);
    // return minInterchange;
    // }

    private static int findInterchangeRecursive(String currentLine, String targetLine, Set<String> visited,
            boolean sameGroup) {
        if (currentLine.equals(targetLine)) {
            return 0; // ถึงสายที่ต้องการแล้ว
        }

        visited.add(currentLine);
        int minInterchange = Integer.MAX_VALUE;

        Set<String> connectedLines = new HashSet<>(lineConnections.getOrDefault(currentLine, new HashSet<>()));
        connectedLines.remove(currentLine); // เอาสายปัจจุบันออกจากเซ็ตของสายที่เชื่อมต่อ

        for (String connectedLine : connectedLines) {
            if (!visited.contains(connectedLine)) {
                int count = findInterchangeRecursive(connectedLine, targetLine, visited,
                        isSameGroup(connectedLine, targetLine));
                if (count != Integer.MAX_VALUE) {
                    minInterchange = Math.min(minInterchange, count + (sameGroup ? 0 : 1)); // +1 for the interchange
                }
            }
        }

        visited.remove(currentLine);
        return minInterchange;
    }

    private static boolean isSameGroup(String line1, String line2) {
        Set<String> greenGroup = new HashSet<>(Arrays.asList("W", "N", "S", "E", "CEN"));
        return greenGroup.contains(line1) && greenGroup.contains(line2);
    }

    private static Map<String, List<Path>> dfsBestPaths(String start, String end) {

        Map<String, Integer> interchangeMap = new HashMap<>();
        PriorityQueue<Edge> edgeQueue = new PriorityQueue<>(
                Comparator.comparingInt(edge -> interchangeMap.getOrDefault(edge.target, Integer.MAX_VALUE)));
        // List<Path> bestPaths = new ArrayList<>();
        countNodeRunning = 0;

        dfs(start, end, new ArrayList<>(Collections.singletonList(start)), 0, 0, 0, 0, edgeQueue,
                interchangeMap);
        return bestPathsMap;
    }

    private static void dfs(String current, String end, List<String> path, int totalDistance, int totalCost,
            int totalTime, int totalInterChange, PriorityQueue<Edge> edgeQueue,
            Map<String, Integer> interchangeMap) {
        // System.out.println("current : " + current + ", totalDistance : " +
        // totalDistance + ", totalCost : " + totalCost + ", totalTime : " + totalTime +
        // ", totalInterChange : " + totalInterChange + ", nodes : " + path.size());
        // try {
        // Thread.sleep(100); // หน่วงเวลา 100 มิลลิวินาที
        // } catch (InterruptedException e) {
        // Thread.currentThread().interrupt();
        // }

        // if (!isPromisingPath(totalDistance, totalCost, totalTime, path.size(),
        // totalInterChange, bestPaths)) {
        // return;
        // }
        countNodeRunning++;

        if (current.equals(end)) {
            Path newPath = new Path(new ArrayList<>(path), totalDistance, totalCost, totalTime);
            newPath.totalInterChange = splitSegments(path).size();
            // bestPaths.add(newPath);
            updateBestPaths(newPath);
            // System.out.println("newPath: ");
            // Graph.Path node = newPath;
            // System.out.println("node : " + node.nodes);
            // System.out.println(", Distance: " + node.totalDistance + ", Cost: " +
            // node.totalCost + ", Time: " + node.totalTime + ", Nodes: " +
            // node.nodes.size());
            // System.out.println("");
            return;
        }

        ///////////////////////////
        // List<Edge> edges = new ArrayList<>(edgeList.getOrDefault(current, new
        /////////////////////////// ArrayList<>()));
        // // edgeQueue.clear();
        // interchangeMap.clear();

        // // PriorityQueue<Edge> edgeQueue = new
        // PriorityQueue<>(Comparator.comparingInt(edge ->
        // interchangeMap.getOrDefault(edge.target, Integer.MAX_VALUE)));

        // for (Edge edge : edges) {
        // // if (!path.contains(edge.target)) {
        // interchangeMap.put(edge.target, getInterchangeCount(edge.target, end));
        // edgeQueue.add(edge);
        // // }
        // }

        // while (!edgeQueue.isEmpty()) {
        // Edge edge = edgeQueue.poll();
        // path.add(edge.target);

        // List<List<String>> segments = splitSegments(path);
        // int newTotalCost = getPrice(path, segments);

        // if (isPromisingPath(totalDistance + edge.distance, newTotalCost, totalTime +
        // edge.time, path.size(), segments.size(), bestPaths)) {
        // dfs(edge.target, end, path, totalDistance + edge.distance, newTotalCost,
        // totalTime + edge.time, segments.size(), bestPaths, interchangeMap);
        // }
        // path.remove(path.size() - 1);
        // }

        ////////////////////////////
        List<Edge> edges = new ArrayList<>(edgeList.getOrDefault(current, new ArrayList<>()));

        // if (bestPaths.size() <= 1) {
        // System.out.print(countNodeRunning + " : ");
        // System.out.print("path : " + path + ", ");
        // System.out.print("current : " + current + ", ");
        // System.out.print("edges : " + edges.stream().map(edge ->
        // edge.target).collect(Collectors.toList()) + ", ");
        // }

        // ลบเส้นทางที่เคยผ่านมาแล้ว
        edges.removeIf(edge -> path.contains(edge.target));

        // เรียงลำดับเส้นทางจากจำนวน Interchange ที่ต้องเปลี่ยนมากไปน้อย
        // Map<String, Integer> interchangeMap = new HashMap<>();

        // interchangeMap.clear();
        // edges.sort(Comparator.comparingInt(edge ->
        // interchangeMap.getOrDefault(edge.target, Integer.MAX_VALUE)));
        // edgeQueue.clear();
        edgeQueue = new PriorityQueue<>(
                Comparator.comparingInt(edge -> interchangeMap.getOrDefault(edge.target, Integer.MAX_VALUE)));
        for (Edge edge : edges) {
            interchangeMap.put(edge.target, getInterchangeCount(edge.target, end));
            if (!path.contains(edge.target)) {
                edgeQueue.add(edge);
            }
        }
        // if (bestPaths.size() <= 1) {
        // System.out.print(" interchangeMap : " + interchangeMap + ", ");
        // }

        // if (bestPaths.size() <= 1) {
        // System.out
        // .print("edges after : " + edgeQueue.stream().map(edge ->
        // edge.target).collect(Collectors.toList()));
        // System.out.println("");
        // }

        for (Edge edge : edgeQueue) {
            // System.out.println("edge : " + edge.target);
            if (!path.contains(edge.target)) {
                // System.out.println("edge : " + edge.target);
                path.add(edge.target);

                List<List<String>> segments = splitSegments(path);
                int newTotalCost = getPrice(path, segments);
                // System.out.print("Path : ");
                // for (int i = 0; i < path.size(); i++) {
                // System.out.print(path.get(i));
                // System.out.print(", ");
                // }
                // System.out.println("newTotalCost : " + newTotalCost);
                // System.out.println("");

                if (isPromisingPath(totalDistance + edge.distance, newTotalCost, totalTime + edge.time, path.size(),
                        segments.size())) {
                    dfs(edge.target, end, path, totalDistance + edge.distance, newTotalCost, totalTime + edge.time,
                            segments.size(), edgeQueue, interchangeMap);
                }
                path.remove(path.size() - 1);
            }
        }
    }

    // private static void updateBestPaths(Path newPath) {
    // // Update the best path for distance
    // updateBestPath("bestDistance", newPath, (existingPath, newP) ->
    // newP.totalDistance <= existingPath.totalDistance);

    // // Update the best path for cost
    // updateBestPath("bestCost", newPath, (existingPath, newP) -> newP.totalCost <=
    // existingPath.totalCost);

    // // Update the best path for time
    // updateBestPath("bestTime", newPath, (existingPath, newP) -> newP.totalTime <=
    // existingPath.totalTime);

    // // Update the best path for interchanges
    // updateBestPath("bestInterchange", newPath, (existingPath, newP) ->
    // newP.totalInterChange <= existingPath.totalInterChange);

    // // Update the best path for the number of nodes
    // updateBestPath("bestNode", newPath, (existingPath, newP) -> newP.nodes.size()
    // <= existingPath.nodes.size());
    // }

    // // Helper method to update a specific best path condition
    // private static void updateBestPath(String key, Path newPath,
    // BiPredicate<Path, Path> condition) {
    // bestPathsMap.compute(key, (k, existingPaths) -> {
    // if (existingPaths == null) {
    // existingPaths = new ArrayList<>();
    // }

    // // Check if the new path is already in the list
    // boolean isDuplicate = existingPaths.stream().anyMatch(existingPath ->
    // isEqualPath(existingPath, newPath));
    // if (isDuplicate) {
    // return existingPaths; // If the new path is already in the list, do nothing
    // }

    // // Remove paths that are strictly worse than the new path
    // existingPaths.removeIf(existingPath -> condition.test(newPath,
    // existingPath));

    // // Add the new path
    // existingPaths.add(newPath);

    // return existingPaths;
    // });
    // }
    // private static void updateBestPaths(Path newPath) {
    // // Update the best path for distance
    // bestPathsMap.compute("bestDistance", (key, existingPath) -> {
    // if (existingPath == null || newPath.totalDistance <
    // existingPath.totalDistance) {
    // return newPath;
    // }
    // return existingPath;
    // });

    // // Update the best path for cost
    // bestPathsMap.compute("bestCost", (key, existingPath) -> {
    // if (existingPath == null || newPath.totalCost < existingPath.totalCost) {
    // return newPath;
    // }
    // return existingPath;
    // });

    // // Update the best path for time
    // bestPathsMap.compute("bestTime", (key, existingPath) -> {
    // if (existingPath == null || newPath.totalTime < existingPath.totalTime) {
    // return newPath;
    // }
    // return existingPath;
    // });

    // // Update the best path for interchanges
    // bestPathsMap.compute("bestInterchange", (key, existingPath) -> { // Fixed
    // typo
    // if (existingPath == null || newPath.totalInterChange <
    // existingPath.totalInterChange) {
    // return newPath;
    // }
    // return existingPath;
    // });

    // // Update the best path for the number of nodes
    // bestPathsMap.compute("bestNode", (key, existingPath) -> {
    // if (existingPath == null || newPath.nodes.size() < existingPath.nodes.size())
    // {
    // return newPath;
    // }
    // return existingPath;
    // });

    // // Update the best paths for all conditions
    // bestPathsMap.compute("bestall", (key, existingPaths) -> {
    // if (existingPaths == null) {
    // existingPaths = new ArrayList<>();
    // }

    // // Check if the new path is already in the list
    // boolean isDuplicate = existingPaths.stream().anyMatch(existingPath ->
    // isEqualPath(existingPath, newPath));
    // if (!isDuplicate) {
    // // Add the new path if it meets any of the conditions
    // if (existingPaths.isEmpty() ||
    // newPath.nodes.size() <= existingPaths.get(0).nodes.size() ||
    // newPath.totalDistance <= existingPaths.get(0).totalDistance ||
    // newPath.totalCost <= existingPaths.get(0).totalCost ||
    // newPath.totalTime <= existingPaths.get(0).totalTime ||
    // newPath.totalInterChange <= existingPaths.get(0).totalInterChange) {
    // existingPaths.add(newPath);
    // }
    // }

    // return existingPaths;
    // });
    // }

    private static void updateBestPaths(Path newPath) {
        // System.out.println("bestPathsMap bestall : " + bestPathsMap.get("bestall"));

        updateSingleBest("bestDistance", newPath, Comparator.comparingDouble(a -> a.totalDistance));
        updateSingleBest("bestCost", newPath, Comparator.comparingDouble(a -> a.totalCost));
        updateSingleBest("bestTime", newPath, Comparator.comparingDouble(a -> a.totalTime));
        updateSingleBest("bestInterchange", newPath, Comparator.comparingInt(a -> a.totalInterChange));
        updateSingleBest("bestNode", newPath, Comparator.comparingInt(a -> a.nodes.size()));

        // Update bestall
        bestPathsMap.compute("bestall", (key, existingPaths) -> {
            if (existingPaths == null) {
                existingPaths = new ArrayList<>();
            }

            boolean isDuplicate = existingPaths.stream().anyMatch(existingPath -> isEqualPath(existingPath, newPath));
            if (isDuplicate) {
                return existingPaths;
            }

            List<Path> updatedPaths = existingPaths.stream()
                    .filter(existingPath -> newPath.nodes.size() <= existingPath.nodes.size() ||
                            newPath.totalDistance <= existingPath.totalDistance ||
                            newPath.totalCost <= existingPath.totalCost ||
                            newPath.totalTime <= existingPath.totalTime ||
                            newPath.totalInterChange <= existingPath.totalInterChange)
                    .collect(Collectors.toList());

            // System.out.println("updatedPaths : " + updatedPaths);
            updatedPaths.add(newPath);
            // System.out.println("updatedPaths : " + updatedPaths);
            return updatedPaths;
        });
        // System.out.println("bestPathsMap bestall : " + bestPathsMap.get("bestall"));
    }

    private static void updateSingleBest(String key, Path newPath, Comparator<Path> comparator) {
        bestPathsMap.compute(key, (k, existingPaths) -> {
            if (existingPaths == null || existingPaths.isEmpty()) {
                return new ArrayList<>(Collections.singletonList(newPath));
            }

            Path sample = existingPaths.get(0);
            int compare = comparator.compare(newPath, sample);

            if (compare < 0) {
                // newPath ดีกว่า → ล้างของเก่าแล้วเก็บ newPath อย่างเดียว
                return new ArrayList<>(Collections.singletonList(newPath));
            } else if (compare == 0) {
                // newPath เท่ากับ → ถ้าไม่ซ้ำก็เพิ่มเข้าไป
                boolean isDuplicate = existingPaths.stream()
                        .anyMatch(existingPath -> isEqualPath(existingPath, newPath));
                if (!isDuplicate) {
                    existingPaths.add(newPath);
                }
            }

            // ถ้ามากกว่า → ไม่เพิ่ม
            return existingPaths;
        });
    }

    // private static void updateSingleBest(String key, Path newPath,
    // Comparator<Path> comparator) {
    // bestPathsMap.compute(key, (k, existingPaths) -> {
    // if (existingPaths == null || existingPaths.isEmpty()) {
    // List<Path> list = new ArrayList<>();
    // list.add(newPath);
    // return list;
    // }

    // Path existing = existingPaths.get(0);
    // if (comparator.compare(newPath, existing) < 0) {
    // return new ArrayList<>(Collections.singletonList(newPath));
    // }

    // return existingPaths;
    // });
    // }

    // private static void updateBestPaths(Path newPath) {
    // // ตรวจสอบและอัปเดตเส้นทางที่ดีที่สุดสำหรับระยะทาง (distance)
    // bestPathsMap.compute("bestDistance", (key, existingPath) -> {

    // if (existingPath == null || newPath.totalDistance <=
    // existingPath.totalDistance) {
    // return newPath;
    // }
    // return existingPath;
    // });

    // // ตรวจสอบและอัปเดตเส้นทางที่ดีที่สุดสำหรับค่าใช้จ่าย (cost)
    // bestPathsMap.compute("bestCost", (key, existingPath) -> {
    // if (existingPath == null || newPath.totalCost <= existingPath.totalCost) {
    // return newPath;
    // }
    // return existingPath;
    // });

    // // ตรวจสอบและอัปเดตเส้นทางที่ดีที่สุดสำหรับเวลา (time)
    // bestPathsMap.compute("bestTime", (key, existingPath) -> {
    // if (existingPath == null || newPath.totalTime <= existingPath.totalTime) {
    // return newPath;
    // }
    // return existingPath;
    // });

    // // ตรวจสอบและอัปเดตเส้นทางที่ดีที่สุดสำหรับสถานีเชื่อม (Interchange)
    // bestPathsMap.compute("bestInterchange)", (key, existingPath) -> {
    // if (existingPath == null || newPath.totalInterChange <=
    // existingPath.totalInterChange) {
    // return newPath;
    // }
    // return existingPath;
    // });

    // // ตรวจสอบและอัปเดตเส้นทางที่ดีที่สุดสำหรับจำนวนสถานี (nodes)
    // bestPathsMap.compute("bestNode", (key, existingPath) -> {
    // if (existingPath == null || newPath.nodes.size() <=
    // existingPath.nodes.size()) {
    // return newPath;
    // }
    // return existingPath;
    // });

    // // ตรวจสอบและอัปเดตเส้นทางที่ดีที่สุดสำหรับทุกเงื่อนไข (all)
    // bestPathsMap.compute("bestall", (key, existingPaths) -> {
    // if (existingPaths == null) {
    // existingPaths = new ArrayList<>();
    // }

    // // Check if the new path is already in the list
    // boolean isDuplicate = existingPaths.stream().anyMatch(existingPath ->
    // isEqualPath(existingPath, newPath));
    // if (!isDuplicate) {
    // // Add the new path if it meets the condition
    // if (existingPaths.isEmpty() ||
    // newPath.nodes.size() <= existingPaths.get(0).nodes.size() ||
    // newPath.totalDistance <= existingPaths.get(0).totalDistance ||
    // newPath.totalCost <= existingPaths.get(0).totalCost ||
    // newPath.totalTime <= existingPaths.get(0).totalTime ||
    // newPath.totalInterChange <= existingPaths.get(0).totalInterChange) {
    // existingPaths.add(newPath);
    // }
    // }

    // return existingPaths;
    // });
    // }

    // private static void updateBestPaths(Path newPath) {
    // // เพิ่มเส้นทางใหม่ลงใน TreeSet
    // bestPathsSet.add(newPath);

    // // จำกัดจำนวนเส้นทางที่ดีที่สุดไว้ที่ `minPath`
    // if (bestPathsSet.size() > minPath) {
    // // Path worstPath = bestPathsSet.pollLast(); //
    // ลบเส้นทางที่แย่ที่สุดออกชั่วคราว

    // // ตรวจสอบว่าเส้นทางที่แย่ที่สุดเท่ากับเส้นทางใหม่หรือไม่
    // // if (isEqualPath(worstPath, newPath)) {
    // // bestPathsSet.add(worstPath); // ถ้าไม่เท่ากัน ให้เพิ่มกลับเข้าไป
    // // }
    // }
    // }

    private static boolean isEqualPath(Path path1, Path path2) {
        return path1.totalDistance == path2.totalDistance &&
                path1.totalCost == path2.totalCost &&
                path1.totalTime == path2.totalTime &&
                path1.totalInterChange == path2.totalInterChange &&
                path1.nodes.size() == path2.nodes.size();
    }

    // private static void updateBestPaths(Path newPath, List<Path> bestPaths) {
    // // List to store paths that are strictly worse in all aspects
    // List<Path> pathsToRemove = new ArrayList<>();
    // // boolean isWorse = false;

    // // if (bestPaths.size() > minPath) {
    // // System.out.println("add path : " + newPath.totalCost + ", " +
    // // newPath.totalTime + ", " + newPath.totalInterChange );
    // // bestPaths.add(newPath); // Always add the new path first
    // // }
    // // Compare each existing path with the new path
    // for (Path existing : bestPaths) {
    // if (isWorsePath(existing, newPath)) {
    // // System.out.println("remove path : " + existing.totalCost + ", " +
    // // existing.totalTime + ", " + existing.totalInterChange );
    // pathsToRemove.add(existing);
    // // isWorse = true; // Mark that we found a worse path
    // } else {
    // // Check if the new path is worse than the existing path
    // if (isWorsePath(newPath, existing)) {
    // pathsToRemove.add(newPath); // Mark the new path for removal
    // break; // No need to check further, we found a worse path
    // }
    // }
    // }
    // // Remove strictly worse paths, but ensure at least one path remains
    // if (pathsToRemove.size() > 0) {
    // bestPaths.removeAll(pathsToRemove);
    // }
    // // System.out.println("bestPaths after remove : " + bestPaths.size());
    // // for (Path path : bestPaths) {
    // // System.out.println("Best Path " + bestPaths.indexOf(path) + ": " +
    // "Distance:
    // // " + path.totalDistance + ", Cost: " + path.totalCost + ", Time: " +
    // // path.totalTime + ", Interchanges: " + path.totalInterChange + ", Nodes: "
    // +
    // // path.nodes.size());
    // // }
    // }

    private static boolean isWorsePath(Path existing, Path newPath) {
        // System.out.print("totalCost" + " : " + existing.totalCost + " > " +
        // newPath.totalCost + ", totalTime : " + existing.totalTime + " > " +
        // newPath.totalTime + ", totalInterChange : " + existing.totalInterChange + " >
        // " + newPath.totalInterChange + ", nodes : " + existing.nodes.size() + " > " +
        // newPath.nodes.size());
        boolean result =
                // existing.totalDistance >= newPath.totalDistance &&
                existing.totalCost >= newPath.totalCost &&
                        existing.totalTime >= newPath.totalTime &&
                        existing.totalInterChange >= newPath.totalInterChange &&
                        existing.nodes.size() > newPath.nodes.size()
        // ||
        // !(
        // // existing.totalDistance == newPath.totalDistance &&
        // existing.totalCost == newPath.totalCost &&
        // existing.totalTime == newPath.totalTime &&
        // existing.totalInterChange == newPath.totalInterChange &&
        // existing.nodes.size() == newPath.nodes.size()
        // )
        ;
        // System.out.println(" :: result : " + result);
        return result;
    }

    // private static boolean isPromisingPath(int totalDistance, int totalCost, int
    // totalTime, int nodeCount, int totalInterChange, List<Path> bestPaths) {
    // if (bestPaths.size() < minPath)
    // return true;

    // for (Path best : bestPaths) {
    // if (totalDistance < best.totalDistance ||
    // totalCost < best.totalCost ||
    // totalTime < best.totalTime ||
    // totalInterChange < best.totalInterChange ||
    // nodeCount < best.nodes.size() ||
    // (totalDistance == best.totalDistance && totalCost == best.totalCost &&
    // totalTime == best.totalTime && totalInterChange == best.totalInterChange &&
    // nodeCount == best.nodes.size())) {
    // return true;
    // }
    // }
    // return false;
    // }
    // private static boolean isPromisingPath(int totalDistance, int totalCost, int
    // totalTime, int nodeCount,
    // int totalInterChange) {
    // if (bestPathsSet.size() < minPath)
    // return true;

    // for (Path best : bestPathsSet) {
    // if (totalDistance >= best.totalDistance &&
    // totalCost >= best.totalCost &&
    // totalTime >= best.totalTime &&
    // totalInterChange >= best.totalInterChange &&
    // nodeCount >= best.nodes.size()) {
    // return false; // Early exit if the path is worse in all aspects
    // }
    // }
    // return true;
    // }

    private static boolean isPromisingPath(int totalDistance, int totalCost, int totalTime, int nodeCount,
            int totalInterChange) {
        // หากยังไม่มีเส้นทางใน bestPathsMap หรือจำนวนเส้นทางน้อยกว่า minPath ให้ถือว่า
        // promising
        if (bestPathsMap.isEmpty() || bestPathsMap.size() < minPath) {
            return true;
        }

        // ตรวจสอบว่าเส้นทางใหม่แย่กว่าทุกเส้นทางใน bestPathsMap หรือไม่
        for (List<Path> bestPaths : bestPathsMap.values()) {
            for (Path best : bestPaths) {
                if (totalDistance >= best.totalDistance &&
                        totalCost >= best.totalCost &&
                        totalTime >= best.totalTime &&
                        totalInterChange >= best.totalInterChange &&
                        nodeCount >= best.nodes.size()) {
                    return false; // หากเส้นทางใหม่แย่กว่าทุกเงื่อนไข ให้ return false
                }
            }
        }

        return true; // หากเส้นทางใหม่ดีกว่าอย่างน้อยหนึ่งเงื่อนไข ให้ถือว่า promising
    }

    public static void main(String[] args) throws Exception {
        String start = "";
        String end = "";

        initStation();
        loadFareFiles();

        // System.out.println("------------------");
        // printStationList();
        // System.out.println("------------------");
        // printEdgeList();
        // System.out.println("------------------");
        // printLineConnectList();
        // System.out.println("------------------");

        try (Scanner s = new Scanner(System.in)) {
            while (true) {
                System.out.println("");
                System.out.print("Select Origin: ");
                start = s.nextLine();
                if (start.trim().isEmpty())
                    break;
                System.out.print("Select Destination: ");
                end = s.nextLine();
                if (end.trim().isEmpty())
                    break;

                System.out.println("");
                System.out.print("start : " + start);
                System.out.println(" => " + end);

                long startTime = System.nanoTime();

                Runtime runtime = Runtime.getRuntime();
                runtime.gc(); // เรียก Garbage Collector เพื่อล้างหน่วยความจำที่ไม่ใช้

                Map<String, List<Path>> kPaths = dfsBestPaths(start, end);

                long endTime = System.nanoTime();

                long usedMemory = runtime.totalMemory() - runtime.freeMemory();

                System.out.println("");
                System.out.println("count Node Running : " + countNodeRunning);
                System.out.println("Execution Time: " + (endTime - startTime) / 1_000_000 + " ms");
                System.out.println("Used Memory: " + usedMemory / 1024 / 1024 + " MB");
                System.out.println("");
                System.out.println("Result Paths:");
                System.out.println("------------------------------------------------------------------------------");

                // for (Map.Entry<String, List<Path>> entry : bestPathsMap.entrySet()) {
                //     String key = entry.getKey();
                //     List<Path> paths = entry.getValue();
                //     for (Path path : paths) {
                //         System.out.println("Path " + key + "\n: " + path.nodes);
                //         System.out
                //                 .println(": Distance: " + path.totalDistance + ", Cost: " + path.totalCost + ", Time: "
                //                         + path.totalTime + ", Nodes: " + path.nodes.size() + ", Intechanges: "
                //                         + path.totalInterChange + "\n");
                //     }
                //     System.out
                //             .println("------------------------------------------------------------------------------");
                //     System.out.println("");
                // }
                for (Map.Entry<String, List<Path>> entry : bestPathsMap.entrySet()) {
                    String key = entry.getKey();
                    List<Path> paths = entry.getValue();
                    
                    // Print section header for each key
                    System.out.println("-------------------------------------------------");
                    System.out.println("Key: " + key);
                    System.out.println("-------------------------------------------------");
                
                    // Loop through each path in the list of paths for this key
                    for (Path path : paths) {
                        System.out.println("Path:");
                        // Print nodes in a readable format
                        System.out.println("  Nodes: " + path.nodes);
                
                        // Pretty print with formatting for numbers (two decimal places for distance, cost, and time)
                        System.out.println("  Distance: "+path.totalDistance+", Cost: "+path.totalCost+", Time: "+path.totalTime+", Nodes Count: "+path.nodes.size()+", Interchanges: "+path.totalInterChange);

                        
                        System.out.println("-------------------------------------------------");
                    }
                
                    System.out.println(); // Blank line for separation
                }
                
                

                // int i = 1;
                // for (Path path : kPaths) {
                // System.out.println("Path " + i + ": " + path.nodes);
                // System.out.println(": Distance: " + path.totalDistance + ", Cost: " +
                // path.totalCost + ", Time: "
                // + path.totalTime + ", Nodes: " + path.nodes.size() + ", Intechanges: "
                // + path.totalInterChange);
                // System.out
                // .println("------------------------------------------------------------------------------");
                // System.out.println("");
                // i++;
                // }
            }
        }
    }
}