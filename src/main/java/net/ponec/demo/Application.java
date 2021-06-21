/*
 * Copyright 2020-2021 Pavel Ponec, https://github.com/pponec/demo-ajax
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
package net.ponec.demo;

import io.javalin.Javalin;
import io.javalin.core.compression.CompressionStrategy;
import org.slf4j.*;

import java.util.regex.Pattern;
import net.ponec.demo.service.*;

/**
 * https://www.infoq.com/news/2019/07/javalin/
 */
public class Application {

    private static final Pattern PORT_PATTERN = Pattern.compile("\\d+");

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private static void startJavalin(int port) {
        Javalin app = Javalin.create(config -> {
            config.defaultContentType = "text/html";
            config.autogenerateEtags = true;
            config.addStaticFiles("/static");
            config.asyncRequestTimeout = 10_000L;
            config.compressionStrategy(CompressionStrategy.GZIP);
            config.enforceSsl = true;
        }).start(port);

        LOGGER.trace("Application is running on the port: " + port);

        app.get("/regexp", ctx -> {
            new RegexpApp().doGet(ctx.req, ctx.res);

        });
        app.post("/regexp", ctx -> {
            new RegexpApp().doPost(ctx.req, ctx.res);
        });
    }

    /** Gete a port from parameters */
    private static int getPort(int defaultPort, String... args) {
        return args.length > 0 && PORT_PATTERN.matcher(args[0]).matches()
                ? Integer.parseInt(args[0])
                : defaultPort;
    }

    public static void main(String... args) {
        new Application().startJavalin(getPort(8080, args));
    }
}