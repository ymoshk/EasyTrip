package cache;

import com.google.gson.Gson;
import connection.DataEngine;
import itinerary.Itinerary;
import model.itinerary.ItineraryModel;
import model.user.GuestUser;
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

    public void addNewItinerary(Itinerary itinerary) {
        try {
            if (this.memory.size() >= CACHE_CAPACITY) {
                removeOldestItinerary();
            }

            this.memory.put(itinerary.getItineraryId(), itinerary);
            this.queue.offer(new AbstractMap.SimpleImmutableEntry<>(
                    itinerary.getItineraryId(),
                    LocalTime.now()));

            saveItinerary(itinerary, false);
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
                    this.addNewItinerary(itinerary);
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


    private void saveItinerary(Itinerary itinerary, boolean isUpdate) {
        try {
            Gson gson = new Gson();

            // TODO - delete the user creation
            User user = new GuestUser();
            DataEngine.getInstance().addUser(user);

            ItineraryModel model = new ItineraryModel(itinerary.getItineraryId(),
                    gson.toJson(itinerary), user);

            if (isUpdate) {
                DataEngine.getInstance().updateItinerary(model);

            } else {
                DataEngine.getInstance().saveItinerary(model);
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void updateData(boolean forceUpdate) {
        try {
            Queue<Map.Entry<String, LocalTime>> newQueue = new LinkedList<>();

            this.queue.forEach(pair -> {
                if (forceUpdate || pair.getValue().plusMinutes(UPDATE_INTERVAL).isAfter(LocalTime.now())) {
                    saveItinerary(this.memory.get(pair.getKey()), true);
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
