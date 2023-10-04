//Grant Wang
//CST 283, Fall Semester
//Game Project
//This is a basic Snake game utilizing rectangles to animate the snake as it travels across the plane
//Player will input directions via WASD or arrow keys

import java.util.ArrayList;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;
public class Snake extends Application {
    public Random rand = new Random();
    public int foodX, foodY, width, height, squareSize, score;
    public boolean dead, eaten;
    public Timeline timeline;
    public Directions direction = Directions.left;
    public ArrayList<SquareTile> Snake;
    public Rectangle[][] tiles;
    public GridPane gridPane;
    public static void main(String args[]) {
        launch(args);
    }
    public void start(Stage primaryStage){
        dead = false; //worm isn't dead
        width = 20; //box dimensions
        height = 20;
        Snake = new ArrayList<>(); //for worm
        eaten = true; //check if the food has been eaten
        score = 0; //score, pretty self explanatory
        squareSize = 20; //square dimensions
        gridPane = new GridPane();
        tiles = new Rectangle[width][height]; //2D array of rectangles

        timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), t -> { //Timeline for animating the Snake
            generateFood();
            updateUI();
            game();
            primaryStage.setTitle("Snake - Score: " +score);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        generateFood();
        for (int x = 0; x < 3; x++) {
            Snake.add(new SquareTile(x + 9, 10)); //create the initial snake
        }

        for (int x = 0; x < width; x++) { //initialize the labels
            for (int y = 0; y < width; y++) {
                tiles[x][y] = new Rectangle(width, height);
                tiles[x][y].setFill(Color.color(0,0,0)); //initialize them all as black tiles
                if ((foodX == x) && (foodY == y)) {
                    tiles[x][y].setFill(Color.color(1,0,0)); //set the food color
                }
                for (int z = 0; z < Snake.size(); z++) {
                    if (Snake.get(z).x == x && Snake.get(z).y == y) {
                        tiles[x][y].setFill(Color.color(0,1,0)); //set up the snake
                    }
                }
                gridPane.add(tiles[x][y], x, y);
            }
        }

        Scene scene = new Scene(gridPane, width * squareSize, height * squareSize);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
            if (key.getCode() == KeyCode.W || key.getCode() == KeyCode.UP ) { direction = Directions.up;}
            if (key.getCode() == KeyCode.A || key.getCode() == KeyCode.LEFT) { direction = Directions.left; }
            if (key.getCode() == KeyCode.S || key.getCode() == KeyCode.DOWN) { direction = Directions.down; }
            if (key.getCode() == KeyCode.D || key.getCode() == KeyCode.RIGHT) { direction = Directions.right; }
        });//Check for key actions
        game();
        timeline.play();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Snake - Score: " +score);
        primaryStage.show();
    }
    public class SquareTile { //individual tile
        int x, y;
        public SquareTile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public void generateFood() {
        if (eaten) { //generate a new food if the current one has been eaten
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);
            eaten = false;
        }
    }

    public enum Directions{ //I never thought this would useful, for changing directions of the snake
        left, right, up, down;
    }

    public void dead() {
        if (dead) { //check if the snake is dead
            timeline.stop();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Snake");
            alert.setHeaderText("");
            alert.setContentText("You died with a score of: " + score);
            alert.show();
        }
    }
    public void game() {
        dead();
        for (int x = Snake.size() - 1; x >= 1; x--) { //push all snake tiles up
            Snake.get(x).x = Snake.get(x - 1).x;
            Snake.get(x).y = Snake.get(x - 1).y;
        }
        switch (direction) { //directional changes, also set the head of the snake in the direction as pressed by user
            case up:
                Snake.get(0).y--; //towards zero
                if (Snake.get(0).y < 0) { //if exceeds bounds
                    dead = true; //dead snake
                }
                break;
            case down:
                Snake.get(0).y++; //towards height
                if (Snake.get(0).y >= height) { //if exceeds bounds
                    dead = true; //dead snake
                }
                break;
            case left:
                Snake.get(0).x--; //towards zero
                if (Snake.get(0).x < 0) { //if exceeds bounds
                    dead = true; //dead snake
                }
                break;
            case right:
                Snake.get(0).x++; //towards width
                if (Snake.get(0).x >= width) { //if exceeds bounds
                    dead = true; //dead snake
                }
                break;
        }
        for (int x = 1; x < Snake.size() ; x++) { //if eats self
            if (Snake.get(0).x == Snake.get(x).x && Snake.get(0).y == Snake.get(x).y){
                dead = true;
                break;
            }
        }
        if (foodX == Snake.get(0).x && foodY == Snake.get(0).y) {
            Snake.add(new SquareTile(-1, -1)); //new square for additional part of snake that will be altered at the beginning of game()
            eaten = true;
            score++;
        }
    }
    public void updateUI() {
        for (int x = 0; x < width; x++) { //initialize the labels
            for (int y = 0; y < width; y++) {
                gridPane.getChildren().remove(tiles[x][y]);
                tiles[x][y].setFill(Color.color(0, 0, 0)); //set everything black
            }
        }
        for (SquareTile sT : Snake) {
            if (sT.x < width && sT.y >= 0 && sT.x >= 0 && sT.y < height) {
                tiles[sT.x][sT.y].setFill(Color.color(0, 1, 0)); //set that tile in the array to snake color (green)
            }
        }
        tiles[foodX][foodY].setFill(Color.color(1, 0, 0)); //set food tile to food color (red)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                gridPane.add(tiles[x][y], x, y); //add all the rectangles
            }
        }
    }
}
