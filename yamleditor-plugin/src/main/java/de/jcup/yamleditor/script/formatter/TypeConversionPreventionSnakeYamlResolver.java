/*
 * Copyright 2020 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.yamleditor.script.formatter;

import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.resolver.Resolver;

class TypeConversionPreventionSnakeYamlResolver extends Resolver {
        
        @Override
        protected void addImplicitResolvers() {
//            we do NOT: addImplicitResolver(Tag.BOOL, BOOL, "yYnNtTfFoO");
            /*
             * INT must be before FLOAT because the regular expression for FLOAT
             * matches INT (see issue 130)
             * http://code.google.com/p/snakeyaml/issues/detail?id=130
             */
            addImplicitResolver(Tag.INT, INT, "-+0123456789");
            addImplicitResolver(Tag.FLOAT, FLOAT, "-+0123456789.");
            addImplicitResolver(Tag.MERGE, MERGE, "<");
            addImplicitResolver(Tag.NULL, NULL, "~nN\0");
            addImplicitResolver(Tag.NULL, EMPTY, null);
            addImplicitResolver(Tag.TIMESTAMP, TIMESTAMP, "0123456789");
            // The following implicit resolver is only for documentation
            // purposes.
            // It cannot work
            // because plain scalars cannot start with '!', '&', or '*'.
            addImplicitResolver(Tag.YAML, YAML, "!&*");
        }
    }