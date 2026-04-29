public interface Command {
    Object execute();
    void   undo();
    String getDescription();
}
