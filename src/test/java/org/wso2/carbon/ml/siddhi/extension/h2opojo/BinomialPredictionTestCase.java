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

import java.net.URISyntaxException;

/**
 * Created by wso2123 on 11/7/16.
 */
public class BinomialPredictionTestCase {
    private volatile boolean eventArrived;
    private String modelStorageLocation = "DRF_model_python_1478508251265_1";

    @Before
    public void init() {
        eventArrived = false;
    }

    @Test
    public void binomialPrediction() throws InterruptedException, URISyntaxException {

        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "define stream InputStream "
                + "(sepal_length double, sepal_width double, petal_length double, petal_width double);";

        String query = "@info(name = 'query1') " + "from InputStream#h2oml:predict('" + modelStorageLocation
                + "') " + "select * " + "insert into outputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {

            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    Assert.assertEquals("Iris-setosa", inEvents[0].getData(4));
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("InputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[] {5.2, 3.4, 1.4, 0.2});
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
