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
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wso2123 on 11/7/16.
 */
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
                    complexEventPopulater.populateComplexEvent(event, new Object[]{prediction});
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

        //Model class path
        String classPath;
        if (attributeExpressionExecutors[0] instanceof ConstantExpressionExecutor){
            Object constatObj = ((ConstantExpressionExecutor) attributeExpressionExecutors[0]).getValue();
            classPath = (String) constatObj;
        } else {
            throw new ExecutionPlanValidationException("H2O model class path is not defined");
        }

        //Define model
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

        return Arrays.asList(new Attribute("prediction", Attribute.Type.STRING));
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
