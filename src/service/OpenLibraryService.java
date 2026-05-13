package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenLibraryService {

    public static class BookInfo {
        public String title;
        public String coverUrl;
        public String synopsis;

        public BookInfo(String title, String coverUrl, String synopsis) {
            this.title = title;
            this.coverUrl = coverUrl;
            this.synopsis = synopsis;
        }
    }

    public static BookInfo fetchByIsbn(String isbn) {
        String apiUrl = "https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String json = response.toString();
                if (json.equals("{}")) return null;

                // Simple regex parsing to avoid full JSON library if not present
                String title = extractValue(json, "\"title\": \"(.*?)\"");
                String coverUrl = "https://covers.openlibrary.org/b/isbn/" + isbn + "-L.jpg";
                
                return new BookInfo(title, coverUrl, "Sinopsis obtenida de Open Library.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String extractValue(String json, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Desconocido";
    }
}
