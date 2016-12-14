# h2opojo

H2opojo is a java extension, which is designed to use H2O.ai models in WSO2 CEP. This extension only supports for H2O POJO models.

# How does h2opojo work?

In CEP, you have to create an event stream to provide input for the model. Then the model will output the result in 'prediction' stream which is a string

/* Enter a unique ExecutionPlan */
@Plan:name('Prediction')

/* Enter a unique description for ExecutionPlan */
-- @Plan:description('ExecutionPlan')

/* define streams/tables and write queries here ... */

@Import('feature_processed_stream:1.0.0')
define stream model_input (T double, V double, AP double, RH double, AT_ma_5 double, V_ma_5 double, AP_ma_5 double, RH_ma_5 double);

@Export('model_output:1.0.0')
define stream model_output (T double, V double, AP double, RH double, prediction string);

from model_input#h2opojo:predict('ccpp/DRF_model_python_1479702792496_1')
select T, V, AP, RH, prediction
insert into model_output;

