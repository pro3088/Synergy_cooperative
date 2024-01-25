package com.synergy.synergy_cooperative.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;

public class CustomBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String rawValue = p.getValueAsString();
        try {
            DecimalFormat format = new DecimalFormat();
            format.setParseBigDecimal(true);
            return (BigDecimal) format.parse(rawValue);
        } catch (ParseException e) {
            throw new IOException("Error parsing BigDecimal", e);
        }
    }
}
