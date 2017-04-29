package org.junit.runners;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.ClassMethodTuple;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.annotation.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by akash on 4/4/17.
 */
public class SelectiveRunner extends ParentRunner<ClassMethodTuple> {


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface SuiteMethods {
        String[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface EnableLogging {
        String value();
    }

    /*  Private Properties  */
    private List<ClassMethodTuple> methodTuples;
    private ConcurrentMap<String, ParentRunner> runners;
    private List<String> failedTests = null;
    private LinkedList<String> finishedTests = null;
    private boolean loggingEnabled = false;
    private String loggingFileName = null;

    public SelectiveRunner(Class<?> testClass, RunnerBuilder builder) throws InitializationError {
        super(testClass);
        // classMethodTuples below is temporary this.methodTuples is created in makeRunnerMapAndMethodTuples.
        // To accomodate with Suite.class runner
        List<ClassMethodTuple> classMethodTuples = Collections.unmodifiableList(getAnnotatedClassesAndMethods(testClass));

        makeRunnerMapAndMethodTuples(classMethodTuples, builder);
        checkLoggingEnabled(testClass);
        if (loggingEnabled) {
            failedTests = new ArrayList<String>();
            finishedTests = new LinkedList<String>();
        }
    }

    private void checkLoggingEnabled(Class<?> klass) {
        EnableLogging enabled = klass.getAnnotation(EnableLogging.class);
        if (enabled!=null) {
            loggingEnabled = true;
            loggingFileName = enabled.value();
        }
    }

    private void makeRunnerMapAndMethodTuples(List<ClassMethodTuple> classMethodTuples, RunnerBuilder builder) {
        methodTuples = new LinkedList<ClassMethodTuple>();
        runners = new ConcurrentHashMap<String, ParentRunner>();
        for (ClassMethodTuple methodTuple : classMethodTuples) {
            Class<?> suiteClass = methodTuple.getSuiteClass();
            Runner runner = builder.safeRunnerForClass(suiteClass);
            if ( runner instanceof ParentRunner) {
                if ( runner instanceof Suite) {
                    int i=0;
                    for (Runner childRunner : ((Suite)runner).getChildren() ) {
                        if (childRunner instanceof ParentRunner) {
                            String key = methodTuple.getKey() + childRunner.getClass().toString() + i;
                            methodTuples.add(new ClassMethodTuple(methodTuple.getSuiteClass(),
                                    methodTuple.getMethod(),
                                    key
                                    ));
                            runners.put(key, (ParentRunner)childRunner);
                            i++;
                        }
                    }
                }
                else {
                    methodTuples.add(methodTuple);
                    runners.put(methodTuple.getKey(), (ParentRunner)runner);
                }
            }
        }
    }

    private LinkedList<ClassMethodTuple> getAnnotatedClassesAndMethods(Class<?> klass) throws InitializationError {
        SuiteMethods annotation = klass.getAnnotation(SuiteMethods.class);
        if (annotation == null) {
            throw new InitializationError(String.format("class '%s' must have a SuiteMethods annotation", klass.getName()));
        }
        LinkedList<ClassMethodTuple> tuples = new LinkedList<ClassMethodTuple>();
        for (String eachMethod : annotation.value()) {
            ClassMethodTuple tuple = null;
            try {
                tuple = ClassMethodTuple.fromAnnotation(eachMethod);
            } catch (InitializationError initializationError) {
                System.out.println(initializationError.getMessage());
            } finally {
                if (tuple != null)
                    tuples.add(tuple);
            }
        }
        return tuples;
    }

    @Override
    protected List<ClassMethodTuple> getChildren() {
        return methodTuples;
    }

    protected Description describeChild(ClassMethodTuple child) {
        ParentRunner runner = runners.get(child.getKey());
        if (runner != null ) {
            return runner.describeChild(child);
        }
        else
            throw new RuntimeException(child.getSuiteClass() + "'s Test Runner isn't Junit4's Parent Runner");
    }

    protected void runChild(ClassMethodTuple child, RunNotifier notifier) {
        ParentRunner runner = runners.get(child.getKey());
        if (runner != null ) {
            runner.runChild(child, notifier);
        }
        else
            throw new RuntimeException(child.getSuiteClass() + "'s Test Runner isn't Junit4's Parent Runner");
    }

    @Override
    public void run(RunNotifier notifier) {
        if (loggingEnabled ) {
            notifier.addListener(new LogWriter());
        }
        super.run(notifier);
    }

    private class LogWriter extends RunListener {
        public void testFailure(Failure failure) throws Exception {
            failedTests.add(failure.getDescription().getClassName() + "#" + failure.getDescription().getMethodName());
        }

        public void testFinished(Description description) throws Exception {
            finishedTests.add(description.getClassName() + "#" + description.getMethodName());
        }

        @Override
        public void testRunFinished(Result result) throws Exception {
            BufferedWriter writer = new BufferedWriter(new FileWriter(loggingFileName));

            for (String t : finishedTests) {
                if (failedTests.contains(t)) {
                    writer.write(t + "\t" + "-1\n");
                } else {
                    writer.write(t + "\t" + "1\n");
                }
            }
            writer.flush();
            writer.close();
        }
    }
}
