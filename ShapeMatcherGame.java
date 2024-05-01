package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

//main class of the game extending JFrame to use swing components
public class ShapeMatcherGame extends JFrame
{
    private Shape currentShape; //holds the currently displayed shape
    private String currentShapeType; //description of the current shape
    private JLabel statusLabel; //label to display messages like "Correct!" or "Try again!"
    private JLabel scoreLabel; //label to display the current score
    private JLabel timerLabel; //label to display the countdown timer
    private JLabel livesLabel; //label to display the number of remaining lives
    private GamePanel gamePanel; //custom panel for drawing shapes
    private Timer timer; //timer for countdown in each round
    private int score = 0; //variable to keep track of the players score
    private int lives; //variable to keep track of the player's remaining lives
    private int timeLeft; //variable to keep track of the time left for answering

    //constructor to set up the game
    public ShapeMatcherGame()
    {
        selectDifficulty(); //prompts the user to select the difficulty

        this.setTitle("Shape Matching Game"); //sets the window title
        this.setSize(800, 600); //sets the window size
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //closes the program when the window is closed
        this.setLayout(new BorderLayout()); //uses BorderLayout for arranging panels

        gamePanel = new GamePanel();
        this.add(gamePanel, BorderLayout.CENTER); //adds the panel for drawing shapes to the center

        JPanel buttonPanel = new JPanel(); //panel for holding shape buttons
        String[] shapes = {"Rectangle", "Circle", "Square", "Trapezoid"};
        for (String shape : shapes)
        {
            JButton button = new JButton(shape);
            button.addActionListener(this::shapeButtonClicked); //adds action listener for button clicks
            buttonPanel.add(button); //adds each button to the panel
        }
        this.add(buttonPanel, BorderLayout.SOUTH); //moves buttons to the bottom for answer choices

        JPanel infoPanel = new JPanel(new GridLayout(1, 4)); //displays game info
        statusLabel = new JLabel("Choose the correct shape.", SwingConstants.CENTER);
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        timerLabel = new JLabel("Time left: " + timeLeft + "s", SwingConstants.CENTER);
        livesLabel = new JLabel("Lives: " + lives, SwingConstants.CENTER);

        //labels for game info
        infoPanel.add(statusLabel);
        infoPanel.add(scoreLabel);
        infoPanel.add(timerLabel);
        infoPanel.add(livesLabel);

        this.add(infoPanel, BorderLayout.NORTH); //moves the game info to the top of the screen

        setupTimer(); //start timer
        this.setVisible(true);
    }

    //difficulty selector and dialog box
    private void selectDifficulty()
    {
        String[] options = {"Easy", "Normal", "Hard"};
        int response = JOptionPane.showOptionDialog(null, "Select Difficulty", "Difficulty Selection",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        switch (response)
        {
            case 0: //easy difficulty
                timeLeft = 10; //easy mode time
                lives = 5; //easy mode lives
                break;
            case 2: //hard difficulty
                timeLeft = 3; //hard mode time
                lives = 1; //hard mode lives
                break;
            case 1: //normal difficulty
            default:
                timeLeft = 5; //normal mode time
                lives = 3; //normal mode lives
                break;
        }
    }

    //handles the shape button clicks to identify the shape
    private void shapeButtonClicked(ActionEvent e)
    {
        if (!timer.isRunning()) return; //ignores clicks if the game is over

        String selectedShape = ((JButton) e.getSource()).getText();
        if (selectedShape.equals(currentShapeType))
        {
            score++;
            scoreLabel.setText("Score: " + score);
            statusLabel.setText("Correct! It's a " + currentShapeType + "!");
            resetTimer();
            gamePanel.drawRandomShape();
        }

        else
        {
            lives--;
            livesLabel.setText("Lives: " + lives);
            statusLabel.setText("Incorrect, try again!");
            if (lives <= 0)
            {
                gameOver("No lives left! Game Over!");
            }
        }
    }

    //sets up the game timer with a 1 second delay between ticks
    private void setupTimer()
    {
        timer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time left: " + timeLeft + "s");
            if (timeLeft <= 0)
            {
                timer.stop();
                gameOver("Time's up! Game Over!");
            }
        });
        timer.start();
    }

    //resets the timer for a new question
    private void resetTimer()
    {
        timeLeft = 5; //resets the timer based on the selected difficulty
        timer.restart();
    }

    //ends the game and disables further interaction
    private void gameOver(String message)
    {
        statusLabel.setText(message);
        timer.stop();
        disableComponents(this.getContentPane()); //disables all components to prevent further interaction
    }

    //disables all components in a container
    private void disableComponents(Container container)
    {
        for (Component component : container.getComponents())
        {
            component.setEnabled(false);
            if (component instanceof Container)
            {
                disableComponents((Container) component);
            }
        }
    }

    //class for the panel where shapes are drawn
    class GamePanel extends JPanel
    {
        private Color shapeColor; //color of the shapes

        public GamePanel()
        {
            this.setPreferredSize(new Dimension(600, 400)); //sets the preferred size for the panel
            drawRandomShape(); //draws a random shape
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g); //calls the method that handles basic painting
            Graphics2D g2 = (Graphics2D) g; //sets the graphics object to Graphics2D
            g2.setColor(shapeColor); //sets the color for drawing the shape
            g2.fill(currentShape); //fills the shape with the set color
        }

        //randomly selects a shape and its color
        public void drawRandomShape()
        {
            Random rand = new Random(); //creates a random generator
            int shapeType = rand.nextInt(4); //generates a random integer to decide the shape
            int x = 250; //x coordinate for the shape
            int y = 150; //y coordinate for the shape
            int size = 100; //size for the shape
            shapeColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)); //random color for the shape

            switch (shapeType)
            {
                case 0: //rectangle
                    currentShape = new Rectangle2D.Float(x, y, size * 2, size); //creates the rectangle
                    currentShapeType = "Rectangle"; //sets the shape type as rectangle for the answer
                    break;
                case 1: //circle
                    currentShape = new Ellipse2D.Float(x, y, size, size); //creates the circle
                    currentShapeType = "Circle"; //sets the shape type as circle for the answer
                    break;
                case 2: //square
                    currentShape = new Rectangle2D.Float(x, y, size, size); //creates the square
                    currentShapeType = "Square"; //sets the shape type as square for the answer for the answer
                    break;
                case 3: //trapezoid
                    Polygon trapezoid = new Polygon(); //creates a polygon to represent a trapezoid
                    trapezoid.addPoint(x, y); //adds the top left point
                    trapezoid.addPoint(x + 200, y); //adds the top right point (wider for trapezoid)
                    trapezoid.addPoint(x + 150, y + size); //adds the bottom right point
                    trapezoid.addPoint(x + 50, y + size); //adds the bottom left point
                    currentShape = trapezoid; //creates the trapezoid
                    currentShapeType = "Trapezoid"; //sets the shape type as trapezoid for the answer
                    break;
            }

            repaint(); //calls repaint to redraw the panel with the new shape
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run() {
                new ShapeMatcherGame(); //starts the game by creating an instance of ShapeMatcherGame
            }
        });
    }
}
