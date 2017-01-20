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

package cn.lhfei.thrift.armeria.server;

import com.linecorp.armeria.common.SerializationFormat;
import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.docs.DocService;
import com.linecorp.armeria.server.http.file.HttpFileService;
import com.linecorp.armeria.server.logging.LoggingService;
import com.linecorp.armeria.server.thrift.THttpService;

/**
 * @version 0.1
 *
 * @author Hefei Li
 *
 * @since Jan 19, 2017
 */

public class HelloServiceApp {

	public static void main(String[] args) {
		HelloService.AsyncIface helloHandler = new MyHelloService();

		ServerBuilder sb = new ServerBuilder();
		sb.port(8080, SessionProtocol.HTTP);
		sb.serviceAt("/hello",
				THttpService.of(helloHandler, SerializationFormat.THRIFT_BINARY).decorate(LoggingService::new));
		
		sb.serviceUnder("/api", new DocService());
		sb.serviceUnder("/images", HttpFileService.forFileSystem("D:/workspace/webapps_thrift/thrift-in-action/src/main/resources/"));
		
		sb.serviceUnder("/static", HttpFileService.forClassPath("/"));
		
		
		Server server = sb.build();
		server.start();

	}

}
