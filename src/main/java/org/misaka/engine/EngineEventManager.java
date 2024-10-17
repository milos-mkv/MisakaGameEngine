package org.misaka.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class EngineEventManager {

    private static final Map<String, Consumer<Object>> listeners = new HashMap<>();

    public static void addListener(String message, Consumer<Object> callback) {
        listeners.put(message, callback);
    }

    public static void removeListener(String message) {
        listeners.remove(message);
    }

    public static void dispatch(String message, Object data) {
        Consumer<Object> callback = listeners.get(message);
        if (callback != null) {
            callback.accept(data);
        }
    }

    public static void dispatch(String message) {
        dispatch(message, null);
    }


}
