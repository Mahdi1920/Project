package tn.esprit.project.utils;

import androidx.room.TypeConverter;
import tn.esprit.project.models.Category;

public class CategoryConverter {

    @TypeConverter
    public static String fromCategory(Category category) {
        return category == null ? null : category.name();
    }

    @TypeConverter
    public static Category toCategory(String value) {
        return value == null ? null : Category.valueOf(value);
    }
}
