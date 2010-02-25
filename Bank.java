import java.util.Scanner;

/**
 *  This class initiates the flow of the entire program.
 *  @author Allen Madsen (acm1546)
 */
public class Bank {

  /**
   * Entry point of prgram. Controlls delegation of input to tasks.
   *
   * @param args string array containing the number of tellers.
   */
  public static void main(String[] args) {
    int numberOfTellers = 0;
    if(args.length != 1){
      System.err.println("You must provide the number of tellers.");
      System.exit(1);
    }
    try {
      numberOfTellers = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
      System.err.println("You must provide a valid number of tellers.");
      System.exit(1);
    }

    Tellers tellers = Tellers.getInstance();
    tellers.setNumberOfTellers(numberOfTellers);

    Scanner scan = new Scanner(System.in);

    boolean quit = false;
    while (!quit) {
      String command = scan.nextLine();
      quit = interpretCommand(command);
    }

    tellers.closeBank();
    Tellers.println("\nEveryone has been serviced", false);
  }

  /**
   * This method determines what command was passed in and acts on it.
   * @param command the operation to complete
   * @return whether or not to quit the program
   */
  private static boolean interpretCommand(String command) {
    if ("open".equalsIgnoreCase(command)) {
      Tellers.getInstance().openBank(); //open the bank
    } else if ("quit".equalsIgnoreCase(command)) {
      return true; //quit
    } else {
      String[] inputs = command.split(" ", 2);
      if (inputs.length == 2 && "arrive".equalsIgnoreCase(inputs[0])) {
        //start a new customer through his actions
        Customer c = new Customer();
        c.setName(inputs[1]);
        c.start();
      } else {
        Tellers.println("Error: invalid input", true); //bad input
      }
    }
    return false; //keep going
  }
}