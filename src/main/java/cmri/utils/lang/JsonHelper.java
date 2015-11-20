package cmri.utils.lang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;
import java.util.Set;

/**
 * Created by zhuyin on 3/18/15.
 */
public class JsonHelper {

    public static String toJson(Object obj){
        return JSON.toJSONString(obj, SerializerFeature.WriteClassName);
    }

    public static <T> T parseObject(String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }

    public static Set<String> parseStringSet(String json) {
        return new Gson().fromJson(json, new TypeToken<Set<String>>() {
        }.getType());
    }

    public static Map<String, String> parseStringMap(String json) {
        Map<String, String> map = new Gson().fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
        map.remove("@type");
        return map;
    }

    public static Map<String, Object> parseStringObjectMap(String json) {
        Map<String, Object> map = new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
        map.remove("@type");
        return map;
    }
}
