package fr.seinksansdooze.backend.connectionManagemen;


import fr.seinksansdooze.backend.ADBridge.IADQuerier;
import org.junit.jupiter.api.Test;

import javax.naming.NamingException;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class ADConexionManagerTest {

    @Test
    public void test() {

        ADConnectionManager manager = new ADConnectionManager(new TokenGenerator(), new TokenSanitizer(), ADQuerierMoke.class);
        AtomicReference<String> token = new AtomicReference<>();
        assertDoesNotThrow(() -> {
            token.set(manager.addConnection("user1", "mdp1"));
        });
        assertThrows(NamingException.class, () -> manager.addConnection("user1", "mdp2"));
        assertDoesNotThrow(() -> {
            IADQuerier q= manager.getQuerier(token.get());
            assertTrue(q instanceof ADQuerierMoke);
        });
    }
}
