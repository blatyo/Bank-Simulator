import java.util.Date;
import java.util.Random;

/**
 * This class simulates a customer visitiing a bank.
 *
 * @author Allen Madsen (acm1546)
 */
public class Customer extends Thread {
  private Tellers tellers;
  private static Random r;

  /**
   * Creates a customer and sets up class variables
   */
  public Customer(){
    tellers = Tellers.getInstance();
    //Use a single random so that customers don't exibit very similar behavior using a
    //random with the same or very close seed.
    if(r == null){
      r = new Random(new Date().getTime());
    }
  }

  @Override
  public void run(){
    try {
      //doing the stuff customers do...
      tellers.enterBank();
      tellers.goToTeller();
      sleep(randomly()); //especially sleeping while being serviced by a teller...
      tellers.leaveTeller(); //best nap ever!

    } catch (InterruptedException ex) {}

  }

  /**
   * Randomly generates a number between 2 and 5 seconds in milleseconds
   * @return 2-5 seconds
   */
  private synchronized long randomly(){
    return (r.nextInt(3) + 2) * 1000;
  }
}