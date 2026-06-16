package pl.studenci.systemoceniania.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.studenci.systemoceniania.entity.Ocena;
import pl.studenci.systemoceniania.entity.Student;

import java.util.List;

@Service
public class RestClientService {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api";

    public RestClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Ocena> getOceny(Long studentId, Long przedmiotId, Long typId, String dataOd, String sortBy, String order) {
        String url = baseUrl + "/oceny?studentId=" + studentId + "&przedmiotId=" + przedmiotId +
                "&typId=" + typId + "&dataOd=" + dataOd + "&sortBy=" + sortBy + "&order=" + order;
        ResponseEntity<List<Ocena>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Ocena>>() {}
        );
        return response.getBody();
    }

    public Student getStudent(Long id) {
        String url = baseUrl + "/studenci/" + id;
        return restTemplate.getForObject(url, Student.class);
    }
}