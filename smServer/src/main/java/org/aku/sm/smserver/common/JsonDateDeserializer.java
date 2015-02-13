package org.aku.sm.smserver.common;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Deserialization of a java.util.Date using ISO8601 date format.
 * Use: @JsonDeserialize(using=JsonDateDeserializer.class)
 * 2014-11-02T13:54:34Z
 * T Trenner von Datum und Uhrzeit (time)
 * Als spezieller Wert kann auch ‚Z‘ für UTC (+00:00) 
 * 
 * @author armin
 *
 */
public class JsonDateDeserializer extends JsonDeserializer<Date> {
	 
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
 
	@Override
	public Date deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		try {
			synchronized (dateFormat) {				
				return dateFormat.parse(parser.getText());
			}
		}
		catch (ParseException e) {
			throw new JsonParseException("Could not parse date", parser.getCurrentLocation(), e);
		}
	}
 
}