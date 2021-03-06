/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.messaging.handler.annotation.support;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.converter.MessageConverter;
import org.springframework.messaging.support.converter.StringMessageConverter;

import static org.junit.Assert.*;


/**
 * Test fixture for {@link PayloadArgumentResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class PayloadArgumentResolverTests {

	private PayloadArgumentResolver resolver;

	private MethodParameter param;
	private MethodParameter paramNotRequired;
	private MethodParameter paramWithSpelExpression;


	@Before
	public void setup() throws Exception {

		MessageConverter messageConverter = new StringMessageConverter();
		this.resolver = new PayloadArgumentResolver(messageConverter );

		Method method = PayloadArgumentResolverTests.class.getDeclaredMethod("handleMessage",
				String.class, String.class, String.class);

		this.param = new MethodParameter(method , 0);
		this.paramNotRequired = new MethodParameter(method , 1);
		this.paramWithSpelExpression = new MethodParameter(method , 2);
	}


	@Test
	public void resolveRequired() throws Exception {
		Message<?> message = MessageBuilder.withPayload("ABC".getBytes()).build();
		Object actual = this.resolver.resolveArgument(this.param, message);

		assertEquals("ABC", actual);
	}

	@Test
	public void resolveNotRequired() throws Exception {

		Message<?> emptyByteArrayMessage = MessageBuilder.withPayload(new byte[0]).build();
		assertNull(this.resolver.resolveArgument(this.paramNotRequired, emptyByteArrayMessage));

		Message<?> notEmptyMessage = MessageBuilder.withPayload("ABC".getBytes()).build();
		assertEquals("ABC", this.resolver.resolveArgument(this.paramNotRequired, notEmptyMessage));
	}

	@Test(expected=IllegalStateException.class)
	public void resolveSpelExpressionNotSupported() throws Exception {
		Message<?> message = MessageBuilder.withPayload("ABC".getBytes()).build();
		this.resolver.resolveArgument(this.paramWithSpelExpression, message);
	}


	@SuppressWarnings("unused")
	private void handleMessage(
			@Payload String param,
			@Payload(required=false) String paramNotRequired,
			@Payload("foo.bar") String paramWithSpelExpression) {
	}

}
