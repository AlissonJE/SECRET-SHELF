package com.secretshelf.profile;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping({"", "/"})
public class ProfileController {
    private final ProfileRepository profiles;
    public ProfileController(ProfileRepository profiles){this.profiles=profiles;}
    public record ProfileDto(String displayName,String ageRange,List<String> genres,String theme,String density,String language){}
    @GetMapping public ProfileDto get(@RequestHeader("X-User-Id") String userId){return toDto(profiles.findById(userId).orElseGet(()->profiles.save(new Profile(userId))));}
    @PutMapping public ProfileDto update(@RequestHeader("X-User-Id") String userId,@RequestBody ProfileDto dto){
        var p=profiles.findById(userId).orElseGet(()->profiles.save(new Profile(userId)));
        if(dto.displayName()!=null && !dto.displayName().isBlank())p.setDisplayName(dto.displayName().substring(0,Math.min(80,dto.displayName().length())));
        if(dto.ageRange()!=null){if(!List.of("child","teen","adult").contains(dto.ageRange()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Rango de edad no válido");p.setAgeRange(dto.ageRange());}
        if(dto.genres()!=null)p.setGenres(com.fasterxml.jackson.databind.json.JsonMapper.builder().build().valueToTree(dto.genres().stream().limit(12).toList()).toString());
        if(dto.theme()!=null){if(!List.of("light","dark").contains(dto.theme()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Tema no válido");p.setTheme(dto.theme());}
        if(dto.density()!=null){if(!List.of("grid","list").contains(dto.density()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Vista no válida");p.setDensity(dto.density());}
        if(dto.language()!=null && dto.language().matches("[a-z]{2,5}"))p.setLanguage(dto.language());
        return toDto(profiles.save(p));
    }
    private ProfileDto toDto(Profile p){try{return new ProfileDto(p.getDisplayName(),p.getAgeRange(),com.fasterxml.jackson.databind.json.JsonMapper.builder().build().readValue(p.getGenres(),new com.fasterxml.jackson.core.type.TypeReference<List<String>>(){}),p.getTheme(),p.getDensity(),p.getLanguage());}catch(Exception e){return new ProfileDto(p.getDisplayName(),p.getAgeRange(),List.of(),p.getTheme(),p.getDensity(),p.getLanguage());}}
}
