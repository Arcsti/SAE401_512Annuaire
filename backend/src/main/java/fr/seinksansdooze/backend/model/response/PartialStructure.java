package fr.seinksansdooze.backend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.naming.directory.SearchResult;

/**
 * Une structure quand elle apparait dans les résultats de recherches.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartialStructure {
    private String ou;

    public PartialStructure(SearchResult sr) {
        this.ou = sr.getNameInNamespace().split(",")[0].split("=")[1];
    }
}
