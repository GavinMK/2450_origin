package origin.views;

import javafx.scene.input.KeyCode;

import java.util.*;
import java.util.function.Consumer;

public class KeyManager {
    private HashMap<KeyCode, HashMap<String, Consumer>> listeners = new HashMap<>();

    public String addListener(List<KeyCode> watchKeys, Consumer<KeyCode> listener) {
        String subID = UUID.randomUUID().toString();
        for (KeyCode keyCode: watchKeys) {
            if (!listeners.containsKey(keyCode)) {
                listeners.put(keyCode, new HashMap<>());
            }
            listeners.get(keyCode).put(subID, listener);
        }
        return subID;
    }

    public String addListener(KeyCode watchKey, Consumer<KeyCode> listener) {
        return addListener(Arrays.asList(watchKey), listener);
    }

    public void removeListener(String subID) {
        for (HashMap<String, Consumer> keyMap: listeners.values()) {
            keyMap.remove(subID);
        }
    }

    public void trigger(KeyCode keyCode) {
        if (listeners.containsKey(keyCode)) {
            for (Consumer consumer: listeners.get(keyCode).values()) {
                consumer.accept(keyCode);
            }
        }
    }
}