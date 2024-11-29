package markup;

import java.util.List;

public abstract class Markup implements TextFormatting{
    private final List<TextFormatting> markText;
    
    protected Markup(List<TextFormatting> list) {
        markText = list;
    }

    @Override
    public void toMarkdown(StringBuilder sb) {
        for (TextFormatting markup : markText) {
            markup.toMarkdown(sb);
        }
    }

    @Override
    public void toTex(StringBuilder sb) {
        for (TextFormatting markup: markText) {
            markup.toTex(sb);
        }
    }

}
