import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Visualize extends JComponent {
    private static int diameter = 20;

//	public static void main(String[] args) {
//		JFrame window = new JFrame("PathFinding");
//		window.setSize(600,600);
//		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		window.add(new Visualize());
//		window.setVisible(true);
//	}

    public static int[][] readFile() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("C:\\Users\\Mohammed\\Dropbox\\Computer science\\Java\\PathFinding\\src\\Map.txt"));

            //Declaring Scanner
            ArrayList<String> rawData = new ArrayList<String>();
            String nextLine = "";

            //Reading the Map into an Arraylist
            while ((nextLine = reader.readLine()) != null) {
                nextLine = nextLine.replace(" ", "");
                rawData.add(nextLine);
            }
            reader.close();

            //Creating the 2D array
            int[][] rawMap = new int[rawData.size()][rawData.get(0).length()];
            for (int i = 0; i < rawData.size(); i++) {
                //Some fancy work to read the text into the array properly
                int counter = 0;
                for (int j = 0; j < rawData.get(i).length(); j++) {
                    rawMap[i][counter] = rawData.get(i).charAt(j) - 48;
                    counter++;
                }
            }

            return rawMap;

        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }

    protected void paintComponent(Graphics g) {
        int[][] rawMap = readFile();
        ArrayList<String> path = PathFinding.findPath(rawMap, "0 0", "0 1");

        Color drawColor = new Color(0, 0, 0);
        for (int y = 0; y < rawMap.length; y++) {
            for (int x = 0; x < rawMap[0].length; x++) {
                switch (rawMap[y][x]) {
                    case 0:
                        drawColor = new Color(150, 150, 150);
                        break;
                    case 1:
                        drawColor = Color.BLACK;
                        break;
                    case 2:
                        drawColor = Color.BLUE;

                    default:
                        break;
                }
                g.setColor(drawColor);
                if (path.contains(String.valueOf(y) + " " + String.valueOf(x))) {
                    g.setColor(Color.GREEN);
                }
                g.fillOval(diameter + (diameter + diameter / 5) * x, diameter + (diameter + diameter / 5) * y, diameter, diameter);
            }
        }


    }

}
