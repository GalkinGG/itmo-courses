public class SumDouble {
    public static void main(String[] args) {
        double sum = 0;
        for (int i = 0; i < args.length; i++) {
            String line = args[i].strip();
            int pos1 = 0;
            int pos2 = 0;
            while (pos2 < line.length()) {
                if (!(Character.isWhitespace(line.charAt(pos2)))) {
                    pos2++;
                } else {
                    sum += Double.parseDouble(line.substring(pos1, pos2));
                    while (Character.isWhitespace(line.charAt(pos2))) {
                        pos2++;
                    }
					pos1 = pos2;
                }
            }
            if (!(line.isEmpty())) {
                sum += Double.parseDouble(line.substring(pos1, pos2));
            }
        }
        System.out.println(sum);
    }
}

