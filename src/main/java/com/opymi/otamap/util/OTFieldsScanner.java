/*
 * MIT License
 *
 * Copyright (c) 2021. Antonino Verde
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.opymi.otamap.util;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Antonino Verde
 * @since 1.0
 */
public class OTFieldsScanner {
	private static final String CLASS_FIELD = "class";
	private static final String SERIAL_VERSION_UID_FIELD = "serialVersionUID";
	private static final Logger logger = Logger.getLogger(OTFieldsScanner.class.getSimpleName());

	/**
	 * @return list of {@link PropertyDescriptor} of the type {@param type}
	 */
	public static List<PropertyDescriptor> retrievePropertyDescriptors(Class<?> type) {
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(type);
		} catch (IntrospectionException exception) {
			throw new RuntimeException("CANNOT RETRIEVE BEAN INFO", exception);
		}

		return Arrays.stream(beanInfo.getPropertyDescriptors()).filter(property -> {
			String name = property.getName();
			return filterField(name);
		}).collect(Collectors.toList());
	}

	//TODO write documentation
	public static Set<String> findFields(Class<?> toVisit) {
		return visitClassForFields(toVisit).stream().map(Field::getName).collect(Collectors.toSet());
	}

	//TODO write documentation
	private static Set<Field> visitClassForFields(Class<?> toVisit) {
		Set<Field> fields = new HashSet<>();
		Class<?> currentClass = toVisit;
		while (currentClass != Object.class) {
			Arrays.stream(currentClass.getDeclaredFields()).filter(field -> filterField(field.getName())).forEach(fields::add);
			currentClass = currentClass.getSuperclass();
		}
		return fields;
	}

	//TODO write documentation
	private static boolean filterField(String field) {
		return !CLASS_FIELD.equals(field) && !SERIAL_VERSION_UID_FIELD.equals(field);
	}

	//TODO write documentation
	public static void logFieldsType(Class<?> toVisit) {
		logClassInfo(toVisit, "FIELDS", false);
		visitClassForFields(toVisit).stream().forEach(field -> {
			logger.info("TYPE: " + field.getType().getName() + " - NAME: " + field.getName());
		});
		logClassInfo(toVisit, "FIELDS", true);
	}

	//TODO write documentation
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> void logFieldsValue(T item) {
		if (item != null) {
			Class<?> clazz = item.getClass();
			logClassInfo(clazz, "VALUES", false);
			if (Collection.class.isAssignableFrom(clazz)) {
				logger.info("SIZE: " + ((Collection) item).size());
				((Collection) item).forEach(OTFieldsScanner::logFieldsValue);
			} else {
				for (PropertyDescriptor pd : retrievePropertyDescriptors(clazz)) {
					try {
						Object value = pd.getReadMethod().invoke(item);
						logger.info(pd.getName() + ": " + value);
					} catch (Exception e) {
						logger.severe("CANNOT READ FIELD " + pd.getName());
					}
				}
			}
			logClassInfo(clazz, "VALUES", true);
		} else {
			logger.severe("ITEM IS NULL");
		}
	}

	private static void logClassInfo(Class<?> clazz, String toLog, boolean end) {
		String message = "--------------------------------------------------- %s FOR CLASS: %s %s";
		String suffix = end ? "<<<<<<<<<<<<<<<<<<<<<<<<<<<" : ">>>>>>>>>>>>>>>>>>>>>>>>>>>";
		logger.info(String.format(message, toLog, clazz.getSimpleName(), suffix));
	}

}
