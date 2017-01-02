import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

// By: Mohammed Eseifan
// Gr. 11
// Date: 12/11/13

//Main class for Breadth-First-Search
public class PathFinding {

    /**
     * Performs BFS on a map to find shortest path between two points
     * @param rawMap two dimensional array representing map to traverse
     * @param startPoint String of format "x y" representing the starting point
     * @param endPoint String of format "x y" representing the ending point
     * @return Array of strings of format "x y" representing shortest path between startPoint and endPoint
     */
    public static ArrayList<String> findPath(int[][] rawMap, String startPoint, String endPoint) {

        //TODO Change algorithm to Dijkstra's

        //Declaring variables for adjacency list
        HashMap<String, ArrayList<String>> adj = new HashMap<String, ArrayList<String>>();
        rawMap[Integer.valueOf(endPoint.split(" ")[0])][Integer.valueOf(endPoint.split(" ")[1])] = 2;
        ArrayList<String> adjList = new ArrayList<String>();

        //Populating adjacency list
        System.out.println("EndPoint: "+endPoint);
        for (int y = 0; y < rawMap.length; y++) {
            for (int x = 0; x < rawMap[0].length; x++) {
                if (rawMap[y][x] == 1) {
                    continue;
                }
                adjList.clear();

                //Checking the surroundings
                for (int yy = -1; yy < 2; yy++) {
                    for (int xx = -1; xx < 2; xx++) {
                        //If its not checking its self and not checking diagonally
                        if (!(xx == 0 && yy == 0) && Math.abs(xx) != Math.abs(yy)) {
                            //If it's within the bounds of the map
                            if (x + xx >= 0 && y + yy >= 0 && x + xx < rawMap[0].length && y + yy < rawMap.length) {
                                //If its not a wall add it to the list
                                if (rawMap[y + yy][x + xx] != 1) {
                                    adjList.add(String.valueOf(y + yy) + " " + String.valueOf(x + xx));
                                }

                            }
                        }

                    }

                }
                adj.put(String.valueOf(y) + " " + String.valueOf(x), (ArrayList<String>) adjList.clone());
            }

        }

        //Declaring needed variables for searching the graph
        HashMap<String, String> parent = new HashMap<String, String>();
        HashMap<String, Integer> level = new HashMap<String, Integer>();
        ArrayList<String> frontier = new ArrayList<String>();
        ArrayList<String> next = new ArrayList<String>();
        int i = 1;
        String target = "";

        //Initializing values
        parent.put(startPoint, "end");
        level.put(startPoint, 0);
        frontier.add(startPoint);
        ArrayList<String> finalPath = new ArrayList<String>();

        //Searching the graph
        while (frontier.size() > 0) {

            next.clear();
            //For each node in the frontier
            for (String u : frontier) {
                //Scan all of the nodes connected to the current node
                //Assign a parent and level to each node
                //If target is found break loop
                for (String v : adj.get(u)) {
                    if (level.containsKey(v) == false) {
                        level.put(v, i);
                        parent.put(v, u);
                        next.add(v);
                        int y = Integer.valueOf(v.split(" ")[0]);
                        int x = Integer.valueOf(v.split(" ")[1]);
                        if (endPoint.equals(v)) {
                            target = v;
                            break;
                        }


                    }
                }

            }
            frontier = (ArrayList<String>) next.clone();
            i++;
        }

        //If there is no path return
        if (parent.containsKey(target) == false) {
            finalPath.add("nopath");
            return finalPath;
        }

        //Retracing the path
        while (parent.get(target) != "end") {
            finalPath.add(target);
            target = parent.get(target);
        }
        finalPath.add(startPoint);
        Collections.reverse(finalPath);
        return finalPath;


    }

}
