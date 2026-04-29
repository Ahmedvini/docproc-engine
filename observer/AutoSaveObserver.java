public class AutoSaveObserver implements DocumentObserver {
    @Override public void onEvent(String event, Object data) {
        System.out.printf("  [AutoSave]    event=%-26s -> auto-saving ...%n", "'" + event + "'");
    }
}
