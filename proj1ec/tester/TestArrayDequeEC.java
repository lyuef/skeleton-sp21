package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.RandomSeq;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

import java.util.Random;

public class TestArrayDequeEC {
    Random rd = new Random();
    @Test
    public void justdoit(){
        ArrayDequeSolution<Integer> ads = new ArrayDequeSolution<Integer>();
        StudentArrayDeque<Integer> sad = new StudentArrayDeque<Integer>();
        StringBuilder mess = new StringBuilder();
        while (true) {
            double whi = rd.nextDouble();
            int add = rd.nextInt();
            if (ads.size() == 0) {
                if (whi < 0.5) {
                    ads.addFirst(add);
                    sad.addFirst(add);
                    mess.append ("addFirst(" + add + ")\n");
                } else {
                    ads.addLast(add);
                    sad.addLast(add);
                    mess.append("addLast(" + add + ")\n");
                }
            } else {
                if (whi < 0.25) {
                    ads.addFirst(add);
                    sad.addFirst(add);
                    mess.append ("addFirst(" + add + ")\n");
                } else if (whi < 0.5) {
                    ads.addLast(add);
                    sad.addLast(add);
                    mess.append("addLast(" + add + ")\n");
                } else if (whi < 0.75) {
                    mess.append("removeFirst()\n");
                    assertEquals(mess.toString(),ads.removeFirst(), sad.removeFirst());
                } else {
                    mess.append("removeLast()\n");
                    assertEquals(mess.toString(),ads.removeLast(), sad.removeLast());
                }
            }
        }
    }
}
