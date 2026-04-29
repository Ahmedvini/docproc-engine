public class LivePreviewObserver implements DocumentObserver {
    @Override public void onEvent(String event, Object data) {
        System.out.printf("  [LivePreview] event=%-26s -> refreshing preview ...%n", "'" + event + "'");
    }
}
