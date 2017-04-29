# SelectiveRunner
JUnit runner for executing selected test methods in provided order..

**Installation:**

Install the artifact into Maven local.

```bash
mvn clean install
```

Add the dependency in your pom.

```xml
<dependency>
  <groupId>com.chaos</groupId>
  <artifactId>selective-runner</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

Create test suite as below:

```Java
@RunWith(SelectiveRunner.class)
@SelectiveRunner.SuiteMethods({
        "junitexample.TriangleTest#testScalene",
        "junitexample.AdditionTest#test1",
        "junitexample.FibonacciTest#test",
        "junitexample.TriangleTest#testGiantTriangle",
        "junitexample.AdditionTest#test2"
})
public class SelectiveTest {
}
```



I needed this wrapper for a class project for testing prioritization techniques in Java.