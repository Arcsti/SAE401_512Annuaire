package fr.seinksansdooze.backend.connectionManaging.ADBridge;

import lombok.SneakyThrows;
import fr.seinksansdooze.backend.connectionManaging.ADBridge.model.ObjectType;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public abstract class ADQuerier {
    private static final String AD_URL = "ldap://10.22.32.2:389/";  //389 (search et createSubcontext) ou 3268 (search only) ou 636 (SSL)
    protected static final String AD_BASE = "OU=512BankFR,DC=EQUIPE1B,DC=local";

    protected DirContext context;

    protected ADQuerier(String username, String pwd) {
        //rajouter un systeme de session et d'authentification
        boolean connected = this.login(username, pwd);
        if (!connected) {
            throw new RuntimeException("Impossible de se connecter à l'annuaire Active Directory");
        }
    }

    public ADQuerier() {
    }

    /////////////////////////// Méthode de connexion et de déconnexion ///////////////////////////

    /**
     * Méthode répondant à la route /api/auth/login en connectant l'utilisateur à l'annuaire Active Directory
     * @param username le nom d'utilisateur
     * @param pwd le mot de passe
     * @return true si la connexion a réussi, false sinon
     */
    public boolean login(String username, String pwd) {
        Properties env = new Properties();
        username = username + "@EQUIPE1B.local";
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, AD_URL);
//        env.put(Context.SECURITY_PROTOCOL, "ssl");
        env.put(Context.SECURITY_AUTHENTICATION, "Simple");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, pwd);
        try {
            this.context = new InitialDirContext(env);
            return true;
        } catch (NamingException e) {
            return false;
        }
    }

    private boolean isAdminConnected(String username) {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        String filter = "(&(objectClass=user)(userPrincipalName=" + username + ")(memberOf=CN=Administrateurs,CN=Builtin,DC=EQUIPE1B,DC=local))";
        NamingEnumeration<SearchResult> res;
        try {
            res = this.context.search(AD_BASE, filter, searchControls);
            return res.hasMore();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Méthode répondant à la route /api/auth/logout en déconnectant l'utilisateur de l'annuaire Active Directory
     * @return true si la déconnexion a réussi, false sinon
     */
    public boolean logout() {
        try {
            this.context.close();
            return true;
        } catch (NamingException e) {
            return false;
        }
    }

    /**
     * Déroule les résultats d'une recherche LDAP, pagine-les et les place dans une liste.
     *
     * @param searchResults    Une énumération de résultats de recherche LDAP à dérouler. Si elle est nulle, une liste
     *                         vide est automatiquement retournée.
     * @param page             Le numéro de la page de résultats à retourner (commençant à zéro).
     * @param perPage          Le nombre d'éléments par page à retourner.
     * @param listContentClass La classe d'objet à instancier pour chaque résultat de recherche. Cette classe doit avoir
     *                         un constructeur prenant un objet SearchResult en paramètre.
     * @return Une liste d'objets correspondants au contenu de la liste spécifiée, paginée selon les paramètres
     * spécifiés. Si aucun résultat n'est trouvé, une liste vide est renvoyée.
     */
    @SneakyThrows
    protected static List<?> unroll(NamingEnumeration<SearchResult> searchResults, int page, int perPage, Class<?> listContentClass) {
        if (searchResults == null) return List.of();

        List<Object> items = new ArrayList<>();

        boolean hasMore;
        while (true) {
            try {
                hasMore = searchResults.hasMore();
            } catch (NamingException e) {
                hasMore = false;
            }
            if (!hasMore) break;

            SearchResult currentItem;
            try {
                currentItem = searchResults.next();
            } catch (NamingException e) {
                break;
            }

            Object content = listContentClass.getDeclaredConstructor(SearchResult.class).newInstance(currentItem);
            items.add(content);
        }

        // On fait la pagination
        int startIndex = page * perPage;
        if (startIndex > items.size()) return List.of();

        int endIndex = Math.min(startIndex + perPage, items.size());

        return items.subList(startIndex, endIndex);
    }

    /////////////////////////// Méthodes pour le MemberController ///////////////////////////




    /////////////////////////// Méthodes pour le AdminController ///////////////////////////



    /////////////////////////// Méthodes de requete AD ///////////////////////////

    //  api/search/person   api/search/structures   api/admin/group/all
    // recherche une personne ou une structure
    // consulter la liste des groupes
    // TODO rajouter un paramètre filtrer pour permettre les requetes incluant des filtres
    protected NamingEnumeration<SearchResult> search(ObjectType searchType, String searchValue) {
        String filter = "(&(objectClass=" + searchType + ")(" + searchType.getNamingAttribute() + "=*" + searchValue + "*))";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> res;
        try {
            res = this.context.search(AD_BASE, filter, searchControls);
            return res;
        } catch (NamingException e) {
//            throw new RuntimeException(e);
            return null;
        }
    }


    //  api/info/structure/{ou}
//    public NamingEnumeration<SearchResult> searchStructure(String ou) {
//        String filter = "(&(objectClass=organizationalUnit)(distinguishedName=" + ou + "))";
//        SearchControls searchControls = new SearchControls();
//        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//        NamingEnumeration<SearchResult> res;
//        try {
//            res = this.context.search(AD_BASE, filter, searchControls);
//            return res;
//        } catch (NamingException e) {
//            return null;
//        }
//    }





    // api/admin/group/all
    public NamingEnumeration<SearchResult> searchAllGroups() {
        String filter = "(&(objectClass=group))";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> res;
        try {
            res = this.context.search(AD_BASE, filter, searchControls);
            return res;
        } catch (NamingException e) {
            return null;
        }
    }



    //  api/admin/group/remove/{groupName}/{username}


    //  api/admin/group/members/{groupName}
    //TODO gérer l'erreur si le groupe n'existe pas et si le groupe est vide
    public ArrayList<SearchResult> queryGroupMembers(String groupName) {
        String filter = "(&(objectClass=group)(CN=" + groupName + "))";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> res;
        try {
            try {
                res = this.context.search(AD_BASE, filter, searchControls);
            } catch (NamingException ex) {
                throw new RuntimeException(ex);
            }
//            System.out.println("nom du group trouvé : ");
//            TestADQuerier.displayResults(res,ObjectType.GROUP);
            //le group test est bien trouvé
            if (res.hasMore()) {
                Attributes groupAttributs = res.next().getAttributes();
                Attribute groupMembersName = groupAttributs.get("member");
                ArrayList<SearchResult> groupMembers = new ArrayList<>();
                NamingEnumeration<?> membersList = groupMembersName.getAll();
                while (membersList.hasMore()) {
                    String currentMemberCN = membersList.next().toString();
                    NamingEnumeration<SearchResult> currentMemberEnum = search(ObjectType.PERSON,currentMemberCN.split(",")[0].split("=")[1]);
                    if(currentMemberEnum.hasMore()){
                        SearchResult currentMember = currentMemberEnum.next();
                        groupMembers.add(currentMember);
                    }
                }
                return groupMembers;
            }else {
                return null;
            }
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    //  api/admin/search/person/{person}/{groupName}
    private NamingEnumeration<SearchResult> searchPerson(String person, String groupName) {
        String filter = "(&(objectClass=user)(CN=*" + person + "*))";
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<SearchResult> res;
        try {
            res = this.context.search(AD_BASE, filter, searchControls);
            if (res.hasMore()) {
                return res;
            } else {
                return null;
            }
        } catch (NamingException e) {
            return null;
        }
    }

}
