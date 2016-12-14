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

import hex.genmodel.easy.exception.PredictException;
import org.wso2.siddhi.core.config.ExecutionPlanContext;
import org.wso2.siddhi.core.event.ComplexEventChunk;
import org.wso2.siddhi.core.event.stream.StreamEvent;
import org.wso2.siddhi.core.event.stream.StreamEventCloner;
import org.wso2.siddhi.core.event.stream.populater.ComplexEventPopulater;
import org.wso2.siddhi.core.exception.ExecutionPlanRuntimeException;
import org.wso2.siddhi.core.executor.ConstantExpressionExecutor;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.query.processor.Processor;
import org.wso2.siddhi.core.query.processor.stream.StreamProcessor;
import org.wso2.siddhi.query.api.definition.AbstractDefinition;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.ExecutionPlanValidationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PredictStreamProcessor extends StreamProcessor{
    private ModelHandler modelHandler;

    @Override
    protected void process(ComplexEventChunk<StreamEvent> complexEventChunk, Processor processor, StreamEventCloner streamEventCloner, ComplexEventPopulater complexEventPopulater) {
        while(complexEventChunk.hasNext()){
            StreamEvent event = complexEventChunk.next();
            Object[] data = event.getOutputData();
            Object[] featureValues = Arrays.copyOfRange(data, 0, data.length-1);

            if(data.length != 0){
                try {
                    Object prediction = modelHandler.predict(featureValues);
                    complexEventPopulater.populateComplexEvent(event, new Object[]{ String.valueOf(prediction) });
                } catch (PredictException e) {
                    throw new ExecutionPlanRuntimeException("Error while predicting", e);
                }
            }
        }
        nextProcessor.process(complexEventChunk);
    }

    @Override
    protected List<Attribute> init(AbstractDefinition abstractDefinition, ExpressionExecutor[] expressionExecutors, ExecutionPlanContext executionPlanContext) {
        if (attributeExpressionExecutors.length < 1) {
            throw new ExecutionPlanValidationException("H2O model class path is not defined");
        }

        // Model path
        String classPath;
        if (attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor){
            Object constatObj = ((ConstantExpressionExecutor) attributeExpressionExecutors[0]).getValue();
            classPath = (String) constatObj;
        } else {
            throw new ExecutionPlanValidationException("H2O model path is not defined");
        }

        // Define model
        try {
            modelHandler = new ModelHandler(classPath);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //return Arrays.asList(new Attribute("prediction", Attribute.Type.STRING));
        return Collections.singletonList(new Attribute("prediction", Attribute.Type.STRING));
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public Object[] currentState() {
        return new Object[0];
    }

    @Override
    public void restoreState(Object[] objects) {

    }
}
