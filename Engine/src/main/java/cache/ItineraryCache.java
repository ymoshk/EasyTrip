package cache;

import com.google.gson.Gson;
import connection.DataEngine;
import itinerary.Itinerary;
import model.itinerary.ItineraryModel;
import model.user.User;

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
            queue.add(new AbstractMap.SimpleImmutableEntry<>(itinerary.getItineraryId(), LocalTime.now()));
        });

        updateHandler();
    }

    private void removeOldestItinerary() {
        try {
            if (!this.queue.isEmpty()) {
                String idToRemove = this.queue.poll().getKey();
                memory.remove(idToRemove);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void removeItinerary(String id) {
        this.memory.remove(id);
        removeFromQueue(id);
        DataEngine.getInstance().removeItinerary(id);
    }

    private void removeFromQueue(String id) {
        Queue<Map.Entry<String, LocalTime>> newQueue = new LinkedList<>();

        for (Map.Entry<String, LocalTime> entry : this.queue) {
            if (!entry.getKey().equals(id)) {
                newQueue.offer(entry);
            }
        }

        this.queue.clear();
        this.queue.addAll(newQueue);
    }

    public void addNewItinerary(Itinerary itinerary, User user) {
        try {
            if (this.memory.size() >= CACHE_CAPACITY) {
                removeOldestItinerary();
            }

            this.memory.put(itinerary.getItineraryId(), itinerary);
            this.queue.offer(new AbstractMap.SimpleImmutableEntry<>(
                    itinerary.getItineraryId(),
                    LocalTime.now()));

            saveItinerary(itinerary, user);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private Optional<Itinerary> loadItineraryToCache(String id) {
        try {
            Gson gson = new Gson();
            DataEngine dataEngine = DataEngine.getInstance();
            ItineraryModel itineraryModel = dataEngine.getItinerary(id);
            Itinerary itinerary = null;

            if (itineraryModel != null) {
                itinerary = gson.fromJson(itineraryModel.getJsonData(), Itinerary.class);

                if (itinerary != null) {
                    this.addNewItinerary(itinerary, itineraryModel.getUser());
                }
            }

            return Optional.ofNullable(itinerary);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Itinerary> getItinerary(String id) {
        try {
            Optional<Itinerary> result;

            if (!this.memory.containsKey(id)) {
                result = loadItineraryToCache(id);
            } else {
                result = Optional.ofNullable(this.memory.get(id));
            }

            return result;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Optional.empty();
        }
    }

    private void updateItinerary(Itinerary itinerary) {
        Gson gson = new Gson();
        ItineraryModel model = DataEngine.getInstance().getItinerary(itinerary.getItineraryId());

        if (model != null) {
            model.setJsonData(gson.toJson(itinerary));
            DataEngine.getInstance().updateItinerary(model);
        }
    }

    private void saveItinerary(Itinerary itinerary, User user) {
        try {
            Gson gson = new Gson();
            ItineraryModel model = new ItineraryModel(itinerary.getItineraryId(),
                    gson.toJson(itinerary), user);

            DataEngine.getInstance().saveItinerary(model);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void updateData(boolean forceUpdate) {
        try {
            Queue<Map.Entry<String, LocalTime>> newQueue = new LinkedList<>();

            this.queue.forEach(pair -> {
                if (forceUpdate || pair.getValue().plusMinutes(UPDATE_INTERVAL).isAfter(LocalTime.now())) {
                    updateItinerary(this.memory.get(pair.getKey()));
                    newQueue.offer(new AbstractMap.SimpleImmutableEntry<>(pair.getKey(), LocalTime.now()));
                } else {
                    newQueue.offer(pair);
                }
            });

            this.queue.clear();
            this.queue.addAll(newQueue);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void updateHandler() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                updateData(false);
            }
        };

        this.timer.scheduleAtFixedRate(timerTask, 60 * 1000, UPDATE_INTERVAL * 60 * 1000);
    }

    private List<Itinerary> mapFromModel(List<ItineraryModel> list) {
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
