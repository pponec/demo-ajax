/*
 * Copyright 2020-2022 Pavel Ponec, https://github.com/pponec
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
package net.ponec.demo.service;

import net.ponec.demo.model.Message;
import org.ujorm.tools.xml.builder.XmlPrinter;
import org.ujorm.tools.xml.config.XmlConfig;

import java.security.SecureRandom;
import org.jetbrains.annotations.NotNull;
import org.ujorm.tools.xml.config.HtmlConfig;

/**
 *
 * @author Pavel Ponec
 */
public class RegexpService {

    /** Max text length */
    private static final int MAX_LENGTH = 1_100;

    /**
     * Highlights the original text according to the regular expression
     * using HTML element {@code <span>}.
     *
     * @param text An original text
     * @param regexp An regular expression
     * @return Raw HTML text.
     */
    public Message highlight(String text, String regexp) {
        try {
            if (text.length() > MAX_LENGTH) {
                String msg = String.format("Shorten text to a maximum of %s characters.",
                        MAX_LENGTH);
                throw new IllegalArgumentException(msg);
            }
            SecureRandom random = new SecureRandom();
            String begTag = "_" + random.nextLong();
            String endTag = "_" + random.nextLong();
            String rawText = text.replaceAll(
                    "(" + regexp + ")",
                    begTag + "$1" + endTag);
            XmlPrinter printer = new XmlPrinter(new StringBuilder(), XmlConfig.ofDoctype(""));
            printer.write(rawText, false);
            return Message.of(printer.toString()
                    .replaceAll(begTag, "<span>")
                    .replaceAll(endTag, "</span>")
            );
        } catch (Exception | OutOfMemoryError e) {
            return Message.of(e);
        }
    }
}
