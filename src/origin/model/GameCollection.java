package origin.model;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
    A collection of game data that can be filtered, searched, and sorted
 */
public class GameCollection {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("M/d/yyyy");
    private ArrayList<ArrayList<String>> parseCSV(File gamesFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(gamesFile));
        String line = reader.readLine();  //read past header line
        int numCols = line.split(",").length;
        ArrayList<ArrayList<String>> csvRows = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            boolean inQuotes = false;
            String entry = "";
            ArrayList<String> entries = new ArrayList<>();
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                boolean lastCharBackSlash = (i > 0 && line.charAt(i - 1) == '\\');
                if (!lastCharBackSlash) {
                    if (c == '\"') {
                        inQuotes = !inQuotes;
                    } else if (c == ',' && !inQuotes) {
                        entries.add(entry);
                        entry = "";
                    } else {
                        entry += c;
                    }
                } else {
                    entry += c;
                }
            }
            entries.add(entry);
            if (entries.size() == numCols) {
                csvRows.add(entries);
            } else {
                System.out.println("# of cols did not match in Game CSV");
            }
        }
        return csvRows;
    }

    public GameData parseGameFromList(List<String> data) throws ParseException {
        GameData gameData = new GameData();
        gameData.title = data.get(0);
        gameData.description = data.get(1);
        gameData.numSales = Integer.parseInt(data.get(2));
        gameData.datePublished = DATE_FORMAT.parse(data.get(3));
        gameData.owned = (data.get(4).toLowerCase() == "true");
        gameData.price = Float.parseFloat(data.get(5));
        gameData.salesPrice = (data.get(6).length() > 0)? Float.parseFloat(data.get(6)): -1;
        gameData.largeImgUri = (data.get(7).length() > 0)? data.get(7): null;
        gameData.vertImgUri = (data.get(8).length() > 0)? data.get(8): null;
        gameData.horzImgUri = (data.get(9).length() > 0)? data.get(9): null;
        gameData.gifUri = (data.get(10).length() > 0)? data.get(10): null;
        gameData.color = (data.get(11).length() > 0)? data.get(11): null;
        gameData.categories = new HashSet<>(Arrays.asList(data.get(12).split(",")));
        gameData.filters = new HashSet<>(Arrays.asList(data.get(13).split(",")));
        gameData.saleGroup = (data.get(14).length() > 0)? data.get(14): null;
        return gameData;
    }

    public List<GameData> games;

    public GameCollection(File gamesFile) throws IOException, ParseException {
        ArrayList<ArrayList<String>> csvData = parseCSV(gamesFile);
        games = new ArrayList<>();
        for (ArrayList<String> row: csvData) {
            games.add(parseGameFromList(row));
        }
        System.out.println("Loaded: " + games.size() + " games");
    }

    public GameCollection(List<GameData> games) {
        this.games = games;
    }

    public GameCollection getTitlesContainingString(String str) {
        ArrayList<GameData> matchingGames = new ArrayList<>();
        for (GameData game: games) {
            if (game.title.contains(str)) {
                matchingGames.add(game);
            }
        }
        return new GameCollection(matchingGames);
    }

    public GameCollection getMatchingGames(List<String> categories, List<String> filters) {
        ArrayList<GameData> matchingGames = new ArrayList<>();
        gameLoop: for (GameData game: games) {
            //Game must match all filters
            if (game.filters.containsAll(filters)) {
                //Game must match at least 1 category, if categories specified
                if (!categories.isEmpty()) {
                    for (String category : categories) {
                        if (game.categories.contains(category)) {
                            matchingGames.add(game);
                            continue gameLoop;
                        }
                    }
                } else {
                    matchingGames.add(game);
                    continue gameLoop;
                }
            }
        }
        return new GameCollection(matchingGames);
    }

    public List<GameData> sortPopular() {
        List<GameData> sortedGames = new ArrayList<>(games);
        sortedGames.sort(Comparator.comparingInt(g -> g.numSales));
        return sortedGames;
    }

    public List<GameData> sortRecent() {
        List<GameData> sortedGames = new ArrayList<>(games);
        sortedGames.sort(Comparator.comparing(g -> g.datePublished));
        return sortedGames;
    }

    public List<GameData> sortAlphabetical() {
        List<GameData> sortedGames = new ArrayList<>(games);
        sortedGames.sort(Comparator.comparing(g -> g.title));
        return sortedGames;
    }

    public List<GameData> sortPrice() {
        List<GameData> sortedGames = new ArrayList<>(games);
        sortedGames.sort((g1, g2) -> (int)(g1.salesPrice - g2.salesPrice));
        return sortedGames;
    }
}
