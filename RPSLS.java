import java.util.Scanner;
import java.util.Random;

public class RPSLS {

    public enum Move {
        ROCK,
        PAPER,
        SCISSORS,
        LIZARD,
        SPOCK
    }

    static int winner(Move player, Move computer) {
        if (player == computer)
            return 0;

        switch (player) {
            case ROCK -> {

                if (computer == Move.SCISSORS || computer == Move.LIZARD)
                    return 1;

                else
                    return -1;
            }

            case PAPER -> {

                if (computer == Move.ROCK || computer == Move.SPOCK)
                    return 1;

                else
                    return -1;
            }

            case SCISSORS -> {

                if (computer == Move.PAPER || computer == Move.LIZARD)
                    return 1;

                else
                    return -1;
            }

            case LIZARD -> {

                if (computer == Move.SPOCK || computer == Move.PAPER)
                    return 1;

                else
                    return -1;
            }

            case SPOCK -> {

                if (computer == Move.ROCK || computer == Move.SCISSORS)
                    return 1;

                else
                    return -1;
            }

            default -> {
                return -2;
            }

        }

    }

    public static void main(String[] args) {
        int playerScore = 0, computerScore = 0, round = 1;

        Scanner sc = new Scanner(System.in);

        while (round <= 5) {

            System.out.print("Enter your move : ");
            String pMove = sc.next();

            round++;

            Move playerMove = Move.valueOf(pMove);

            Random random = new Random();

            Move computerMove = Move.values()[random.nextInt(Move.values().length)];

            System.out.println("Computer's move : " + computerMove);

            int result = winner(playerMove, computerMove);

            if (result == 1)
            {
                System.out.println("Player wins the round");
                playerScore++;
            }

            else if (result == -1)
            {
                System.out.println("Computer wins the round");
                computerScore++;
            }

        }

        if(computerScore > playerScore)
            System.out.println("Computer Wins " + computerScore + "-" + playerScore);

        else
            System.out.println("Player Wins " + playerScore + "-" + computerScore);

        sc.close();
    }
}
