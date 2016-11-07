package org.wso2.carbon.ml.siddhi.extension.h2opojo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by wso2123 on 11/7/16.
 */
public class RegressionStreamProcessorTestCase {
    private volatile boolean eventArrived;
    private String modelStorageLocation = "DeepLearning_model_python_1478324086305_1";

    @Before
    public void init() {
        eventArrived = false;
    }

    @Test
    public void predictFunctionTest() throws InterruptedException, URISyntaxException {

        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "define stream InputStream "
                + "(id long, text string);";

        String query = "@info(name = 'query1') " + "from InputStream#h2opojo:regression('" + modelStorageLocation
                + "') " + "select * " + "insert into outputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {

            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    Assert.assertEquals("0.0", inEvents[0].getData(2));
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("InputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[] {4L, new String("0.0018 -0.0001 49.0 2.0 47.506 521.596 2388.102 8.44226 642.672 38.902 23.27434 1588.342 1406.778 553.748 2388.108 1.94949839702 3.16007420799")});
        sleepTillArrive(20000);
        Assert.assertTrue(eventArrived);
        executionPlanRuntime.shutdown();
        siddhiManager.shutdown();
    }

    private void sleepTillArrive(int milliseconds) {
        int totalTime = 0;
        while (!eventArrived && totalTime < milliseconds) {
            int t = 1000;
            try {
                Thread.sleep(t);
            } catch (InterruptedException ignore) {
            }
            totalTime += t;
        }
    }
}


