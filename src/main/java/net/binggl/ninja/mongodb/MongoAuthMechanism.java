package net.binggl.ninja.mongodb;

import org.apache.commons.lang.StringUtils;

/**
 * define the possible mongoDB authentication mechanisms
 * @author henrik
 */
public enum MongoAuthMechanism {
	SCRAM_SHA_1("SCRAM-SHA-1"), MONGODB_CR("MONGODB-CR"), MONGO_X509("MONGO-X509");

	private String text;

	MongoAuthMechanism(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public static MongoAuthMechanism fromString(String text) {
		if(StringUtils.isNotEmpty(text)) {
			String t = replaceUnwantedChars(text);
			for (MongoAuthMechanism b : MongoAuthMechanism.values()) {
				String enumText = replaceUnwantedChars(b.text);
				if (t.equalsIgnoreCase(enumText)) {
					return b;
				}
			}
		}
		throw new IllegalArgumentException("No constant with text " + text + " found");
	}
	
	private static String replaceUnwantedChars(String text) {
		String t = text;
		for(String s : new String[]{"_", "-"}) {
			t = t.replace(s, "");
		}
		return t;
	}
}
