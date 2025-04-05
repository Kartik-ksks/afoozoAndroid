package in.kpis.afoozo.okhttpServcice;

import com.google.gson.Gson;

/**
 * Created by wingstud on 24-11-2016.
 */
public class JsonDeserializer {
    private static Gson gson = new Gson();

    public static <T> T deserializeJson(String jsonString, Class<T> type) {
        T result = null;

        result = gson.fromJson(jsonString, type);
        return result;
    }
}
