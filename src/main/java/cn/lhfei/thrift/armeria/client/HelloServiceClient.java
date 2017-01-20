/*
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.lhfei.thrift.armeria.client;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linecorp.armeria.client.ClientBuilder;
import com.linecorp.armeria.client.Clients;
import com.linecorp.armeria.client.http.HttpClient;
import com.linecorp.armeria.client.http.retrofit2.ArmeriaRetrofit;
import com.linecorp.armeria.client.logging.LoggingClient;
import com.linecorp.armeria.common.http.AggregatedHttpMessage;
import com.linecorp.armeria.common.http.HttpHeaderNames;
import com.linecorp.armeria.common.http.HttpHeaders;
import com.linecorp.armeria.common.http.HttpMethod;
import com.linecorp.armeria.common.http.HttpRequest;
import com.linecorp.armeria.common.http.HttpResponse;
import com.linecorp.armeria.common.util.SafeCloseable;

import cn.lhfei.thrift.armeria.server.HelloService;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @version 0.1
 *
 * @author Hefei Li
 *
 * @since Jan 20, 2017
 */

public class HelloServiceClient {
	private static final Logger log = LoggerFactory.getLogger(HelloServiceClient.class);

	public static void main(String[] args) {
		armeriaClient();
		
		retrofit2Client();
	}
	
	
	public static void basicClient() {
		try {
			HelloService.Iface helloService = new ClientBuilder("tbinary+http://127.0.0.1:8080/hello")
					.defaultResponseTimeoutMillis(10000)
					.decorator(HttpRequest.class, HttpResponse.class, LoggingClient::new)
					.build(HelloService.Iface.class); // or AsyncIface.class

			String greeting = helloService.hello("Armerian World");

			assert greeting.equals("Hello, Armerian World!");

			log.info("===={}", greeting);

		} catch (TException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	public static void armeriaClient() {
		HttpClient httpClient = Clients.newClient("none+http://127.0.0.1:8080/", HttpClient.class);
		
		AggregatedHttpMessage textResponse = httpClient.get("/images/far/bar.txt").aggregate().join();
		
		log.info("{}", new String(textResponse.content().array()));

		AggregatedHttpMessage getJson = AggregatedHttpMessage.of(
		        HttpHeaders.of(HttpMethod.GET, "/images/far/bar.json")
		                   .set(HttpHeaderNames.ACCEPT, "application/json"));

		AggregatedHttpMessage jsonResponse = httpClient.execute(getJson).aggregate().join();
		
		
		log.info("{}", new String(jsonResponse.content().array()));
	}
	
	
	public static void retrofit2Client() {
		try {
			HttpClient httpClient = Clients.newClient("none+http://localhost:8080/userInfo", HttpClient.class);
			
			Retrofit retrofit = ArmeriaRetrofit.builder(httpClient)
					.addConverterFactory(JacksonConverterFactory.create())
					.build();
			
			MyService service = retrofit.create(MyService.class);
			
			String uid = "li";
			UserInfo userInfo = service.getUser(uid);
			
			log.info("name: {}", uid, userInfo.getName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
