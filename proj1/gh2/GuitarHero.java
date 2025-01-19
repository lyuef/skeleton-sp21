package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    public static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        GuitarString[] strings = new GuitarString[37];
        for(int i=0;i<37;i++) {
            strings[i] = new GuitarString(440.0*Math.pow(2,(i-24.0)/12.0));
        }
        //StdAudio.play(10000);
        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int whi = -1;
                for(int i=0;i<37;i++) {
                    if(key==keyboard.charAt(i)) {
                        whi = i;
                        break;
                    }
                }
                if(whi==-1) continue;
                strings[whi].pluck();
            }

            /* compute the superposition of samples */
            double sample = 0.0;
            for(int i=0;i<37;i++)
                sample += strings[i].sample();

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for(int i=0;i<37;i++)
                strings[i].tic();
        }
    }
}
