/**
 * Copyright 2013 José Martínez
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.xose.persona.verifier;

import java.util.Date;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public final class VerifyResultParser {
	
	private static final JsonParser parser = new JsonParser();
	
	public final static VerifyResult fromJSONString(String input) {
		JsonObject response = parser.parse(input).getAsJsonObject();
		String status = response.get("status").getAsString();
		if ("okay".equals(status)) {
			String email = response.get("email").getAsString();
			String audience = response.get("audience").getAsString();
			Date expires = new Date(response.get("expires").getAsLong());
			String issuer = response.get("issuer").getAsString();
			return new VerifyResult(email, audience, expires, issuer);
		} else if ("failure".equals(status)) {
			String reason = response.get("reason").getAsString();
			return new VerifyResult(reason);
		}
		throw new JsonSyntaxException("Invalid status");
	}
	
	public final static String toJSONString(VerifyResult input) {
		JsonObject result = new JsonObject();
		result.addProperty("status", input.getStatus().toString());
		if (VerifyResult.Status.OKAY.equals(input.getStatus())) {
			result.addProperty("email", input.getEmail());
			result.addProperty("audience", input.getAudience());
			result.addProperty("expires", input.getExpires().getTime());
			result.addProperty("issuer", input.getIssuer());
		} else if (VerifyResult.Status.FAILURE.equals(input.getStatus())) {
			result.addProperty("reason", input.getReason());
		}
		return result.toString();
	}
	
	private VerifyResultParser() {
	}
	
}
