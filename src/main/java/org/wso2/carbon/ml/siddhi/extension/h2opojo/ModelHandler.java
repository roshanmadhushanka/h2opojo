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

import hex.ModelCategory;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class ModelHandler {
    private EasyPredictModelWrapper model;
    private String[] column_names;
    private final String H2O_GENMODEL_PATH = "/repository/components/lib/h2o-genmodel.jar";

    public ModelHandler(String modelStorageLoc) throws ClassNotFoundException, IllegalAccessException,
            InstantiationException, IOException {

        String modelName = extractModelName(modelStorageLoc);

        File f = new File(modelStorageLoc);
        URL[] cp = { f.toURI().toURL(), new File(H2O_GENMODEL_PATH).toURI().toURL() };
        URLClassLoader urlcl = new URLClassLoader(cp, this.getClass().getClassLoader());

        hex.genmodel.GenModel rawModel = (hex.genmodel.GenModel)
                urlcl.loadClass(modelName).newInstance();

        this.model = new EasyPredictModelWrapper(rawModel);
        this.column_names = rawModel._names;
    }

    public Object predict(Object[] data) throws PredictException {

        // Generate input data row
        RowData row = new RowData();
        for(int i=0; i<data.length; i++){
            row.put(column_names[i], data[i].toString());
        }

        // Select specific model
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

    private String extractModelName(String modelStoragePath) {
        // Extract the model name from the given path
        int index = modelStoragePath.lastIndexOf(File.separator);
        return modelStoragePath.substring(index + 1);
    }
}

