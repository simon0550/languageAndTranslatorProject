import java.util.Scanner;

public class AppRuntime {
  private static final Scanner scanner = new Scanner(System.in);

  public static int read_INT() {
    if (scanner.hasNextInt()) {
      return scanner.nextInt();
    }
    return 0;
  }
}