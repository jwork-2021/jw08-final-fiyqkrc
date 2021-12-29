package mazeGenerator;

public class Main {
    public static void main(String[] args) {
        MazeGenerator mazeGenerator = new MazeGenerator(50);
        mazeGenerator.generateMaze();

        System.out.println("RAW MAZE\n" + mazeGenerator.getRawMaze());
        System.out.println("SYMBOLIC MAZE\n" + mazeGenerator.getSymbolicMaze());
    }
}