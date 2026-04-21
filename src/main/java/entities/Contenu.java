package entities;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Contenu {

    public static final List<String> AVAILABLE_TYPES =
            Arrays.asList("video", "pdf", "ppt", "texte", "quiz", "lien");

    private Integer id;
    private Cours cours;

    private String typeContenu;
    private String titre;
    private String urlContenu;
    private String description;

    private Integer duree;
    private int ordreAffichage;
    private boolean estPublic;

    private LocalDateTime dateAjout;
    private int nombreVues;

    private String format;
    private List<String> ressources;

    // Constructeur
    public Contenu() {
        this.dateAjout = LocalDateTime.now();
        this.typeContenu = "texte";
        this.ressources = new ArrayList<>();
        this.nombreVues = 0;
        this.ordreAffichage = 0;
        this.estPublic = false;
    }

    // Getters & Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    public String getTypeContenu() {
        return typeContenu;
    }

    public void setTypeContenu(String typeContenu) {
        this.typeContenu = typeContenu;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getUrlContenu() {
        return urlContenu;
    }

    public void setUrlContenu(String urlContenu) {
        this.urlContenu = urlContenu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public int getOrdreAffichage() {
        return ordreAffichage;
    }

    public void setOrdreAffichage(int ordreAffichage) {
        this.ordreAffichage = ordreAffichage;
    }

    public boolean isEstPublic() {
        return estPublic;
    }

    public void setEstPublic(boolean estPublic) {
        this.estPublic = estPublic;
    }

    public LocalDateTime getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDateTime dateAjout) {
        this.dateAjout = dateAjout;
    }

    public int getNombreVues() {
        return nombreVues;
    }

    public void setNombreVues(int nombreVues) {
        this.nombreVues = nombreVues;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<String> getRessources() {
        return ressources;
    }

    public void setRessources(List<String> ressources) {
        this.ressources = ressources;
    }

    // Méthodes utiles (comme Symfony mais simplifiées)

    public List<String> getTypeContenuList() {
        if (typeContenu == null || typeContenu.isEmpty()) {
            return Arrays.asList("texte");
        }
        return Arrays.asList(typeContenu.split(","));
    }

    public void setTypeContenuFromArray(List<String> types) {
        this.typeContenu = String.join(",", types);
    }

    public boolean hasType(String type) {
        return getTypeContenuList().contains(type);
    }

    public List<String> getRessourcesForDisplay() {
        List<String> result = new ArrayList<>(ressources);

        if (result.isEmpty() && urlContenu != null && !urlContenu.isEmpty()) {
            result.add(urlContenu);
        }

        if (description != null && !description.isEmpty()) {
            result.add(description);
        }

        return result;
    }
}