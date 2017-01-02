import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

// By: Mohammed Eseifan
// Gr. 11
// Date: 12/11/13


public class PathFinding {


    @SuppressWarnings("unchecked")
    public static ArrayList<String> findPath(int[][] rawMap, String startpoint, String endpoint) {

        //Creating the adjacentcey list
        HashMap<String, ArrayList<String>> adj = new HashMap<String, ArrayList<String>>();
        rawMap[Integer.valueOf(endpoint.split(" ")[0])][Integer.valueOf(endpoint.split(" ")[1])] = 2;
        ArrayList<String> adjList = new ArrayList<String>();
        //ArrayList<String> debug = new ArrayList<String>();

        System.out.println("EndPoint: "+endpoint);
        for (int y = 0; y < rawMap.length; y++) {
            for (int x = 0; x < rawMap[0].length; x++) {
                if (rawMap[y][x] == 1) {
                    continue;
                }
                adjList.clear();
                //Checking the surroundings
                for (int yy = -1; yy < 2; yy++) {
                    for (int xx = -1; xx < 2; xx
                            ++) {
                        //If its not checking its self
                        if (!(xx == 0 && yy == 0) && Math.abs(xx) != Math.abs(yy)) {
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
                // debug.add(String.valueOf(y)+" "+String.valueOf(x));
            }

        }

        //Delcaring needed vars
        HashMap<String, String> parent = new HashMap<String, String>();
        HashMap<String, Integer> level = new HashMap<String, Integer>();
        ArrayList<String> frontier = new ArrayList<String>();
        ArrayList<String> next = new ArrayList<String>();
        int i = 1;
        String target = "";

        //Initializing values
        parent.put(startpoint, "end");
        level.put(startpoint, 0);
        frontier.add(startpoint);
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
                        if (endpoint.equals(v)) {
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
        finalPath.add(startpoint);
        Collections.reverse(finalPath);
        return finalPath;


    }

}
