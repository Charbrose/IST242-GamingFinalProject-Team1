import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer; 
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


enum ShapeType { // set up and creates the enum with different types of shapes  
    RECTANGLE, // added for level one
    CIRCLE, //^
    TRIANGLE, //^
    TRAPEZOID, // added for level two
    PENTAGON, //^
    HEXAGON //^
}

// declares and creates constants width, height, speed, time limit, score, level 
public class ShapeMatching extends JFrame implements KeyListener {
private static final int WIDTH = 500;
private static final int HEIGHT = 500;
private static final int PLAYER_WIDTH = 50;
private static final int PLAYER_HEIGHT = 50;
private static final int OBSTACLE_WIDTH = 20;
private static final int OBSTACLE_HEIGHT = 20; 
private static final int PROJECTILE_WIDTH = 50; // edited from 5 to 50 in order to align with obstacle dimensions
private static final int PROJECTILE_HEIGHT = 50; // height of the projectiles 
private static final int PLAYER_SPEED = 25; // player speed when going left or right with arrows 
private int OBSTACLE_SPEED = 3; // drop speed of obstacle is 
private static final int PROJECTILE_SPEED = 15;
private int SHAPE_PROMPT_TIME_LIMIT = 20000; // added to fix timer bug
private static final int LEVEL_THRESHOLD_SCORE = 100;
private long lastPromptTime = 0; // ^
private static final String[] SHAPE_NAMES = {"Rectangle", "Circle", "Triangle"}; // added
private int score = 0;
private int currentLevel = 1;

// variables from jframe to connect and link to the game
private JPanel gamePanel;
private JLabel scoreLabel;
private JLabel correctLabel;
private JLabel levelLabel;
private Timer timer;
private boolean isGameOver;
private int playerX, playerY;
private int projectileX, projectileY;
private int currentShapeIndex;
private boolean isProjectileVisible;
private boolean isFiring;
private List<Obstacle> obstacles;
private Image pointerImage;
private Image spriteTriangle;
private Image spriteSheet;
private Image spriteRectangle;
private Image spriteCircle;
private Image scaledPointerImage;
private Image spriteHexagon;
private Image spriteTrapezoid;
private Image spritePentagon;
private int spriteWidth;
private int spriteHeight;

// Inner class representing obstacles
class Obstacle { // added
    int x;
    int y;
    ShapeType shapeType;

    public Obstacle(int x, int y, ShapeType shapeType) { //added
        this.x = x;
        this.y = y;
        this.shapeType = shapeType;
    }
}

 // Constructor for the game
    public ShapeMatching() {
    // set up title of game inside the frame
    setTitle("Shape and Number Game!");
    setSize(WIDTH, HEIGHT);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);

    gamePanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }
    };
    levelLabel = new JLabel("Level: " + currentLevel);
    levelLabel.setBounds(10,50,100,20);
    levelLabel.setForeground(Color.RED);
    gamePanel.add(levelLabel);

    obstacles = new ArrayList<>();

    try {
        pointerImage = ImageIO.read(new File("pointer.png")); // links together custom image to pointer 
        spriteCircle = ImageIO.read(new File("circle.png")); // links together custom image to circle 
        spriteTriangle = ImageIO.read(new File("triangle.png")); // links together custom image to triangle 
        spriteRectangle = ImageIO.read(new File("rectangle.png")); // links together custom image to rectangle
        spriteHexagon = ImageIO.read(new File("Hexagon.png")); // links together custom image to hexagon  
        spriteTrapezoid = ImageIO.read(new File("Trapezoid.png")); // links together custom image to trapezoid 
        spritePentagon = ImageIO.read(new File("Pentagon.png")); // links togther custom image to pentagon 

        scaledPointerImage = pointerImage.getScaledInstance(PLAYER_WIDTH, PLAYER_HEIGHT, Image.SCALE_DEFAULT); // sets up a player pointer scale 
        spriteWidth = OBSTACLE_WIDTH; 
        spriteHeight = OBSTACLE_HEIGHT;
    } catch (IOException ex) {
        ex.printStackTrace();
    }

    // sets score line position and color 
    scoreLabel = new JLabel("Score: 0");
    scoreLabel.setBounds(10, 10, 100, 20);
    scoreLabel.setForeground(Color.BLUE);
    gamePanel.add(scoreLabel);

    // adds/creates game panel popup 
    add(gamePanel);
    gamePanel.setFocusable(true);
    gamePanel.addKeyListener(this);

    // x and y value postions of the player 
    playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
    playerY = HEIGHT - PLAYER_HEIGHT - 20;
    projectileX = playerX + PLAYER_WIDTH / 2 - PROJECTILE_WIDTH / 2;
    projectileY = playerY;
    isProjectileVisible = false;
    isGameOver = false;
    isFiring = false;

    // creates timer class and then screen prompt for game over 
    timer = new Timer(20, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isGameOver) {
                update();
                gamePanel.repaint();
            }
        }
    });
    timer.start();

    correctLabel = new JLabel(); // added for "correct" label instead of pausing game with JOptionframe
    correctLabel.setBounds(10, 30, 200, 20);
    gamePanel.add(correctLabel);

    currentShapeIndex = 0; //added shape index to lines of code 
}

// method to diplay the next shape 
private void promptNextShape() {
    if (currentShapeIndex < SHAPE_NAMES.length) {
        String currentShape = SHAPE_NAMES[currentShapeIndex];
        JOptionPane.showMessageDialog(ShapeMatching.this, "Strike the " + currentShape);
        currentShapeIndex++;
        lastPromptTime = System.currentTimeMillis();
        System.out.println("Current shape index: " + currentShapeIndex);
        gamePanel.requestFocusInWindow(); // brings back the focus onto the JFrame
    }
}

// method to move to the next level two
private void moveToLevel2() {
    System.out.println("Moving to level 2!"); // prints on screen the next level name 
    OBSTACLE_SPEED = 7; // increase the set obstacles by seven
    SHAPE_PROMPT_TIME_LIMIT = 15000; // creates a time limit for how long the game runs for 
    currentLevel = 2; // sets up and establishes the game is running level 2 and not level 1
    levelLabel.setText("Level: 2");
    JOptionPane.showMessageDialog(this, "Welcome to level 2!");
    currentShapeIndex = 0;
}

// method to draaw the game components 
private void draw(Graphics g) { // creates customization color for background color 
    // displays the game background 
    g.setColor(Color.WHITE); 
    g.fillRect(0, 0, WIDTH, HEIGHT);

    // Draws the  player ship
    if (scaledPointerImage != null) {
        g.drawImage(scaledPointerImage, playerX, playerY, null);
    }

    // Draw each obstacles (shapes)
    for (Obstacle obstacle : obstacles) {
        switch (obstacle.shapeType) {
            case RECTANGLE:
                g.drawImage(spriteRectangle, obstacle.x, obstacle.y, null);
                break;
            case CIRCLE:
                g.drawImage(spriteCircle, obstacle.x, obstacle.y, null);
                break;
            case TRIANGLE:
                g.drawImage(spriteTriangle, obstacle.x, obstacle.y, null);
                break;
            case HEXAGON:
                g.drawImage(spriteHexagon, obstacle.x, obstacle.y, null);
                break;
            case TRAPEZOID:
                g.drawImage(spriteTrapezoid, obstacle.x, obstacle.y, null);
                break;
            case PENTAGON:
                g.drawImage(spritePentagon, obstacle.x, obstacle.y, null);
                break;
        }
    }

    // Draw projectile
    if (isProjectileVisible) {
        g.setColor(Color.BLUE);
        g.fillRect(projectileX, projectileY, PROJECTILE_WIDTH, PROJECTILE_HEIGHT);
    }

    // Draw score label
    scoreLabel.setText("Score: " + score);

    // Draw game over message
    if (isGameOver) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Game Over!", WIDTH / 2 - 80, HEIGHT / 2);
    }
}

// method to update when the game starts 
private void update() {
    if (!isGameOver) {
        // Update obstacle positions
        for (int i = 0; i < obstacles.size(); i++) {
            obstacles.get(i).y += OBSTACLE_SPEED;
            if (obstacles.get(i).y > HEIGHT) {
                obstacles.remove(i);
                i--;
            }
        }
        // Generate code for new obstacles
        if (Math.random() < 0.02) {
            boolean overlap; //added
            do { //added
                int obstacleX = (int) (Math.random() * (WIDTH - OBSTACLE_WIDTH));
                ShapeType shapeType = getRandomShapeType(1); //edited
                overlap = false; // Added
                //obstacles.add(new Obstacle(obstacleX, 0, shapeType));
                for (Obstacle obstacle : obstacles) { // added
                    Rectangle newObstacleRect = new Rectangle(obstacleX, 0, OBSTACLE_WIDTH, OBSTACLE_HEIGHT); //added
                    Rectangle existingObstacleRect = new Rectangle(obstacle.x, obstacle.y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT); //added
                    if (newObstacleRect.intersects(existingObstacleRect)) {
                        overlap = true;
                        break; //added
                    }
                }
                if (!overlap) {//Repeat until no overlap
                    obstacles.add(new Obstacle(obstacleX, 0, shapeType));
                }
            } while (overlap);
        }

        // Move projectiles 
        if (isProjectileVisible) {
            projectileY -= PROJECTILE_SPEED;
            if (projectileY < 0) {
                isProjectileVisible = false;
            }
        }

        // Check collision between projectile and obstacles
        if (isProjectileVisible) { // added
            Rectangle projectileRect = new Rectangle(projectileX, projectileY, PROJECTILE_WIDTH, PROJECTILE_HEIGHT);
            for (int i = 0; i < obstacles.size(); i++) {
                Rectangle obstacleRect = new Rectangle(obstacles.get(i).x, obstacles.get(i).y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
                if (projectileRect.intersects(obstacleRect)) {
                    ShapeType struckShape = obstacles.get(i).shapeType;
                    String currentShape = SHAPE_NAMES[currentShapeIndex - 1];
                    if (struckShape != null && struckShape.toString().equalsIgnoreCase(currentShape)) {
                        // Correct shape struck, add points and remove the obstacle
                        obstacles.remove(i);
                        score += 10;
                        correctLabel.setText("Correct!");
                    } else {
                        // Incorrect shape struck, display message
                        String obstacleName = struckShape != null ? struckShape.toString() : "Unknown Shape";
                        JOptionPane.showMessageDialog(this, "Incorrect! This is a " + obstacleName);
                    }
                    isProjectileVisible = false;
                    break;
                }
            }
        }
        // Check if it's time to move to level two 
        if (currentLevel == 1 && score >= LEVEL_THRESHOLD_SCORE) {
            moveToLevel2();
            currentLevel = 2;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPromptTime >= SHAPE_PROMPT_TIME_LIMIT) {
            if (currentShapeIndex < SHAPE_NAMES.length) {
                promptNextShape();
            } else {
                if (currentLevel == 1 && score >= LEVEL_THRESHOLD_SCORE) {
                    if (score % 10 == 0) { // Assuming score increases by 10 for each correct shape
                        moveToLevel2();
                    } else {
                        // Perhaps end the game or repeat level 1
                        currentShapeIndex = 0;  // Optionally reset shapes for endless level 1 mode
                        promptNextShape();
                    }
                } else {
                    currentShapeIndex = 0;
                    promptNextShape();
                }
            }
        }
    }
}


private ShapeType getStruckShape(int keyCode) { // added
    switch (keyCode) {
        case KeyEvent.VK_R: // Rectangle
            return ShapeType.RECTANGLE;
        case KeyEvent.VK_C: // Circle
            return ShapeType.CIRCLE;
        case KeyEvent.VK_T: // Triangle
            return ShapeType.TRIANGLE;
        case KeyEvent.VK_H: // Hexagon
            return ShapeType.HEXAGON;
        case KeyEvent.VK_Y: // Trapezoid
            return ShapeType.TRAPEZOID;
        case KeyEvent.VK_P: // Pentagon
            return ShapeType.PENTAGON;
        default:
            return null;
    }
}

// random generate of shapes displayed on the screen
private ShapeType getRandomShapeType(int level) {
    if (level == 1) {
        int randomIndex = new Random().nextInt(3); // 0, 1, or 2
        switch (randomIndex) {
            case 0:
                return ShapeType.RECTANGLE;
            case 1:
                return ShapeType.CIRCLE;
            case 2:
                return ShapeType.TRIANGLE;
            default:
                return ShapeType.RECTANGLE; // Default to rectangle
        }
    } else if (level == 2) {
        int randomIndex = new Random().nextInt(3); // 0, 1, or 2
        switch (randomIndex) {
            case 0:
                return ShapeType.HEXAGON;
            case 1:
                return ShapeType.TRAPEZOID;
            case 2:
                return ShapeType.PENTAGON;
            default:
                return ShapeType.HEXAGON; // Default shape to display is hexagon
        }
    } else {
        return null; // Handle other levels if needed
    }
}

@Override
public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();
    if (keyCode == KeyEvent.VK_SPACE) {
        isFiring = true;
    } else { // Handle player movement
        // Your existing code for player movement
        if (keyCode == KeyEvent.VK_LEFT && playerX > 0) {
            playerX -= PLAYER_SPEED;
        } else if (keyCode == KeyEvent.VK_RIGHT && playerX < WIDTH - PLAYER_WIDTH) {
            playerX += PLAYER_SPEED;
        }
    }
    ShapeType struckShape = getStruckShape(keyCode);
    if (struckShape != null) {
        // Check if the struck shape matches the current prompt
        String currentShape = SHAPE_NAMES[currentShapeIndex - 1];
        if (struckShape.toString().equalsIgnoreCase(currentShape)) {
            // Correct shape struck, proceed to the next prompt
            correctLabel.setText("Correct!");
        } else {
            // Incorrect shape struck
            JOptionPane.showMessageDialog(this, "Incorrect! Please strike the " + currentShape);
        }
    }
}

@Override
public void keyTyped(KeyEvent e) {
}

@Override
public void keyReleased(KeyEvent e) {
    int keyCode = e.getKeyCode(); //added
    if (keyCode == KeyEvent.VK_SPACE) {
        // Fire the projectile when space is released
        fireProjectile();
        isFiring = false;
    }
}

private void fireProjectile() { //added
    // Add logic here to fire the projectile
    projectileX = playerX + PLAYER_WIDTH / 2 - PROJECTILE_WIDTH / 2;
    projectileY = playerY;
    isProjectileVisible = true;
}

public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
            new ShapeMatching().setVisible(true);
        }
    });
}
}
