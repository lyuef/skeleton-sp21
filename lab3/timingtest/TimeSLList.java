package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer>ns = new AList<Integer>();
        AList<Integer>ops = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        ns.addLast(1000);
        for(int i=0;i<8;i++) {
            if(i>0) {
                ns.addLast(2*ns.getLast());
            }
            ops.addLast(10000);
            SLList<Integer>test_sllist = new SLList<Integer>();
            for(int j=0;j<ns.size();j++) {
                test_sllist.addFirst(1);
            }
            Stopwatch sw = new Stopwatch();
            for(int j=0;j<10000;j++) {
                test_sllist.getLast();
            }
            times.addLast(sw.elapsedTime());
        }
        printTimingTable(ns,times,ops);
    }
}
