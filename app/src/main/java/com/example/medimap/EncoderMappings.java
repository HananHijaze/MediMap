package com.example.medimap;

import java.util.HashMap;
import java.util.Map;

public class EncoderMappings {
    // Define the mappings for each categorical attribute
    public static final Map<String, Integer> DIET_TYPE_ENCODINGS = new HashMap<>();
    public static final Map<String, Integer> BODY_TYPE_ENCODINGS = new HashMap<>();
    public static final Map<String, Integer> GENDER_ENCODINGS = new HashMap<>();
    public static final Map<String, Integer> GOAL_ENCODINGS = new HashMap<>();
    public static final Map<String, Integer> WHERETOWORKOUT_ENCODINGS = new HashMap<>();

    static {
        // Initialize the mappings
        DIET_TYPE_ENCODINGS.put("balanced", 0);
        DIET_TYPE_ENCODINGS.put("keto", 1);
        DIET_TYPE_ENCODINGS.put("low carb", 2);
        DIET_TYPE_ENCODINGS.put("paleo", 3);
        DIET_TYPE_ENCODINGS.put("vegan", 4);
        DIET_TYPE_ENCODINGS.put("vegetarian", 5);

        BODY_TYPE_ENCODINGS.put("heavier", 0);
        BODY_TYPE_ENCODINGS.put("normal", 1);
        BODY_TYPE_ENCODINGS.put("skinny", 2);

        GENDER_ENCODINGS.put("female", 0);
        GENDER_ENCODINGS.put("male", 1);

        GOAL_ENCODINGS.put("gain muscle", 0);
        GOAL_ENCODINGS.put("healthy life", 1);
        GOAL_ENCODINGS.put("lose weight", 2);

        WHERETOWORKOUT_ENCODINGS.put("gym", 0);
        WHERETOWORKOUT_ENCODINGS.put("home", 1);
    }
}
