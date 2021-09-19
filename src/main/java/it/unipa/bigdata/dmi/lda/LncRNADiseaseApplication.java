package it.unipa.bigdata.dmi.lda;

import it.unipa.bigdata.dmi.lda.config.LDACli;
import it.unipa.bigdata.dmi.lda.enums.Functions;
import it.unipa.bigdata.dmi.lda.interfaces.ModelInterface;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;


public class LncRNADiseaseApplication {

    public static void main(String[] args) throws ParseException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Load user inputs
        ModelInterface model = LDACli.getParsedModel(args);
        if (model != null) {
            Set<Functions> functions = LDACli.getFunction();
            for (Functions f : Functions.order) {
                if (functions.contains(f)) {
                    Method method = model.getClass().getDeclaredMethod(f.label);
                    Object result = method.invoke(model);
                }
            }
        }
    }
}

