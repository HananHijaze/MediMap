package com.example.medimap;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class ModelManager {
    private static ModelManager instance;
    private Interpreter tflite;

    private ModelManager(Context context) {
        try {
            Interpreter.Options options = new Interpreter.Options();
            tflite = new Interpreter(loadModelFile(context, "multi_output_model.tflite"), options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized ModelManager getInstance(Context context) {
        if (instance == null) {
            instance = new ModelManager(context.getApplicationContext());
        }
        return instance;
    }

    // Helper method to load the model file from assets
    private static MappedByteBuffer loadModelFile(Context context, String modelFileName) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelFileName);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public float[][] createPlan(encodedUser user) {
        // Prepare input data
        float[][] inputData = prepareInputData(user);

        // Create output arrays for each of the model outputs
        float[][] outputWorkout = new float[1][8];  // Assuming 8 classes for workout
        float[][] outputBreakfast = new float[1][8]; // Assuming 8 classes for breakfast
        float[][] outputLunch = new float[1][8]; // Assuming 8 classes for lunch
        float[][] outputDinner = new float[1][8]; // Assuming 8 classes for dinner
        float[][] outputSnack = new float[1][8]; // Assuming 8 classes for snack

        // Define input and output maps
        Object[] inputs = {inputData};

        Map<Integer, Object> outputs = new HashMap<>();
        outputs.put(0, outputWorkout);
        outputs.put(1, outputBreakfast);
        outputs.put(2, outputLunch);
        outputs.put(3, outputDinner);
        outputs.put(4, outputSnack);

        // Run inference
        tflite.runForMultipleInputsOutputs(inputs, outputs);

        // Collect and return the predictions from outputs
        return new float[][]{
                outputWorkout[0],
                outputBreakfast[0],
                outputLunch[0],
                outputDinner[0],
                outputSnack[0]
        };
    }

    // Method to prepare the input data
    private float[][] prepareInputData(encodedUser user) {
        // Create a 2D array with the required dimensions
        float[][] input = new float[1][10]; // Adjust the size based on your model's input

        // Populate the array with the encoded user data
        input[0][0] = user.getDietType();
        input[0][1] = user.getBodyType();
        input[0][2] = user.getGender();
        input[0][3] = user.getGoal();
        input[0][4] = (float) user.getHeight();
        input[0][5] = user.getMealsPerDay();
        input[0][6] = user.getSnacksPerDay();
        input[0][7] = (float) user.getWeight();
        input[0][8] = user.getWhereToWorkout();
        input[0][9] = (float) user.getAge();

        return input;
    }
}
