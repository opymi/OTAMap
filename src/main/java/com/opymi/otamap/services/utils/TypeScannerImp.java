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

package com.opymi.otamap.services.utils;


import com.opymi.otamap.entry.services.TypeScanner;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Type Scanner
 *
 * @author Antonino Verde
 * @since 1.0
 */
public class TypeScannerImp implements TypeScanner {
	private static final String CLASS_FIELD = "class";
	private static final String SERIAL_VERSION_UID_FIELD = "serialVersionUID";

	@Override
	public List<PropertyDescriptor> retrievePropertyDescriptors(Class<?> type) {
		BeanInfo beanInfo = createBeanInfoInstance(type);
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

		return Arrays.stream(propertyDescriptors)
				.filter(property -> isNotJBaseField(property.getName()))
				.collect(Collectors.toList());
	}

	/**
	 * Create an instance of {@link BeanInfo}
	 *
	 * @param type
	 * @return BeanInfo instance
	 * @throws RuntimeException
	 */
	private BeanInfo createBeanInfoInstance(Class<?> type) {
		try {
			return Introspector.getBeanInfo(type);
		} catch (IntrospectionException exception) {
			throw new RuntimeException("CANNOT RETRIEVE BEAN INFO", exception);
		}
	}

	@Override
	public Set<String> retrieveDeclaredFieldsNames(Class<?> type) {
		return retrieveDeclaredFields(type).stream()
				.map(Field::getName)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<Field> retrieveDeclaredFields(Class<?> type) {
		Set<Field> fields = new HashSet<>();

		Class<?> currentClass = type;
		while (currentClass != Object.class) {
			Arrays.stream(currentClass.getDeclaredFields())
					.filter(field -> isNotJBaseField(field.getName()))
					.forEach(fields::add);

			currentClass = currentClass.getSuperclass();
		}

		return fields;
	}

	/**
	 * @param field
	 * @return true if field is not java defined field
	 */
	private boolean isNotJBaseField(String field) {
		return !CLASS_FIELD.equals(field) && !SERIAL_VERSION_UID_FIELD.equals(field);
	}

}
