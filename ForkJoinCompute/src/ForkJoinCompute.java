import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

public class ForkJoinCompute extends RecursiveTask<Long> {

    private final long[] numbers;
    private final int start;
    private final int end;
    public static final long threshold = 10_000;

    public ForkJoinCompute(long[] numbers) {
        this(numbers, 0, numbers.length);
    }

    private ForkJoinCompute(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute(){
        int len = end - start;
        if (len <= threshold) {
            return add();
        }
        int mid = (end-start)/2;
        ForkJoinCompute task1 = new ForkJoinCompute(numbers, start, mid);
        ForkJoinCompute task2 = new ForkJoinCompute(numbers, mid, end);

        task1.fork(); //start asynchronously
        Long task2Result = task2.compute();
        Long task1Result = task1.join();

        return task1Result + task2Result;
    }

    private Long add() {
        long result = 0;
        for (int i = start; i < end; ++i) {
            result += numbers[i];
        }
        return result;
    }

    public static Long StartForkJoinCompute(long n) {
        long[] numbers = LongStream.rangeClosed(1, n).toArray();
        ForkJoinTask<Long> task = new ForkJoinCompute(numbers);
        return new ForkJoinPool().invoke(task);
    }

    public static void main(String[] args) {
        System.out.println(StartForkJoinCompute(1_000_000));
    }
}
