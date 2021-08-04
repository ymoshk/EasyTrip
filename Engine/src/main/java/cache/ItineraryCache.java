package cache;

import com.google.gson.Gson;
import connection.DataEngine;
import itinerary.Itinerary;
import model.itinerary.ItineraryModel;

import java.io.Closeable;
import java.time.LocalTime;
import java.util.*;

public class ItineraryCache implements Closeable {

    private final HashMap<String, Itinerary> memory;
    private final Queue<Map.Entry<String, LocalTime>> queue;
    private final int CACHE_CAPACITY = 50;
    private final int UPDATE_INTERVAL = 15;
    private final Timer timer;

    public ItineraryCache() {
        this.memory = new HashMap<>();
        this.queue = new LinkedList<>();
        this.timer = new Timer();
        DataEngine dataEngine = DataEngine.getInstance();
        List<Itinerary> recentItinerary = mapFromModel(dataEngine.getRecentItineraries());

        recentItinerary.forEach(itinerary -> {
            memory.put(itinerary.getItineraryId(), itinerary);
        });

        updateHandler();
    }

    public void removeOldestItinerary() {
        if (!this.queue.isEmpty()) {
            String idToRemove = this.queue.poll().getKey();
            memory.remove(idToRemove);
        }
    }

    public void addNewItinerary(Itinerary itinerary) {
        if (this.memory.size() >= CACHE_CAPACITY) {
            removeOldestItinerary();
        }

        this.memory.put(itinerary.getItineraryId(), itinerary);
        this.queue.offer(new AbstractMap.SimpleImmutableEntry<>(
                itinerary.getItineraryId(),
                LocalTime.now()));

        saveItinerary(itinerary);
    }

    public Itinerary loadItineraryToCache(String id) {
        Gson gson = new Gson();
        DataEngine dataEngine = DataEngine.getInstance();
        ItineraryModel itineraryModel = dataEngine.getItinerary(id);
        Itinerary itinerary = null;

        if (itineraryModel != null) {
            itinerary = gson.fromJson(itineraryModel.getJsonData(), Itinerary.class);

            if (itinerary != null) {
                this.addNewItinerary(itinerary);
            }
        }

        return itinerary;
    }

    public Itinerary getItinerary(String id) {
        Itinerary result;

        if (!this.memory.containsKey(id)) {
            result = loadItineraryToCache(id);
        } else {
            result = this.memory.get(id);
        }

        return result;
    }

    public void saveItinerary(Itinerary itinerary) {
        Gson gson = new Gson();
        ItineraryModel model = new ItineraryModel(itinerary.getItineraryId(),
                gson.toJson(itinerary));
        DataEngine.getInstance().saveItinerary(model);
    }

    public void updateData(boolean forceUpdate) {
        this.queue.forEach(pair -> {
            if (forceUpdate || pair.getValue().plusMinutes(UPDATE_INTERVAL).isAfter(LocalTime.now())) {
                saveItinerary(this.memory.get(pair.getKey()));
                pair.setValue(LocalTime.now());
            }
        });
    }

    public void updateHandler() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                updateData(false);
            }
        };

        this.timer.scheduleAtFixedRate(timerTask, 60 * 1000, UPDATE_INTERVAL * 60 * 1000);
    }

    public List<Itinerary> mapFromModel(List<ItineraryModel> list) {
        Gson gson = new Gson();
        List<Itinerary> result = new ArrayList<>();

        list.forEach(item -> result.add(gson.fromJson(item.getJsonData(), Itinerary.class)));
        return result;
    }

    @Override
    public void close() {
        this.timer.purge();
        this.timer.cancel();
        updateData(true);
    }
}
