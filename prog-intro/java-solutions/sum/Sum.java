public class Sum {
    public static void main(String[] args) {
        int sum = 0;
        for (int i = 0; i < args.length; i++) {
            String line = args[i].strip();
            int pos1 = 0;
            int pos2 = 0;
            while (pos2 < line.length()) {
                if (!(Character.isWhitespace(line.charAt(pos2)))) {
                    pos2++;
                } else {
                    sum += Integer.parseInt(line.substring(pos1, pos2));
                    while (Character.isWhitespace(line.charAt(pos2))) {
                        pos2++;
                    }
					pos1 = pos2;
                }
            }
            if (!(line.isEmpty())) {
                sum += Integer.parseInt(line.substring(pos1, pos2));
            }
        }
        System.out.println(sum);
    }
}

