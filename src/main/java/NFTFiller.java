import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NFTFiller {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        final ObjectMapper objectMapper = new ObjectMapper();
        HttpClient client = HttpClient.newHttpClient();

        while (true) {
            final Instant now = Instant.now();
            final Instant after = now.minus(5, ChronoUnit.MINUTES);
            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(new URI(
                                                   "https://wax.cryptolions"
                                                         //TODO use skip
                                                         + ".io/v2/history/get_actions?limit=100&act"
                                                         + ".account=simpleassets&act.name=createlog"
                                                         + "&after=" + after.toString()
                                                         + "&before=" + now.toString()))
                                             .GET()
                                             .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            final Map<String, Object> resp = objectMapper.readValue(response.body(), new TypeReference<>() {});
            for (Object action : (List<Object>) resp.get("actions")) {
                final HttpResponse<String> elasticResponse = client.send(HttpRequest.newBuilder() // use bulk
                                                                                    .uri(new URI("http://localhost:9200/nft_new/_doc"))
                                                                                    .headers("Content-Type", "application/json")
                                                                                    .POST(BodyPublishers.ofString(
                                                                                          objectMapper.writeValueAsString(action))
                                                                                    )
                                                                                    .build(),
                      BodyHandlers.ofString());
                System.out.println(elasticResponse.statusCode());
                System.out.println(elasticResponse.body());
            }
            Thread.sleep(5*60*1000); //TODO 200 ms
        }

    }
}
