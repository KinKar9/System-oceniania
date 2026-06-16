package pl.studenci.systemoceniania.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pl.studenci.systemoceniania.entity.Ocena;
import pl.studenci.systemoceniania.entity.Student;

import java.util.List;

@FeignClient(name = "system-oceniania", url = "http://localhost:8080/api")
public interface SystemOcenianiaClient {

    @GetMapping("/oceny")
    List<Ocena> getOceny(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long przedmiotId,
            @RequestParam(required = false) Long typId,
            @RequestParam(required = false) String dataOd,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String order
    );

    @GetMapping("/studenci/{id}")
    Student getStudent(@PathVariable("id") Long id);

}