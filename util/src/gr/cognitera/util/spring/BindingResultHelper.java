package gr.cognitera.util.spring;

import java.util.List;

import org.junit.Assert;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class BindingResultHelper {
    public static final String toString(BindingResult result) {
        List<ObjectError> errors= result.getAllErrors();
        if (errors.isEmpty())
            return "no errors detected";
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("[%d] errors detected: ", errors.size()));
            int i = 0;
            for (Object error: errors) {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    sb.append(String.format("Error %d of %d: field [%s] - rejected value: [%s], msg is: [%s]"
                                            , ++i
                                            , errors.size()
                                            , fieldError.getField()
                                            , fieldError.getRejectedValue()
                                            , fieldError.toString()));
                } else if (error instanceof ObjectError) {
                    ObjectError objectError = (ObjectError) error;
                    sb.append(String.format("Error %d of %d: object [%s] error: [%s]"
                                            , ++i
                                            , errors.size()
                                            , objectError.getObjectName()
                                            , objectError.toString()));
                }
            }
            Assert.assertEquals(errors.size(), i);
            return sb.toString();
        }

    }
}
