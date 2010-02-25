import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**\
 *  This class controlls the flow of customers.
 *
 * @author Allen Madsen (acm1546)
 */
public class Tellers {

  private static Tellers self; //Singleton instance
  private Lock lock;
  private Condition openForBusiness;
  private Condition nextPlease;
  private Condition closedForBusiness;
  private int tellers; //total number of tellers
  private int counter; //number of tellers servicing
  private boolean isBankOpen;

  /**
   * Constructs the tellers object and initializes class variables.
   */
  private Tellers() {
    tellers = 0;
    counter = 0;
    lock = new ReentrantLock();
    openForBusiness = lock.newCondition();
    nextPlease = lock.newCondition();
    closedForBusiness = lock.newCondition();
    isBankOpen = false;
  }

  /**
   * Provides access to the singleton instance of this class.
   * @return singleton instance
   */
  public static Tellers getInstance() {
    if (self == null) {
      self = new Tellers();
    }
    return self;
  }

  /**
   * Sets the total number of tellers.
   * @param tellers number of tellers
   */
  public void setNumberOfTellers(int tellers) {
    lock.lock();
    try {
      this.tellers = tellers;
    } finally {
      lock.unlock();
    }
  }

  /**
   * Blocks customers from entering the bank before it is open. Once it opens, allows
   * customers to enter freely.
   */
  public void enterBank() {
    lock.lock();
    try {
      if (!isBankOpen) {
        try {
          openForBusiness.await();
        } catch (InterruptedException ex) {
        }
      }
      String name = Thread.currentThread().getName();
      Tellers.println(name + " waiting for service", false);
    } finally {
      lock.unlock();
    }
  }

  /**
   * Opens the bank so customers may enter.
   */
  public void openBank() {
    lock.lock();
    try {
      isBankOpen = true;
      openForBusiness.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Closes the bank once everyone has been serviced.
   */
  public void closeBank() {
    lock.lock();
    try {
      while (counter > 0) {
        try {
          closedForBusiness.await();
        } catch (InterruptedException ex) {
        }
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * Blocks a customer from going to a teller unless one is free.
   */
  public void goToTeller() {
    lock.lock();
    try {
      while (tellers == counter) {
        try {
          nextPlease.await();
        } catch (InterruptedException e) {
        }
      }
      ++counter;
      String name = Thread.currentThread().getName();
      Tellers.println("\t" + name + " now being serviced", false);
    } finally {
      lock.unlock();
    }
  }

  /**
   * Opens a teller for other customers to visit
   */
  public void leaveTeller() {
    lock.lock();
    try {
      --counter;
      String name = Thread.currentThread().getName();
      Tellers.println("\t" + name + " done being serviced", false);
      nextPlease.signal();
      closedForBusiness.signal(); //check if all customers have left and try to close
    } finally {
      lock.unlock();
    }
  }

  /**
   * Shared printing method so output doesn't get all jumbled.
   * @param text text to print
   * @param isError whether or not to print it as an error
   */
  public static synchronized void println(String text, boolean isError) {
    if (isError) {
      System.err.println(text);
    } else {
      System.out.println(text);
    }
  }
}