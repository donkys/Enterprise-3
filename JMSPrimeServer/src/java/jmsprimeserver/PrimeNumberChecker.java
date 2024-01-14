package jmsprimeserver;

/**
 *
 * @author donky
 */
public class PrimeNumberChecker {
    private static PrimeNumberChecker instance;

    private PrimeNumberChecker() {}

    public static PrimeNumberChecker getInstance() {
        if (instance == null) {
            instance = new PrimeNumberChecker();
        }
        return instance;
    }

    public boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    public int countPrimesInRange(int start, int end) {
        int count = 0;
        for (int i = Math.max(start, 2); i <= end; i++) {
            if (isPrime(i)) {
                count++;
            }
        }
        return count;
    }
    
    public int countPrimesFromRangeString(String range) {
        try {
            String[] parts = range.split(",");
            int start = Integer.parseInt(parts[0].trim());
            int end = Integer.parseInt(parts[1].trim());
            return countPrimesInRange(start, end);
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format in range string.");
            return 0;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error: Invalid range format. Please use the format 'start,end'.");
            return 0;
        }
    }
}
