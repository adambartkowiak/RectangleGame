package pl.adambartkowiak.support.opengl

object GLTFFragmentShader {
    const val code =
        """
            precision mediump float;
            
            uniform mat4 u_viewMatrix;
            uniform vec3 u_cameraPosVec3;
            
            uniform vec4 u_baseColorFactorVec4;
            
            uniform sampler2D u_Texture;
            uniform sampler2D u_normalTexture;
            uniform sampler2D u_roughnessTexture;
            
            uniform float u_lightLocationX;
            uniform float u_lightLocationZ;
            
            varying vec4 v_fragPos;
            varying vec2 v_TexCoordinate;
            varying vec3 v_faceNormal;
//            varying mat3 v_TBN;
            
            void main() {
                float ambient = 1.0;

                vec3 faceNormal = normalize(v_faceNormal);
                vec3 normalMap = texture2D(u_normalTexture, v_TexCoordinate).rgb;
                normalMap = normalize((2.0 * normalMap) - 1.0);
//                normalMap = normalize(v_TBN * normalMap);
                
                
//                vec3 zNormal = normalize(vec3(0.0, 0.2, 1.0));
//                normalMap = normalize(v_TBN * zNormal);

//                vec3 fragmentNormal = normalMap;
                vec3 fragmentNormal = faceNormal;
//                vec4 tempNormalTexture = vec4(fragmentNormal * 0.5 + 0.5, 1.0);
                
                
                //light
                float scale = 3.0;
                vec4 lightPosition = vec4(u_lightLocationX/scale, 20.0/scale, u_lightLocationZ/scale, 1.0);
                vec4 lightDir = normalize(lightPosition - v_fragPos);
                float diff = max(dot(fragmentNormal, lightDir.xyz), 0.0);

                //specular light
                float specularStrength = 2.0;
                vec3 camPos = vec3(u_cameraPosVec3);
                vec3 viewDir = normalize(camPos.xyz - v_fragPos.xyz);
                vec3 reflectDir = reflect(-lightDir.xyz, fragmentNormal);
                float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);
                float specular = specularStrength * spec;
//
//                float roughness = texture2D(u_roughnessTexture, v_TexCoordinate).y;
//                float lightData = ambient + diff + specular * (1.0 - roughness);
                float lightData = ambient + diff + specular;
//
                vec4 textureColor = texture2D(u_Texture, v_TexCoordinate);


//                gl_FragColor = vec4(lightData, lightData, lightData, 1.0) * u_baseColorFactorVec4;

                gl_FragColor = vec4(lightData, lightData, lightData, 1.0) * textureColor;
//                gl_FragColor = vec4(lightData, lightData, lightData, 1.0) * tempNormalTexture;
//                gl_FragColor = tempNormalTexture;
//                gl_FragColor = textureColor;

//                gl_FragColor = vec4(1.0, 1.0, 1.0, 0.5);
//                gl_FragColor = u_baseColorFactorVec4;

            }
            
            """
}