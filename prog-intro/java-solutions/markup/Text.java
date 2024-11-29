package markup;

public class Text implements TextFormatting {
    private String text = "";

    public Text(String txt) {
        text = txt;
    }

    @Override
    public void toMarkdown(StringBuilder sb) {
        sb.append(text);
    }

    @Override
    public void toTex(StringBuilder sb) {
        sb.append(text);
    }
}
