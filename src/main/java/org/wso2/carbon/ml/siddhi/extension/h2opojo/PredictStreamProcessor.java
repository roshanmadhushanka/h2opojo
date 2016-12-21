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
import org.wso2.siddhi.core.exception.ExecutionPlanCreationException;
import org.wso2.siddhi.core.exception.ExecutionPlanRuntimeException;
import org.wso2.siddhi.core.executor.ConstantExpressionExecutor;
import org.wso2.siddhi.core.executor.ExpressionExecutor;
import org.wso2.siddhi.core.query.processor.Processor;
import org.wso2.siddhi.core.query.processor.stream.StreamProcessor;
import org.wso2.siddhi.query.api.definition.AbstractDefinition;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.ExecutionPlanValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PredictStreamProcessor extends StreamProcessor{
    private ModelHandler modelHandler;
    private boolean attributeSelectionAvailable;
    private HashMap<Integer, Integer> attributeIndexMapper;

    @Override
    protected void process(ComplexEventChunk<StreamEvent> complexEventChunk, Processor processor,
                           StreamEventCloner streamEventCloner,
                           ComplexEventPopulater complexEventPopulater) {

        while(complexEventChunk.hasNext()){
            StreamEvent event = complexEventChunk.next();
            Object[] data = event.getOutputData();
            Object[] featureValues;

            if (attributeSelectionAvailable) {
                featureValues = new Object[attributeIndexMapper.size()];

                // Reorder and filter events
                for ( int i :attributeIndexMapper.keySet()){
                    featureValues[i] = data[attributeIndexMapper.get(i)];
                }

            } else {
                featureValues = Arrays.copyOfRange(data, 0, data.length-1);
            }

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
    protected List<Attribute> init(AbstractDefinition abstractDefinition,
                                   ExpressionExecutor[] expressionExecutors,
                                   ExecutionPlanContext executionPlanContext) {

        if (attributeExpressionExecutors.length < 1) {
            throw new ExecutionPlanValidationException("H2O model class path is not defined");
        } else if (attributeExpressionExecutors.length == 1) {
            attributeSelectionAvailable = false;
        } else {
            attributeSelectionAvailable = true;
            attributeIndexMapper = new HashMap<Integer, Integer>();
        }

        // Model path
        String modelStorageLocation;
        if (attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor){
            Object constantObj = ((ConstantExpressionExecutor) attributeExpressionExecutors[0]).getValue();
            modelStorageLocation = (String) constantObj;
        } else {
            throw new ExecutionPlanValidationException("H2O model is not valid");
        }

        // Define model
        try {
            modelHandler = new ModelHandler(modelStorageLocation);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Attribute Index Map
        if (attributeSelectionAvailable) {
            ArrayList<String> featureNames = new ArrayList<String>(Arrays.asList(modelHandler.getFeatureNames()));
            ExpressionExecutor[] selectedAttributes = Arrays.copyOfRange(attributeExpressionExecutors,
                    1, attributeExpressionExecutors.length);

            for(int i=0; i<selectedAttributes.length; i++){
                if ( selectedAttributes[i] instanceof ConstantExpressionExecutor) {

                    ConstantExpressionExecutor constantObj = (ConstantExpressionExecutor) selectedAttributes[i];
                    String featureName = (String) constantObj.getValue();

                    int index = featureNames.indexOf(featureName);

                    if (index != -1) {
                        attributeIndexMapper.put(index, i);
                    }
                } else {
                    throw new ExecutionPlanValidationException("Attribute should be a constant");
                }
            }

            // Check whether number of model attributes are equal to given attributes
            if ( attributeIndexMapper.size() != featureNames.size()){
                throw new ExecutionPlanValidationException("Number of selected attributes are not equal " +
                        "to the number of attributes in the model");
            }

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

    private void logError(Exception e) {
        log.error("Error while retrieving ML-model : " + modelHandler.getModelName(), e);
        throw new ExecutionPlanCreationException(
                "Error while retrieving ML-model : " + modelHandler.getModelName() + "\n" + e.getMessage());
    }
}
