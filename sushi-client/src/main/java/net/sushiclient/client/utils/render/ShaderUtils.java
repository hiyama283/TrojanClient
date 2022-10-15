/*
 * Contact github.com/hiyama283
 * Project "sushi-client"
 *
 * Copyright 2022 hiyama283
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

package net.sushiclient.client.utils.render;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL20.*;

public class ShaderUtils {

    private static final String shadersDir = "assets/sushi/shaders/";
    public static final int RAINBOW;

    static {
        int rainbow = 0;
        try {
            rainbow = loadGLSL("rainbow.vsh", "rainbow.fsh");
        } catch (IOException e) {
            e.printStackTrace();
        }
        RAINBOW = rainbow;
    }

    private static int loadGLSL(String vertex, String fragment) throws IOException {
        int vertexId = glCreateShader(GL_VERTEX_SHADER);
        int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        String vertexSrc = IOUtils.toString(ShaderUtils.class.getClassLoader().getResource(shadersDir + vertex), StandardCharsets.UTF_8);
        String fragmentSrc = IOUtils.toString(ShaderUtils.class.getClassLoader().getResource(shadersDir + fragment), StandardCharsets.UTF_8);

        glShaderSource(vertexId, vertexSrc);
        glShaderSource(fragmentId, fragmentSrc);
        glCompileShader(vertexId);
        glCompileShader(fragmentId);

        int programId = glCreateProgram();
        glAttachShader(programId, vertexId);
        glAttachShader(programId, fragmentId);
        glLinkProgram(programId);

        String vertInfo = glGetShaderInfoLog(vertexId, 200);
        String fragInfo = glGetShaderInfoLog(fragmentId, 200);
        String programInfo = glGetProgramInfoLog(programId, 200);
        if (vertInfo.length() != 0) System.err.println(vertInfo);
        if (fragInfo.length() != 0) System.err.println(fragInfo);
        if (programInfo.length() != 0) System.err.println(programInfo);

        glDeleteShader(vertexId);
        glDeleteShader(fragmentId);

        return programId;
    }

}
