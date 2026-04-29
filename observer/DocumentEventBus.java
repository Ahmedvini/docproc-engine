import java.util.ArrayList;
import java.util.List;

public class DocumentEventBus {
    private final List<DocumentObserver> observers = new ArrayList<>();
    public void subscribe(DocumentObserver o)   { observers.add(o);    }
    public void unsubscribe(DocumentObserver o) { observers.remove(o); }
    public void notify(String event)            { notify(event, null); }
    public void notify(String event, Object data) {
        for (DocumentObserver o : observers) o.onEvent(event, data);
    }
}
