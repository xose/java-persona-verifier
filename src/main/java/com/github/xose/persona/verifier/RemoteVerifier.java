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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;

public final class RemoteVerifier implements Verifier {

	public static final String DEFAULT_URL = "https://verifier.login.persona.org/verify";

	private final AsyncHttpClient client;
	private final String verifyUrl;

	public RemoteVerifier() {
		this(DEFAULT_URL);
	}

	public RemoteVerifier(final String verifyUrl) {
		this.verifyUrl = checkNotNull(verifyUrl);
		client = new AsyncHttpClient();
	}

	@Override
	public ListenableFuture<VerifyResult> verify(String assertion, String audience) {
		final SettableFuture<VerifyResult> future = SettableFuture.create();

		Request request = new RequestBuilder("POST").setUrl(verifyUrl)
				.addParameter("assertion", assertion)
				.addParameter("audience", audience).build();

		try {
			client.executeRequest(request, new AsyncCompletionHandler<Response>() {
				@Override
				public Response onCompleted(Response response) throws IOException {
					if (200 != response.getStatusCode()) {
						future.setException(new Exception("HTTP Code " + response.getStatusCode()));
						return response;
					}

					try {
						future.set(VerifyResultParser.fromJSONString(response.getResponseBody()));
					} catch (Throwable e) {
						future.setException(new Exception("JSON parsing error", e));
					}

					return response;
				}

				@Override
				public void onThrowable(Throwable t) {
					future.setException(t);
				}
			});
		} catch (IOException e) {
			future.setException(e);
		}

		return future;
	}

}
