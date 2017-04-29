package junitexample;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AdditionTest {
    @Test
    public void test1(){
        Addition a = new Addition(1,2,3,4,5,6,7);
        assertEquals(28,a.result());
    }

    @Test
    public void test2(){
        Addition a = new Addition(-7,-6,-5,-4,-3,-2,-1,0,1,2,3,4,5,6,7);
        assertEquals(0,a.result());
    }
}
