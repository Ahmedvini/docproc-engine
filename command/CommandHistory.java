import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class CommandHistory {
    private final Deque<Command> done   = new ArrayDeque<>();
    private final Deque<Command> undone = new ArrayDeque<>();

    public Object execute(Command cmd) {
        Object result = cmd.execute();
        done.push(cmd); undone.clear();
        System.out.println("  [Cmd] + execute : " + cmd.getDescription());
        return result;
    }
    public boolean undo() {
        if (done.isEmpty()) { System.out.println("  [Cmd] x Nothing to undo."); return false; }
        Command cmd = done.pop(); cmd.undo(); undone.push(cmd);
        System.out.println("  [Cmd] < undone  : " + cmd.getDescription()); return true;
    }
    public boolean redo() {
        if (undone.isEmpty()) { System.out.println("  [Cmd] x Nothing to redo."); return false; }
        Command cmd = undone.pop(); cmd.execute(); done.push(cmd);
        System.out.println("  [Cmd] > redone  : " + cmd.getDescription()); return true;
    }
    public List<String> getSummary() {
        List<String> list = new ArrayList<>();
        for (Command c : done) list.add(c.getDescription());
        return list;
    }
}
