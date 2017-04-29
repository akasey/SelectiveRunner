package junitexample;

public class Addition {
    private int[] numbers;
    public Addition(int... a) {
        this.numbers = a;
    }

    public int result() {
        int sum = 0;
        for (int n : numbers) {
            sum += n;
        }
        return sum;
    }
}
