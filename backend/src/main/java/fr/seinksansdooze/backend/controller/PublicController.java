package fr.seinksansdooze.backend.controller;

import fr.seinksansdooze.backend.connectionManaging.ADBridge.ADQuerier;
import fr.seinksansdooze.backend.connectionManaging.ADBridge.IPublicADQuerier;
import fr.seinksansdooze.backend.model.response.FullPerson;
import fr.seinksansdooze.backend.model.response.FullStructure;
import fr.seinksansdooze.backend.model.response.PartialPerson;
import fr.seinksansdooze.backend.model.response.PartialStructure;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final IPublicADQuerier querier;

    public PublicController() {
        querier = ADQuerier.getPublicADQuerier();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Les résultats sont disponibles"),
            @ApiResponse(responseCode = "400", description = "Il manque un ou plusieurs paramètres")
    })
    @GetMapping("/search/person")
    public List<PartialPerson> searchPerson(@RequestParam String name,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "25") int perPage) {
        return querier.searchPerson(name);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Les résultats sont disponibles"),
            @ApiResponse(responseCode = "400", description = "Il manque un ou plusieurs paramètres")
    })
    @GetMapping("/search/structure")
    public List<PartialStructure> searchStructure(@RequestParam String name,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "25") int perPage) {
        return querier.searchStructure(name);
    }

    @GetMapping("/info/person/{cn}")
    public PartialPerson personInfo(@PathVariable String cn) {
        return querier.getPersonInfo(cn);
    }

    @GetMapping("/info/structure/{ou}")
    public PartialStructure structureInfo(@PathVariable String ou) {
        return querier.getStructureInfo(ou);
    }
}
