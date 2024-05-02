import javax.swing.*;

public class Main {
    public static void main(String[] args){
        String[] options = {"Shape Matcher Game", "Quiz Controller Game", "Shape Shooter Game"}; //String array of the three game options

        //Creates dialog box that displays the game titles and descriptions and provides buttons that allow the user to select a game
        int response = JOptionPane.showOptionDialog(null,
                "Select Game:\n \nShape Matcher Game: Pick the correct name of the shape that is displayed on the screen before the timer runs out, in a series of levels that get more challenging\nQuiz Controller Game: Complete the math equations displayed on the screen for a chance to win a funny prize \nShape Shooter: Shoot the correct shape that corresponds to the shape that the player is assigned",
                "Game Selection Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        switch (response) //switch statement that runs the game selected by the user
        {
            case 0: // Game 1 selection
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run() {
                        new ShapeMatching(); // Starts the game by creating an instance of ShapeMatcherGame
                    }
                });
                break;
            case 1: // Game 2 selection
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new QuizController();
                    }
                });
                break;
            case 2: // Game 3 selection
            default:
                System.out.println("Game 3 playing");
                break;
        }
    }
}
