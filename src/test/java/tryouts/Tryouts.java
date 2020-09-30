package tryouts;

import org.junit.Test;

import java.util.stream.IntStream;

public class Tryouts {

    @Test
    public void yxcv() {
        IntStream.rangeClosed(1, 4).forEach(val -> {
            System.out.println(val);
        });
    }

}
