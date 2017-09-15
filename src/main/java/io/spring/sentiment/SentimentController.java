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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author David Turanski
 **/

@RestController
@Import(TcpClientProcessorConfiguration.class)
public class SentimentController {
	private static Logger log = LoggerFactory.getLogger(SentimentController.class);

	@Autowired
	private SentimentService sentimentService;

	@PostMapping("/sentiments")
	public String sentiments(@RequestBody String tweets) {
		log.info("request {}",tweets);
		return sentimentService.getSentiments(tweets);
	}
}
