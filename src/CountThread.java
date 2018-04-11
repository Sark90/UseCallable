import java.util.concurrent.Callable;

public class CountThread implements Callable<Integer> {
    private int[] arr;
    private int start, elements;
    private int sum = 0;

    public CountThread(int[] arr, int start, int elements) {
        this.arr = arr;
        this.start = start;
        this.elements = elements;
    }

    @Override
    public Integer call() throws Exception {
        for (int i=0; i<elements; i++) {
            sum += arr[start++];
        }
        return sum;
    }
}
