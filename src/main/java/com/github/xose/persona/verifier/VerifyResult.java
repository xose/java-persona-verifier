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

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

@Immutable
public final class VerifyResult {
	
	public static enum Status {
		OKAY, FAILURE;
		
		public static Status fromDomString(String input) {
			return Status.valueOf(input.toUpperCase());
		}

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}
	
	private final Status status;
	
	// OKAY
	private final String email;
	private final String audience;
	private final Date expires;
	private final String issuer;
	
	// FAILURE
	private final String reason;
	
	public VerifyResult(String email, String audience, Date expires, String issuer) {
		this.status = Status.OKAY;
		this.email = Preconditions.checkNotNull(email);
		this.audience = Preconditions.checkNotNull(audience);
		this.expires = Preconditions.checkNotNull(expires);
		this.issuer = Preconditions.checkNotNull(issuer);
		this.reason = null;
	}
	
	public VerifyResult(String reason) {
		this.status = Status.FAILURE;
		this.email = null;
		this.audience = null;
		this.expires = null;
		this.issuer = null;
		this.reason = Preconditions.checkNotNull(reason);
	}

	public final Status getStatus() {
		return status;
	}

	public final String getEmail() {
		return email;
	}

	public final String getAudience() {
		return audience;
	}

	public final Date getExpires() {
		return expires;
	}

	public final String getIssuer() {
		return issuer;
	}

	public final String getReason() {
		return reason;
	}
	
	@Override
	public final String toString() {
		Objects.ToStringHelper helper = Objects.toStringHelper(this).add("status", status.toString());
		if (Status.OKAY.equals(status)) {
			helper.add("email", email);
			helper.add("audience", audience);
			helper.add("expires", expires);
			helper.add("issuer", issuer);
		} else if (Status.FAILURE.equals(status)) {
			helper.add("reason", reason);
		}
		return helper.toString();
	}
}
