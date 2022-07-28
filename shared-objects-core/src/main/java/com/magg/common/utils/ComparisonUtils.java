
package com.magg.common.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class Description goes here.
 */
public class ComparisonUtils {
    private static final Logger LOGGER = LogManager.getLogger(ComparisonUtils.class);

    /**
     * Evaluate a single-level of
     * @param type
     * @param t1
     * @param t2
     * @param <T>
     * @return
     */
    public static <T> boolean haveSamePropertyValues(Class<T> type, T t1, T t2) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                Method m = pd.getReadMethod();
                Object o1 = m.invoke(t1);
                Object o2 = m.invoke(t2);
                if (!Objects.equals(o1, o2)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.warn(String.format("Error comparing type %s. Defaulting to false",
                    type.getName()), e);
            return false;
        }
    }
}
