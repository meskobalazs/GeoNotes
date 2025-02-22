package de.hauke_stieler.geonotes.export;

import java.util.ArrayList;
import java.util.List;

import de.hauke_stieler.geonotes.notes.Note;

/**
 * The model class which is needed for GSON to generate the json. GSON will just take every field
 * and turn it into according JSON. Because the JSON is nested, we here need nesting as well. Some
 * Fields are probably markes "unused" by your IDE, but they are in deed used by GSON ;)
 */
public class NoteExportModel {
    public final String type = "FeatureCollection";
    public List<NoteFeatureModel> features;

    public NoteExportModel(List<Note> notes) {
        this.features = new ArrayList<>(notes.size());
        for (Note note : notes) {
            NoteFeatureModel model = new NoteFeatureModel(
                    note.getId(),
                    note.getDescription(),
                    note.getCreationDateTimeString(),
                    note.getLon(),
                    note.getLat());
            features.add(model);
        }
    }
}

class NoteFeatureModel {
    final String type = "Feature";
    NotePropertiesModel properties;
    GeometryModel geometry;

    NoteFeatureModel(long id, String note, String created_at, double lon, double lat) {
        this.properties = new NotePropertiesModel(id, note, created_at);
        this.geometry = new GeometryModel(lon, lat);
    }
}

class NotePropertiesModel {
    long name;
    String description;
    String created_at;

    NotePropertiesModel(long id, String note, String created_at) {
        this.name = id;
        this.description = note;
        this.created_at = created_at;
    }
}

class GeometryModel {
    final String type = "Point";
    final double[] coordinates = new double[2];

    GeometryModel(double lon, double lat) {
        coordinates[0] = lon;
        coordinates[1] = lat;
    }
}