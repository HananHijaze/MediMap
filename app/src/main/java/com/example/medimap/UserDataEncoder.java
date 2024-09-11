package com.example.medimap;

import com.example.medimap.server.User;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class UserDataEncoder {

    public static encodedUser encodeValues(User user) {
        // Create a new encodedUser object
        encodedUser encodedUser = new encodedUser();

        // Encode categorical attributes
        Integer encodedDietType = EncoderMappings.DIET_TYPE_ENCODINGS.get(user.getDietType());
        Integer encodedBodyType = EncoderMappings.BODY_TYPE_ENCODINGS.get(user.getBodyType());
        Integer encodedGender = EncoderMappings.GENDER_ENCODINGS.get(user.getGender());
        Integer encodedGoal = EncoderMappings.GOAL_ENCODINGS.get(user.getGoal());
        Integer encodedWhereToWorkout = EncoderMappings.WHERETOWORKOUT_ENCODINGS.get(user.getWheretoworkout());

        // Set the encoded values in the encodedUser object
        encodedUser.setDietType(encodedDietType != null ? encodedDietType : -1);
        encodedUser.setBodyType(encodedBodyType != null ? encodedBodyType : -1);
        encodedUser.setGender(encodedGender != null ? encodedGender : -1);
        encodedUser.setGoal(encodedGoal != null ? encodedGoal : -1);
        encodedUser.setWhereToWorkout(encodedWhereToWorkout != null ? encodedWhereToWorkout : -1);

        // Set the numerical and other attributes directly
        encodedUser.setHeight(user.getHeight());
        encodedUser.setWeight(user.getWeight());
        encodedUser.setAge(calculateAge(user.getBirthDate()));
        encodedUser.setMealsPerDay(user.getMealsperday());
        encodedUser.setSnacksPerDay(user.getSnackesperday());

        return encodedUser;
    }

    // Function to calculate age from birthdate
    private static int calculateAge(Date birthDate) {
        if (birthDate == null) {
            return -1; // Or any other indicator for missing value
        }

        // Convert java.util.Date to java.time.LocalDate
        LocalDate birthDateLocal = birthDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate today = LocalDate.now();
        return Period.between(birthDateLocal, today).getYears();
    }
}