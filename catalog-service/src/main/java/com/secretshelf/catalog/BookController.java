package com.secretshelf.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping({"", "/"})
public class BookController {
    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final WebClient client;
    @Value("${google.books.api-key:}") private String apiKey;
    public BookController(WebClient.Builder builder){this.client=builder.baseUrl("https://www.googleapis.com/books/v1").build();}
    @GetMapping
    public JsonNode search(@RequestParam(name="q",defaultValue="") String q,@RequestParam(name="lang",defaultValue="") String lang,@RequestParam(name="age",defaultValue="adult") String age,@RequestParam(name="startIndex",defaultValue="0") int startIndex){
        if(q.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Escribe un título, autor o tema para buscar");
        JsonNode result;
        try {
            result=client.get().uri(uriBuilder -> {
                var url=uriBuilder.path("/volumes").queryParam("q",q).queryParam("maxResults",24).queryParam("startIndex",Math.max(0,startIndex)).queryParam("printType","books");
                if(!lang.isBlank()) url.queryParam("langRestrict",lang); if(!apiKey.isBlank()) url.queryParam("key",apiKey);
                return url.build();
            }).retrieve().bodyToMono(JsonNode.class).block();
        } catch (WebClientResponseException e) {
            log.error("Google Books respondió HTTP {} al buscar libros: {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Google Books respondió HTTP "+e.getStatusCode().value(),e);
        } catch (Exception e) {
            log.error("No se pudo conectar con Google Books", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"No se pudo conectar con Google Books",e);
        }
        if(result!=null && !"adult".equalsIgnoreCase(age) && result.has("items") && result.get("items").isArray()) {
            var safe=(com.fasterxml.jackson.databind.node.ObjectNode)result.deepCopy(); var items=(com.fasterxml.jackson.databind.node.ArrayNode)safe.path("items");
            for(int i=items.size()-1;i>=0;i--) if(items.get(i).path("volumeInfo").path("maturityRating").asText("").equalsIgnoreCase("MATURE")) items.remove(i);
            safe.set("items",items); return safe;
        }
        return result;
    }
    @GetMapping("/{id}")
    public JsonNode detail(@PathVariable("id") String id){
        try {
            return client.get().uri(uriBuilder -> {
                var url=uriBuilder.path("/volumes/{id}");
                if(!apiKey.isBlank()) url.queryParam("key",apiKey);
                return url.build(id);
            }).retrieve().bodyToMono(JsonNode.class).block();
        } catch (WebClientResponseException e) {
            log.error("Google Books respondió HTTP {} al recuperar un libro: {}", e.getStatusCode().value(), e.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Google Books respondió HTTP "+e.getStatusCode().value(),e);
        } catch (Exception e) {
            log.error("No se pudo conectar con Google Books para recuperar un libro", e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"No se pudo conectar con Google Books",e);
        }
    }
}
