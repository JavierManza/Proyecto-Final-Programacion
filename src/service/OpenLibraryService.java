package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase encargada de comunicarse con la API de Open Library para obtener
 * información de los libros a partir de su ISBN.
 */
public class OpenLibraryService {

    /**
     * Clase interna que sirve para almacenar la información básica que 
     * recuperamos de la API: título, portada y sinopsis.
     */
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

    /**
     * Método principal que busca un libro por su ISBN.
     * @param isbn El número ISBN del libro a buscar.
     * @return Un objeto BookInfo con los datos, o null si no lo encuentra.
     */
    public static BookInfo fetchByIsbn(String isbn) {
        // 1. Construimos la dirección web (URL) para hacer la consulta a la API.
        // Solicitamos que la respuesta venga en formato JSON.
        String apiUrl = "https://openlibrary.org/api/books?bibkeys=ISBN:" + isbn + "&format=json&jscmd=data";
        
        try {
            // 2. Preparamos la conexión a internet hacia la URL generada.
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Indicamos que queremos hacer una petición GET (para obtener información).
            conn.setRequestMethod("GET");

            // 3. Comprobamos si la respuesta del servidor es OK (Código 200).
            if (conn.getResponseCode() == 200) {
                // Preparamos un lector para ir leyendo la respuesta de la página línea por línea.
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder(); // Aquí guardaremos todo el texto
                String inputLine;
                
                // Mientras haya líneas que leer, las vamos añadiendo a 'response'.
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close(); // Cerramos el lector cuando terminamos.

                // 4. Convertimos todo lo leído en un único texto (String).
                String json = response.toString();
                
                // Si la respuesta es "{}", significa que la API no encontró el libro para ese ISBN.
                if (json.equals("{}")) return null;

                // 5. Extraemos la información del texto (JSON).
                // Como no usamos una librería específica para JSON, buscamos el título con un patrón de texto (regex).
                String title = extractValue(json, "\"title\": \"(.*?)\"");
                
                // 6. Construimos el enlace a la portada (Open Library permite obtener las portadas con el ISBN).
                // El "-L" al final indica que queremos la imagen en tamaño grande (Large).
                String coverUrl = "https://covers.openlibrary.org/b/isbn/" + isbn + "-L.jpg";
                
                // Devolvemos el objeto con toda la información empaquetada.
                return new BookInfo(title, coverUrl, "Sinopsis obtenida de Open Library.");
            }
        } catch (Exception e) {
            // Si hay un error de conexión, imprimimos el fallo en la consola.
            e.printStackTrace();
        }
        
        // Si la conexión falla o el código no fue 200, devolvemos null (no se encontró).
        return null;
    }

    /**
     * Método auxiliar que busca un texto específico dentro de la respuesta JSON
     * utilizando Expresiones Regulares (Regex).
     * 
     * @param json El texto completo devuelto por la API.
     * @param regex El patrón que queremos buscar (ej. "\"title\": \"(.*?)\"").
     * @return El valor encontrado, o "Desconocido" si no encuentra nada.
     */
    private static String extractValue(String json, String regex) {
        // Preparamos el patrón de búsqueda
        Pattern pattern = Pattern.compile(regex);
        // Buscamos ese patrón dentro de nuestro texto JSON
        Matcher matcher = pattern.matcher(json);
        
        // Si encontramos una coincidencia...
        if (matcher.find()) {
            // ...devolvemos el valor capturado en el grupo 1 (lo que está entre paréntesis en la regex).
            return matcher.group(1);
        }
        
        return "Desconocido";
    }
}
