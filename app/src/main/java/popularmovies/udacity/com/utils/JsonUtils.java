package popularmovies.udacity.com.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class JsonUtils {

    public static JSONObject convertStringToJsonObject(String jsonString) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e(TAG, "ConvertStringToJsonObject: ", e);
        }

        return jsonObject;
    }
}
