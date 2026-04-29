public class AddElementCommand implements Command {
    private final CompositeElement parent;
    private final DocumentElement  element;

    public AddElementCommand(CompositeElement parent, DocumentElement element) {
        this.parent = parent; this.element = element;
    }
    @Override public DocumentElement execute() { parent.add(element); return element; }
    @Override public void            undo()    { parent.remove(element); }
    @Override public String getDescription()   { return "Add("+element.getClass().getSimpleName()+")"; }
}
