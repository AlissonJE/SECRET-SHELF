package com.secretshelf.shelf;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping({"", "/"})
public class ShelfController {
    private final ShelfRepository shelves; private final StoredBookRepository books;
    public ShelfController(ShelfRepository shelves,StoredBookRepository books){this.shelves=shelves;this.books=books;}
    public record BookDto(String volumeId,String title,String authors,String thumbnail,java.time.Instant addedAt){}
    public record ShelfDto(String id,String name,java.time.Instant createdAt,List<BookDto> books){}
    public record CreateShelf(String name){}
    public record SaveBook(String volumeId,String title,List<String> authors,String thumbnail){}
    @GetMapping public List<ShelfDto> all(@RequestHeader("X-User-Id") String user){return shelves.findByOwnerIdOrderByCreatedAtDesc(user).stream().map(s->dto(s)).toList();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ShelfDto create(@RequestHeader("X-User-Id") String user,@RequestBody CreateShelf input){if(input.name()==null||input.name().isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La estantería necesita un nombre");return dto(shelves.save(new ReadingShelf(user,input.name().trim().substring(0,Math.min(80,input.name().trim().length())))));}
    @PostMapping("/{id}/books") @ResponseStatus(HttpStatus.CREATED) public BookDto add(@RequestHeader("X-User-Id") String user,@PathVariable("id") String id,@RequestBody SaveBook input){
        owned(user,id); if(input.volumeId()==null||input.title()==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Falta el libro");
        if(books.findByShelfIdOrderByAddedAtDesc(id).stream().anyMatch(b->b.getVolumeId().equals(input.volumeId())))throw new ResponseStatusException(HttpStatus.CONFLICT,"El libro ya está guardado en esta estantería");
        var book=books.save(new StoredBook(id,input.volumeId(),input.title().substring(0,Math.min(300,input.title().length())),input.authors()==null?"":String.join(", ",input.authors()).substring(0,Math.min(500,String.join(", ",input.authors()).length())),input.thumbnail()));
        return bookDto(book);
    }
    @DeleteMapping("/{id}/books/{volumeId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void remove(@RequestHeader("X-User-Id") String user,@PathVariable("id") String id,@PathVariable("volumeId") String volumeId){owned(user,id);books.deleteByShelfIdAndVolumeId(id,volumeId);}
    private ReadingShelf owned(String user,String id){return shelves.findByIdAndOwnerId(id,user).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Estantería no encontrada"));}
    private ShelfDto dto(ReadingShelf s){return new ShelfDto(s.getId(),s.getName(),s.getCreatedAt(),books.findByShelfIdOrderByAddedAtDesc(s.getId()).stream().map(ShelfController::bookDto).toList());}
    private static BookDto bookDto(StoredBook b){return new BookDto(b.getVolumeId(),b.getTitle(),b.getAuthors(),b.getThumbnail(),b.getAddedAt());}
}
