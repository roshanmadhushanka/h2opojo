import hex.genmodel.easy.exception.PredictException;
import org.wso2.carbon.ml.siddhi.extension.h2opojo.ModelHandler;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by wso2123 on 11/5/16.
 */
public class Test {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException, PredictException {
//        ModelHandler modelHandler = new ModelHandler("DeepLearning_model_python_1478324086305_1");
//        Object[][] data = {{-0.0006,0.0004,31.0,1.0,47.288,521.924,2388.064,8.4262,642.266,38.924,23.3735,1586.998,1399.918,554.116,2388.082,2.49669161091,4.60114507487},
//                {0.0018,-0.0001,49.0,2.0,47.506,521.596,2388.102,8.44226,642.672,38.902,23.27434,1588.342,1406.778,553.748,2388.108,1.94949839702,3.16007420799},
//                {-0.0016,0.0004,126.0,3.0,47.706,520.722,2388.174,8.45868,642.874,38.686,23.24412,1592.64,1413.35,552.702,2388.166,2.75528238117,3.23718643269},
//                {0.0012,0.0004,106.0,4.0,47.642,521.434,2388.114,8.45182,642.786,38.758,23.25156,1591.918,1410.736,553.444,2388.116,2.58106663998,4.81240521985},
//                {-0.0013,-0.0004,98.0,5.0,47.536,521.062,2388.134,8.43662,642.702,38.81,23.29114,1591.568,1411.796,553.158,2388.122,3.48163237577,3.35417888611},
//                {0.0076,-0.0003,105.0,6.0,47.516,521.382,2388.116,8.44874,642.586,38.774,23.28456,1588.934,1406.044,553.29,2388.146,2.83751229072,2.67699010084},
//                {0.0016,-0.0001,160.0,7.0,47.35,522.192,2388.054,8.4241,642.216,38.956,23.33198,1587.022,1407.372,553.876,2388.028,3.30292733798,5.6397153297},
//                {0.0016,-0.0005,166.0,8.0,47.546,520.892,2388.116,8.4539,642.89,38.78,23.29198,1592.436,1410.176,553.184,2388.13,2.31100951967,3.55028956566},
//                {-0.0003,0.0004,55.0,9.0,47.584,521.528,2388.142,8.44332,642.55,38.794,23.33906,1588.58,1407.53,553.252,2388.134,3.37791192899,3.39542869164},
//                {-0.0018,0.0004,192.0,10.0,47.436,521.546,2388.056,8.4301,642.798,38.864,23.2793,1589.724,1403.658,553.574,2388.094,3.58027038085,2.96109591199}};
//
//        long startTime = System.nanoTime();
//        for(Object[] row: data){
//            System.out.println(modelHandler.predict(row));
//        }
//        long endTime = System.nanoTime();
//        System.out.println("Took "+(endTime - startTime)/10.0 + " ns");
//
//        startTime = System.nanoTime();
//        for(Object[] row: data){
//            System.out.println(modelHandler.regressionPredict(row));
//        }
//        endTime = System.nanoTime();
//        System.out.println("Took "+(endTime - startTime)/10.0 + " ns");

        ModelHandler modelHandler = new ModelHandler("/home/wso2123/Documents/MyProjects/h2opojo/model/DRF_model_python_1478682616811_1");
        Object[] data = {5.4, 3.7, 1.5, 0.2};
        System.out.println(modelHandler.predict(data));


    }
}
