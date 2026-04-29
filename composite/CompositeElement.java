import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CompositeElement extends DocumentElement {
    protected final List<DocumentElement> children = new ArrayList<>();

    protected CompositeElement() { super(); }
    protected CompositeElement(CompositeElement src) {
        super(src);
        for (DocumentElement child : src.children)
            this.children.add(child.deepCopy());
    }

    public CompositeElement add(DocumentElement element) { children.add(element); return this; }
    public void             remove(DocumentElement el)   { children.remove(el);               }
    public List<DocumentElement> getChildren()           { return Collections.unmodifiableList(children); }
}
