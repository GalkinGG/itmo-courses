import java.util.*;

public class Equidistant {

    private static int n;
    private static int m;
    private static boolean visited[];
    private static List<Integer>[] g;
    private static Set<Integer> teamCities;
    private static int maxDepthOfTeamCity;
    private static int midwayVertex;
    private static int midwayDepth;
    private static boolean checkMode;
    private static boolean success = true;
    private static List<Integer> currentLongestWay = new ArrayList<>();

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        n = in.nextInt();
        m = in.nextInt();
        visited = new boolean[n];
        g = new ArrayList[n];
        for (int i = 0; i < n; ++i) {
            g[i] = new ArrayList<>();
        }
        for (int i = 0; i < n - 1; ++i) {
            int v, u;
            v = in.nextInt() - 1;
            u = in.nextInt() - 1;
            g[v].add(u);
            g[u].add(v);
        }
        teamCities = new HashSet<>();
        int firstCity = Integer.MIN_VALUE;
        for (int i = 0; i < m; ++i) {
            int c = in.nextInt() - 1;
            if (firstCity == Integer.MIN_VALUE) {
                firstCity = c;
            }
            if (m == 1) {
                System.out.println("YES");
                System.out.println(firstCity + 1);
                return;
            }
            teamCities.add(c);
        }
        dfs(firstCity, 0);
        checkMode = true;
        visited = new boolean[n];
        dfs(midwayVertex, midwayDepth);
        if (success) {
            System.out.println("YES");
            System.out.println(midwayVertex + 1);
        } else {
            System.out.println("NO");
        }
    }

    private static boolean dfs(int v, int curDepth) {
        visited[v] = true;
        currentLongestWay.add(v);
        if (teamCities.contains(v)) {
            if (!checkMode) {
                if (curDepth > maxDepthOfTeamCity) {
                    maxDepthOfTeamCity = curDepth;
                    int midwayIdx = currentLongestWay.size() / 2;
                    midwayVertex = currentLongestWay.get(midwayIdx);
                    midwayDepth = maxDepthOfTeamCity - (currentLongestWay.size() - midwayIdx - 1);
                    currentLongestWay.clear();
                }
            } else {
                if (curDepth != maxDepthOfTeamCity) {
                    success = false;
                }
            }
        }
        for (Integer u : g[v]) {
            if (!visited[u]) {
                dfs(u, curDepth + 1);
            }
        }
        return true;
    }

}


