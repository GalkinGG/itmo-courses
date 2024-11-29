package markup;

import java.util.List;

public class Strikeout extends TextDecorator {

    public Strikeout(List<TextFormatting> list) {
        super(list, "~", "\\textst{");
    }

}
