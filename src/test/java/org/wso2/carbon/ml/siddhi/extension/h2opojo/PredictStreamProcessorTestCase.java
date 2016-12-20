
/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

public class PredictStreamProcessorTestCase {
    private volatile boolean eventArrived;
    private String modelPath;
    private final String PROJECT_HOME = System.getProperty("user.dir");

    @Before
    public void init() {
        eventArrived = false;
    }

    @Test
    public void binomialPredictionTest() throws InterruptedException, URISyntaxException {
        modelPath = PROJECT_HOME + "/src/test/model/DRF_model_python_1478682616811_1";
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "define stream InputStream "
                + "(sepal_length double, sepal_width double, petal_length double, petal_width double);";

        String query = "@info(name = 'query1') " + "from InputStream#h2opojo:predict('" + modelPath
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

    @Test
    public void regressionPredictionTest() throws InterruptedException, URISyntaxException {
        modelPath = PROJECT_HOME + "/src/test/model/DeepLearning_model_python_1478750840781_1";
        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "define stream InputStream "
                + "(temperature double, vacuum double, preasure double, humidity double);";

        String query = "@info(name = 'query1') " + "from InputStream#h2opojo:predict('" + modelPath
                + "') " + "select * " + "insert into outputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {

            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    Assert.assertEquals("482.54533464519204", inEvents[0].getData(4));
                    eventArrived = true;
                }
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("InputStream");
        executionPlanRuntime.start();
        inputHandler.send(new Object[] {8.34, 40.77, 1010.84, 90.01});
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
