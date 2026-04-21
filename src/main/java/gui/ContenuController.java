package gui;

import entities.Contenu;
import entities.Cours;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContenuController {

    public void configureFormDefaults(
            CheckBox videoCheck,
            CheckBox pdfCheck,
            CheckBox pptCheck,
            CheckBox texteCheck,
            CheckBox quizCheck,
            CheckBox lienCheck,
            ComboBox<String> publicCombo,
            DatePicker dateAjoutPicker
    ) {
        texteCheck.setSelected(true);
        videoCheck.setSelected(false);
        pdfCheck.setSelected(false);
        pptCheck.setSelected(false);
        quizCheck.setSelected(false);
        lienCheck.setSelected(false);

        publicCombo.setItems(FXCollections.observableArrayList("oui", "non"));
        if (publicCombo.getValue() == null) {
            publicCombo.setValue("non");
        }
        if (dateAjoutPicker.getValue() == null) {
            dateAjoutPicker.setValue(LocalDate.now());
        }
    }

    public Map<String, String> validateContenuForm(
            List<String> selectedTypes,
            TextField titreField,
            TextArea descriptionArea,
            TextField dureeField,
            TextField ordreField,
            ComboBox<String> publicCombo,
            TextField vuesField,
            TextField formatField,
            TextArea ressourcesArea,
            TextField urlField,
            TextField pdfField,
            TextField pptField,
            TextField videoField,
            TextField lienField,
            TextField quizField
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        String titre = safeText(titreField.getText());
        String description = safeText(descriptionArea.getText());
        String duree = safeText(dureeField.getText());
        String ordre = safeText(ordreField.getText());
        String vues = safeText(vuesField.getText());
        String format = safeText(formatField.getText());
        String ressources = safeText(ressourcesArea.getText());
        String genericUrl = safeText(urlField.getText());
        String pdf = safeText(pdfField.getText());
        String ppt = safeText(pptField.getText());
        String video = safeText(videoField.getText());
        String lien = safeText(lienField.getText());
        String quiz = safeText(quizField.getText());

        if (selectedTypes.isEmpty()) {
            errors.put("type", "Choisissez au moins un type de contenu.");
        }
        if (titre.length() < 3) {
            errors.put("titre", "Le titre du contenu doit contenir au moins 3 caracteres.");
        }
        if (!description.isEmpty() && description.length() < 10) {
            errors.put("description", "La description doit contenir au moins 10 caracteres lorsqu'elle est remplie.");
        }
        if (selectedTypes.contains("texte") && description.length() < 10) {
            errors.put("description", "Pour un contenu texte, la description doit contenir au moins 10 caracteres.");
        }
        if (!genericUrl.isEmpty() && !isValidPathOrUrl(genericUrl)) {
            errors.put("url", "Le lien principal doit etre un lien http(s) ou un chemin local valide.");
        }
        if (selectedTypes.contains("pdf")) {
            if (pdf.isEmpty()) {
                errors.put("pdf", "Selectionnez un fichier PDF.");
            } else if (!pdf.toLowerCase().endsWith(".pdf")) {
                errors.put("pdf", "Le fichier selectionne doit etre un PDF.");
            }
        }
        if (selectedTypes.contains("ppt")) {
            if (ppt.isEmpty()) {
                errors.put("ppt", "Selectionnez un fichier PowerPoint.");
            } else if (!(ppt.toLowerCase().endsWith(".ppt") || ppt.toLowerCase().endsWith(".pptx"))) {
                errors.put("ppt", "Le fichier selectionne doit etre au format .ppt ou .pptx.");
            }
        }
        if (selectedTypes.contains("video")) {
            if (video.isEmpty()) {
                errors.put("video", "Selectionnez un fichier video ou renseignez un chemin valide.");
            } else if (!isValidPathOrUrl(video)) {
                errors.put("video", "Le chemin video doit etre un lien http(s) ou un chemin local valide.");
            }
        }
        if (selectedTypes.contains("lien")) {
            if (lien.isEmpty()) {
                errors.put("lien", "Renseignez un lien pour ce contenu.");
            } else if (!isHttpUrl(lien)) {
                errors.put("lien", "Le lien doit commencer par http:// ou https://.");
            }
        }
        if (selectedTypes.contains("quiz") && quiz.length() < 3) {
            errors.put("quiz", "Renseignez une reference de quiz valide.");
        }
        if (!duree.isEmpty()) {
            try {
                int parsed = Integer.parseInt(duree);
                if (parsed <= 0) {
                    errors.put("duree", "La duree doit etre superieure a zero.");
                }
            } catch (NumberFormatException e) {
                errors.put("duree", "La duree doit etre un entier valide.");
            }
        }
        if (ordre.isEmpty()) {
            errors.put("ordre", "L'ordre d'affichage est obligatoire.");
        } else {
            try {
                int parsed = Integer.parseInt(ordre);
                if (parsed < 0) {
                    errors.put("ordre", "L'ordre d'affichage doit etre positif ou nul.");
                }
            } catch (NumberFormatException e) {
                errors.put("ordre", "L'ordre d'affichage doit etre un entier valide.");
            }
        }
        if (publicCombo.getValue() == null || (!"oui".equals(publicCombo.getValue()) && !"non".equals(publicCombo.getValue()))) {
            errors.put("public", "Veuillez choisir si le contenu est public.");
        }
        if (!vues.isEmpty()) {
            try {
                int parsed = Integer.parseInt(vues);
                if (parsed < 0) {
                    errors.put("vues", "Le nombre de vues doit etre positif ou nul.");
                }
            } catch (NumberFormatException e) {
                errors.put("vues", "Le nombre de vues doit etre un entier valide.");
            }
        }
        if (!format.isEmpty() && format.length() < 2) {
            errors.put("format", "Le format saisi est trop court.");
        }
        if (!ressources.isEmpty()) {
            String resourceError = validateOptionalResources(ressources);
            if (resourceError != null) {
                errors.put("ressources", resourceError);
            }
        }

        return errors;
    }

    public Contenu buildContenu(
            Contenu contenu,
            Cours cours,
            List<String> selectedTypes,
            TextField titreField,
            TextField urlField,
            TextArea descriptionArea,
            TextField dureeField,
            TextField ordreField,
            ComboBox<String> publicCombo,
            DatePicker dateAjoutPicker,
            TextField vuesField,
            TextField formatField,
            TextArea ressourcesArea,
            TextField pdfField,
            TextField pptField,
            TextField videoField,
            TextField lienField,
            TextField quizField
    ) {
        Contenu target = contenu != null ? contenu : new Contenu();

        target.setCours(cours);
        target.setTypeContenuFromArray(selectedTypes);
        target.setTitre(safeText(titreField.getText()));
        target.setDescription(nullableText(descriptionArea.getText()));
        target.setDuree(parseNullableInteger(dureeField.getText()));
        target.setOrdreAffichage(Integer.parseInt(safeText(ordreField.getText())));
        target.setEstPublic("oui".equals(publicCombo.getValue()));
        target.setDateAjout(dateAjoutPicker.getValue() != null ? dateAjoutPicker.getValue().atStartOfDay() : null);
        target.setNombreVues(parseIntegerOrZero(vuesField.getText()));
        target.setFormat(nullableText(formatField.getText()));
        target.setUrlContenu(resolvePrimaryUrl(urlField, pdfField, pptField, videoField, lienField, quizField));
        target.setRessources(parseCommaSeparated(ressourcesArea.getText()));
        return target;
    }

    public void populateForm(
            Contenu contenu,
            CheckBox videoCheck,
            CheckBox pdfCheck,
            CheckBox pptCheck,
            CheckBox texteCheck,
            CheckBox quizCheck,
            CheckBox lienCheck,
            TextField titreField,
            TextField urlField,
            TextArea descriptionArea,
            TextField dureeField,
            TextField ordreField,
            ComboBox<String> publicCombo,
            DatePicker dateAjoutPicker,
            TextField vuesField,
            TextField formatField,
            TextArea ressourcesArea,
            TextField pdfField,
            TextField pptField,
            TextField videoField,
            TextField lienField,
            TextField quizField
    ) {
        List<String> types = contenu.getTypeContenuList().stream()
                .map(this::safeText)
                .filter(type -> !type.isEmpty())
                .collect(Collectors.toList());
        videoCheck.setSelected(types.contains("video"));
        pdfCheck.setSelected(types.contains("pdf"));
        pptCheck.setSelected(types.contains("ppt"));
        texteCheck.setSelected(types.isEmpty() || types.contains("texte"));
        quizCheck.setSelected(types.contains("quiz"));
        lienCheck.setSelected(types.contains("lien"));

        titreField.setText(contenu.getTitre());
        descriptionArea.setText(contenu.getDescription());
        dureeField.setText(contenu.getDuree() != null ? String.valueOf(contenu.getDuree()) : "");
        ordreField.setText(String.valueOf(contenu.getOrdreAffichage()));
        publicCombo.setValue(contenu.isEstPublic() ? "oui" : "non");
        dateAjoutPicker.setValue(contenu.getDateAjout() != null ? contenu.getDateAjout().toLocalDate() : LocalDate.now());
        vuesField.setText(String.valueOf(contenu.getNombreVues()));
        formatField.setText(contenu.getFormat());

        String pdf = contenu.hasType("pdf") ? safeText(contenu.getUrlContenu()) : "";
        String ppt = contenu.hasType("ppt") ? safeText(contenu.getUrlContenu()) : "";
        String video = contenu.hasType("video") ? safeText(contenu.getUrlContenu()) : "";
        String lien = contenu.hasType("lien") ? safeText(contenu.getUrlContenu()) : "";
        String quiz = contenu.hasType("quiz") ? safeText(contenu.getUrlContenu()) : "";

        urlField.setText(!pdf.isEmpty() || !ppt.isEmpty() || !video.isEmpty() || !lien.isEmpty() || !quiz.isEmpty()
                ? ""
                : contenu.getUrlContenu());
        pdfField.setText(pdf);
        pptField.setText(ppt);
        videoField.setText(video);
        lienField.setText(lien);
        quizField.setText(quiz);
        ressourcesArea.setText(contenu.getRessources() != null ? String.join(", ", contenu.getRessources()) : "");
    }

    public void clearForm(
            CheckBox videoCheck,
            CheckBox pdfCheck,
            CheckBox pptCheck,
            CheckBox texteCheck,
            CheckBox quizCheck,
            CheckBox lienCheck,
            TextField titreField,
            TextField urlField,
            TextArea descriptionArea,
            TextField dureeField,
            TextField ordreField,
            ComboBox<String> publicCombo,
            DatePicker dateAjoutPicker,
            TextField vuesField,
            TextField formatField,
            TextArea ressourcesArea,
            TextField pdfField,
            TextField pptField,
            TextField videoField,
            TextField lienField,
            TextField quizField
    ) {
        videoCheck.setSelected(false);
        pdfCheck.setSelected(false);
        pptCheck.setSelected(false);
        texteCheck.setSelected(true);
        quizCheck.setSelected(false);
        lienCheck.setSelected(false);
        titreField.clear();
        urlField.clear();
        descriptionArea.clear();
        dureeField.clear();
        ordreField.setText("0");
        publicCombo.setValue("non");
        dateAjoutPicker.setValue(LocalDate.now());
        vuesField.setText("0");
        formatField.clear();
        ressourcesArea.clear();
        pdfField.clear();
        pptField.clear();
        videoField.clear();
        lienField.clear();
        quizField.clear();
    }

    private String resolvePrimaryUrl(
            TextField urlField,
            TextField pdfField,
            TextField pptField,
            TextField videoField,
            TextField lienField,
            TextField quizField
    ) {
        String genericUrl = nullableText(urlField.getText());
        if (genericUrl != null) {
            return genericUrl;
        }

        for (TextField field : List.of(pdfField, pptField, videoField, lienField, quizField)) {
            String value = nullableText(field.getText());
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private boolean isHttpUrl(String value) {
        return value.matches("^https?://.+");
    }

    private boolean isValidPathOrUrl(String value) {
        return isHttpUrl(value) || value.matches("^[A-Za-z]:\\\\.+") || value.matches("^[\\\\/\\w .\\-()]+$");
    }

    private Integer parseNullableInteger(String value) {
        String text = safeText(value);
        return text.isEmpty() ? null : Integer.parseInt(text);
    }

    private int parseIntegerOrZero(String value) {
        String text = safeText(value);
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }

    private List<String> parseCommaSeparated(String raw) {
        String value = safeText(raw);
        if (value.isEmpty()) {
            return FXCollections.observableArrayList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    private String validateOptionalResources(String raw) {
        if (raw.endsWith(",")) {
            return "Supprimez la virgule finale ou ajoutez une ressource apres.";
        }

        List<String> resources = Arrays.stream(raw.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        for (String resource : resources) {
            if (resource.isEmpty()) {
                return "Chaque ressource doit contenir une valeur valide.";
            }
            if (resource.length() < 2) {
                return "Chaque ressource doit contenir au moins 2 caracteres.";
            }
        }

        return null;
    }

    private String nullableText(String value) {
        String text = safeText(value);
        return text.isEmpty() ? null : text;
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }
}
