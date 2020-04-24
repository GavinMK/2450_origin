package origin.utils;

import javafx.util.Pair;
import java.util.*;
import java.util.function.Consumer;

/*
    Stores and sends global state of application
 */
public class RouteState {
    //Maximum number of states that can be stored (how many times can you hit the back button)
    private static final int MAX_STATES = 50;
    //Lambdas listening for state updates, key is a unique ID given to the subscriber
    private HashMap<String, Consumer<HashMap<String, Object>>> subscribers = new HashMap<>();
    private LinkedList<HashMap<String, Object>> states = new LinkedList<>();
    //Index of the current state
    private int stateI;

    public RouteState(ArrayList<Pair<String, Object>> initialState) {
        pushState(initialState);
    }

    //Add a new state that applies the given changes to the current state
    public void pushState(ArrayList<Pair<String, Object>> stateChanges) {
        HashMap<String, Object> newState;
        if (states.isEmpty()) {
            newState = new HashMap<>();
            stateI = 0;
        } else {
            //Do not add state if no changes are made
            if (!doesStateChange(stateChanges)) {
                return;
            }
            newState = (HashMap<String, Object>) (states.get(stateI).clone());
            clearNextStates(stateI + 1);
            //Remove oldest state if at capacity
            if (states.size() >= MAX_STATES) {
                states.removeFirst();
            } else {
                stateI++;
            }
        }
        for (Pair<String, Object> entry: stateChanges) {
            newState.put(entry.getKey(), entry.getValue());
        }
        states.add(newState);
        sendStateToSubs();
    }

    private boolean doesStateChange(ArrayList<Pair<String, Object>> stateChanges) {
        HashMap<String, Object> currentState = states.get(stateI);
        for (Pair<String, Object> entry: stateChanges) {
            if (!currentState.containsKey(entry.getKey())) {
                return true;
            }
            if ((currentState.get(entry.getKey()) == null && entry.getValue() != null) ||
                    (!currentState.get(entry.getKey()).equals(entry.getValue()))) {
                return true;
            }
        }
        return false;
    }

    private void sendStateToSubs() {
        for (Consumer<HashMap<String, Object>> subscriber: subscribers.values()) {
            subscriber.accept(states.get(stateI));
        }
    }

    private void clearNextStates(int clearStartI) {
        int numRemoves = states.size() - clearStartI;
        for (int i = 0; i < numRemoves; i++) {
            states.removeLast();
        }
    }

    public boolean hasNextState() {
        return (stateI < states.size() - 1);
    }

    public boolean hasPrevState() {
        return (stateI > 0);
    }

    public void toNextState() throws Exception {
        if (hasNextState()) {
            stateI++;
            sendStateToSubs();
        } else {
            throw new Exception("Attempted to go to next state without next states");
        }
    }

    public void toPrevState() throws Exception {
        if (hasPrevState()) {
            stateI--;
            sendStateToSubs();
        } else {
            throw new Exception("Attempted to go to prev state without prev states");
        }
    }

    //Add lambda to listen to state changes, invoke with current state immediately
    public String subscribe(Consumer<HashMap<String, Object>> subscriber) {
        String subKey = UUID.randomUUID().toString();
        subscribers.put(subKey, subscriber);
        subscriber.accept(states.get(stateI));
        return subKey;
    }

    public void unsubscribe(String subKey) {
        subscribers.remove(subKey);
    }
}
