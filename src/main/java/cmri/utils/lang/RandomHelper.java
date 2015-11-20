package cmri.utils.lang;

import java.util.Random;

/**
 * Created by zhuyin on 11/5/15.
 */
public class RandomHelper {
    protected RandomHelper(){}

    /**
     * @param bound the upper bound (exclusive).  Must be positive.
     *  @return the next pseudo random, uniformly distributed {@code int}
     *         value between zero (inclusive) and {@code bound} (exclusive)
     *         from this random number generator's sequence
     */
    public static int rand(int bound){
        return  new Random().nextInt(bound);
    }
    public static int rand(int min, int max){
        return  new Random().nextInt(max - min)+ min;
    }

    public static String rand(String dict, int number){
        Random random = new Random();
        String sRand = "";
        for (int i = 0; i < number; i++) {
            String rand = String.valueOf(dict.charAt(random.nextInt(dict.length())));
            sRand += rand;
        }
        return sRand;
    }
}
