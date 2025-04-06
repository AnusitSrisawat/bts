import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.io.*;

enum SearchCriterion {
    BEST_COST,
    BEST_NODE,
    BEST_INTERCHANGE,
    BEST_DISTANCE,
    BEST_TIME,
    BEST_ALL
}

class App {
    private final static int minPath = 1;
    private static int countNodeRunning = 0;
    private static final Map<String, List<Edge>> edgeList = new HashMap<>();
    private static final LinkedHashMap<String, Station> stationList = new LinkedHashMap<>();
    private static final Map<String, Set<String>> lineConnections = new HashMap<>();
    private static final Map<String, Integer> interchangeCache = new HashMap<>();
    private static final Map<String, Map<String, Map<String, Integer>>> fareCache = new HashMap<>();

    private static final Map<String, List<Path>> bestPathsMap = new HashMap<>();

    private static SearchCriterion searchCriterion = SearchCriterion.BEST_ALL;

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
        countNodeRunning = 0;

        dfs(start, end, new ArrayList<>(Collections.singletonList(start)), 0, 0, 0, 0, edgeQueue,
                interchangeMap);
        return bestPathsMap;
    }

    private static void dfs(String current, String end, List<String> path, int totalDistance, int totalCost,
            int totalTime, int totalInterChange, PriorityQueue<Edge> edgeQueue,
            Map<String, Integer> interchangeMap) {
        countNodeRunning++;

        if (current.equals(end)) {
            Path newPath = new Path(new ArrayList<>(path), totalDistance, totalCost, totalTime);
            newPath.totalInterChange = splitSegments(path).size();
            updateBestPaths(newPath);
            return;
        }

        List<Edge> edges = new ArrayList<>(edgeList.getOrDefault(current, new ArrayList<>()));

        edges.removeIf(edge -> path.contains(edge.target));

        edgeQueue = new PriorityQueue<>(
                Comparator.comparingInt(edge -> interchangeMap.getOrDefault(edge.target, Integer.MAX_VALUE)));
        for (Edge edge : edges) {
            interchangeMap.put(edge.target, getInterchangeCount(edge.target, end));
            if (!path.contains(edge.target)) {
                edgeQueue.add(edge);
            }
        }

        for (Edge edge : edgeQueue) {
            if (!path.contains(edge.target)) {
                path.add(edge.target);

                List<List<String>> segments = splitSegments(path);
                int newTotalCost = getPrice(path, segments);

                if (isPromisingPath(totalDistance + edge.distance, newTotalCost, totalTime + edge.time, path.size(),
                        segments.size())) {
                    dfs(edge.target, end, path, totalDistance + edge.distance, newTotalCost, totalTime + edge.time,
                            segments.size(), edgeQueue, interchangeMap);
                }
                path.remove(path.size() - 1);
            }
        }
    }

    private static void updateBestPaths(Path newPath) {
        String key = searchCriterion.name(); // Use the criterion name as the key
        bestPathsMap.compute(key, (k, existingPaths) -> {
            if (existingPaths == null) {
                existingPaths = new ArrayList<>();
            }

            existingPaths.removeIf(existingPath -> {
                switch (searchCriterion) {
                    case BEST_COST:
                        return newPath.totalCost < existingPath.totalCost;
                    case BEST_NODE:
                        return newPath.nodes.size() < existingPath.nodes.size();
                    case BEST_INTERCHANGE:
                        return newPath.totalInterChange < existingPath.totalInterChange;
                    case BEST_DISTANCE:
                        return newPath.totalDistance < existingPath.totalDistance;
                    case BEST_TIME:
                        return newPath.totalTime < existingPath.totalTime;
                    case BEST_ALL:
                        return newPath.nodes.size() < existingPath.nodes.size()
                                && newPath.totalDistance < existingPath.totalDistance
                                && newPath.totalCost < existingPath.totalCost
                                && newPath.totalTime < existingPath.totalTime
                                && newPath.totalInterChange <= existingPath.totalInterChange;
                    default:
                        return newPath.nodes.size() < existingPath.nodes.size()
                                && newPath.totalDistance < existingPath.totalDistance
                                && newPath.totalCost < existingPath.totalCost
                                && newPath.totalTime < existingPath.totalTime
                                && newPath.totalInterChange <= existingPath.totalInterChange;
                }
            });

            boolean isDuplicate = existingPaths.stream().anyMatch(existingPath -> isEqualPath(existingPath, newPath));
            if (!isDuplicate) {
                existingPaths.add(newPath); // Add the new path if it's not a duplicate
            }

            return existingPaths;
        });
    }

    private static boolean isEqualPath(Path path1, Path path2) {
        return path1.totalDistance == path2.totalDistance &&
                path1.totalCost == path2.totalCost &&
                path1.totalTime == path2.totalTime &&
                path1.totalInterChange == path2.totalInterChange &&
                path1.nodes.size() == path2.nodes.size();
    }

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
                switch (searchCriterion) {
                    case BEST_COST:
                        if (totalCost >= best.totalCost) {
                            return false;
                        }
                        break;
                    case BEST_NODE:
                        if (nodeCount >= best.nodes.size()) {
                            return false;
                        }
                        break;
                    case BEST_INTERCHANGE:
                        if (totalInterChange >= best.totalInterChange) {
                            return false;
                        }
                        break;
                    case BEST_DISTANCE:
                        if (totalDistance >= best.totalDistance) {
                            return false;
                        }
                        break;
                    case BEST_TIME:
                        if (totalTime >= best.totalTime) {
                            return false;
                        }
                        break;
                    case BEST_ALL:
                        if (totalDistance > best.totalDistance &&
                                totalCost > best.totalCost &&
                                totalTime > best.totalTime &&
                                totalInterChange > best.totalInterChange &&
                                nodeCount > best.nodes.size()) {
                            return false; // หากเส้นทางใหม่แย่กว่าทุกเงื่อนไข ให้ return false
                        }
                        break;
                    default:
                        // any other case default BEST_ALL
                        if (totalDistance > best.totalDistance &&
                                totalCost > best.totalCost &&
                                totalTime > best.totalTime &&
                                totalInterChange > best.totalInterChange &&
                                nodeCount > best.nodes.size()) {
                            return false;
                        }
                        break;
                }
            }
        }

        return true; // หากเส้นทางใหม่ดีกว่าอย่างน้อยหนึ่งเงื่อนไข ให้ถือว่า promising
    }

    public static void main(String[] args) throws Exception {
        String start;
        String end;

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
                System.out.println("");
                System.out.println("Select Search Criterion:");
                System.out.println("1. Best Cost");
                System.out.println("2. Best Node");
                System.out.println("3. Best Interchange");
                System.out.println("4. Best Distance");
                System.out.println("5. Best Time");
                System.out.println("6. Best All");
                System.out.print("Enter your choice (1-5): ");
                int choice = Integer.parseInt(s.nextLine());

                switch (choice) {
                    case 1:
                        searchCriterion = SearchCriterion.BEST_COST;
                        break;
                    case 2:
                        searchCriterion = SearchCriterion.BEST_NODE;
                        break;
                    case 3:
                        searchCriterion = SearchCriterion.BEST_INTERCHANGE;
                        break;
                    case 4:
                        searchCriterion = SearchCriterion.BEST_DISTANCE;
                        break;
                    case 5:
                        searchCriterion = SearchCriterion.BEST_TIME;
                        break;
                    case 6:
                        searchCriterion = SearchCriterion.BEST_ALL;
                        break;
                    default:
                        System.out.println("Invalid choice. Defaulting to Best All.");
                        searchCriterion = SearchCriterion.BEST_ALL;
                }

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

                for (Map.Entry<String, List<Path>> entry : kPaths.entrySet()) {
                    System.out.println("Criterion: " + entry.getKey());
                    for (Path path : entry.getValue()) {
                        System.out.println("Path: " + path.nodes);
                        System.out.println("Distance: " + path.totalDistance + ", Cost: " + path.totalCost +
                                ", Time: " + path.totalTime + ", Nodes: " + path.nodes.size() +
                                ", Interchanges: " + path.totalInterChange);
                        System.out.println("");
                    }
                }
            }
        }
    }
}