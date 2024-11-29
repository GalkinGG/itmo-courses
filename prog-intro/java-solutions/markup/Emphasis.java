package markup;

import java.util.List;

public class Emphasis extends TextDecorator {

    public Emphasis(List<TextFormatting> list) {
        super(list, "*", "\\emph{");
    }

}
