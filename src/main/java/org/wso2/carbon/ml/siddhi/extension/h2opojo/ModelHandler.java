package org.wso2.carbon.ml.siddhi.extension.h2opojo;

import hex.ModelCategory;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;

import java.util.Arrays;

/**
 * Created by wso2123 on 11/4/16.
 */
public class ModelHandler {
    private EasyPredictModelWrapper model;
    private String[] column_names;

    public ModelHandler(String classPath) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        hex.genmodel.GenModel rawModel = (hex.genmodel.GenModel) Class.forName(classPath).newInstance();
        this.model = new EasyPredictModelWrapper(rawModel);
        this.column_names = rawModel._names;
    }

    public Object predict(Object[] data) throws PredictException {
        /*
        For general use.
        Select which algorithm to predict in runtime
         */

        //Generate input data row
        RowData row = new RowData();
        for(int i=0; i<data.length; i++){
            row.put(column_names[i], data[i].toString());
        }

        //Select specific model
        if(model.getModelCategory() == ModelCategory.Regression){
            return model.predictRegression(row).value;
        }else if(model.getModelCategory() == ModelCategory.Binomial){
            return model.predictBinomial(row).label;
        }else if(model.getModelCategory() == ModelCategory.Multinomial){
            return model.predictMultinomial(row).label;
        }else if(model.getModelCategory() == ModelCategory.Clustering){
            return model.predictClustering(row).cluster;
        }else if(model.getModelCategory() == ModelCategory.DimReduction){
            return model.predictDimReduction(row).dimensions;
        }else if(model.getModelCategory() == ModelCategory.AutoEncoder){
            return model.predictAutoEncoder(row);
        }else{
            return null;
        }
    }

    public Object regressionPredict(Object[] data) throws PredictException {
        //For regression applications
        RowData row = new RowData();
        for(int i=0; i<data.length; i++){
            row.put(column_names[i], data[i].toString());
        }
        return model.predictRegression(row).value;
    }

    public Object binomialPredict(Object[] data) throws PredictException {
        //For multinomial classification applications
        System.out.println(Arrays.asList(column_names));
        System.out.println(column_names.length);
        System.out.println(Arrays.asList(data));
        System.out.println(data.length);
        RowData row = new RowData();
        for(int i=0; i<data.length; i++){
            row.put(column_names[i], data[i].toString());
        }
        return model.predictBinomial(row).label;
    }

    public Object multinomialPredict(Object[] data) throws PredictException {
        //For multinomial classification applications
        RowData row = new RowData();
        for(int i=0; i<data.length; i++){
            row.put(column_names[i], data[i].toString());
        }
        return model.predictMultinomial(row).label;
    }

    public Object clusteringPredict(Object[] data) throws PredictException {
        //For clustering applications
        RowData row = new RowData();
        for(int i=0; i<data.length; i++){
            row.put(column_names[i], data[i].toString());
        }
        return model.predictClustering(row).cluster;
    }

    public Object dimReductionPredict(Object[] data) throws PredictException {
        //For dimension reduction applications
        RowData row = new RowData();
        for(int i=0; i<data.length; i++){
            row.put(column_names[i], data[i].toString());
        }
        return model.predictDimReduction(row).dimensions;
    }

    public Object autoencoderPredict(Object[] data) throws PredictException {
        //For autoencoder applications
        RowData row = new RowData();
        for(int i=0; i<data.length; i++){
            row.put(column_names[i], data[i].toString());
        }
        return model.predictAutoEncoder(row);
    }
}
