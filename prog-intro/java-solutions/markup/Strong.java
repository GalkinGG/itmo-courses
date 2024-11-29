package markup;

import java.util.List;

public class Strong extends TextDecorator {

    public Strong(List<TextFormatting> list) {
        super(list, "__", "\\textbf{");
    }

}
