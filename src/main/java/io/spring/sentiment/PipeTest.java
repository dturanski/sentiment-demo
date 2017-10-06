/*
 * Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.spring.sentiment;

import org.springframework.integration.ip.tcp.serializer.AbstractByteArraySerializer;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLengthHeaderSerializer;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;
import org.springframework.integration.ip.tcp.serializer.ByteArrayStxEtxSerializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author David Turanski
 **/
public class PipeTest {

	public static void main(String... args) {
		Map<String, AbstractByteArraySerializer> serializers = new HashMap<>();
		serializers.put("lf",new ByteArrayLfSerializer());
		serializers.put("crlf", new ByteArrayCrLfSerializer());
		serializers.put("stxetx", new ByteArrayStxEtxSerializer());
		serializers.put("l2", new ByteArrayLengthHeaderSerializer(2));
		serializers.put("l4", new ByteArrayLengthHeaderSerializer(4));
		serializers.put("l1", new ByteArrayLengthHeaderSerializer(1));

		String encoder = "l1";
		int pyVersion = 2;

		String python = "python";
		if (pyVersion == 2) {
			python = "/Users/dturanski/miniconda2/bin/" + python;
		}

		AbstractByteArraySerializer serializer = serializers.get(encoder);
		ShellCommandProcessor processor = new ShellCommandProcessor(serializer,
			python + " -u /Users/dturanski/python-dev/springcloudstream/stdiotests/servers/io_echo_"
				+ encoder + ".py");
		processor.setWorkingDirectory("/Users/dturanski/python-dev/springcloudstream/stdiotests");
		try {
			processor.afterPropertiesSet();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		processor.start();
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			String str = "hello, world-" + random.nextInt();
			byte[] bytes = str.getBytes(StandardCharsets.UTF_8);

			System.out.println("SendAndReceive...");

			byte[] result = processor.sendAndReceive(bytes);
			System.out.println(new String(result, Charset.defaultCharset()));

			for (byte b : result) {
				System.out.print(b);
			}
			System.out.println();
		}

		processor.stop();

	}

}
