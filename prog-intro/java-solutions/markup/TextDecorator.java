package markup;

import java.util.List;

public abstract class TextDecorator extends Markup {

    private final String markdownFormattingDecorator;
    private final String texFormattingStart;

    public TextDecorator(List<TextFormatting> list,
                         String markdownFormattingDecorator,
                         String texFormattingStart
    ) {
        super(list);
        this.markdownFormattingDecorator = markdownFormattingDecorator;
        this.texFormattingStart = texFormattingStart;
    }

    @Override
    public void toMarkdown(StringBuilder sb) {
        sb.append(markdownFormattingDecorator);
        super.toMarkdown(sb);
        sb.append(markdownFormattingDecorator);
    }

    @Override
    public void toTex(StringBuilder sb) {
        sb.append(texFormattingStart);
        super.toTex(sb);
        sb.append("}");
    }
}
