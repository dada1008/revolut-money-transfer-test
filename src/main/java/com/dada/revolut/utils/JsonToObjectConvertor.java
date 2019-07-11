package com.dada.revolut.utils;


import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonToObjectConvertor {
    private static final ObjectMapper mapper = new ObjectMapper();
    static {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
	}
    public static <T> T jsonToObject(String json, Class<T> objectClass) throws IOException  {
		T t = mapper.readValue(json, objectClass);
		return t;
	}
    
    public static String convertToJson(Object object) throws IOException {
        return mapper.writeValueAsString(object);
    }
}