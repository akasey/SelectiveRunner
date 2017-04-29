package junitexample;

import org.junit.runner.RunWith;
import org.junit.runners.SelectiveRunner;

/**
 * Created by akash on 4/29/17.
 */

@RunWith(SelectiveRunner.class)
@SelectiveRunner.EnableLogging("./log.txt")
@SelectiveRunner.SuiteMethods({
        "junitexample.TriangleTest#testScalene",
        "junitexample.AdditionTest#test1",
        "junitexample.FibonacciTest#test",
        "junitexample.TriangleTest#testGiantTriangle",
        "junitexample.AdditionTest#test2"
})
public class SelectiveTest {
}
