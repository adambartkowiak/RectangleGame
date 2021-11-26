package pl.adambartkowiak.support.opengl


object GLTFVertextShader {
    const val code =
        """ 
            precision mediump float;
            
            uniform mat4 u_modelMatrix;
            uniform mat4 u_viewMatrix;
            uniform mat4 u_projectionMatrix;
            
            uniform vec4 u_baseColorFactorVec4;
            
            uniform float u_lightLocationX;
            uniform float u_lightLocationZ;
            uniform mat4 u_jointMat[100];
            
            attribute vec3 a_Position;
            attribute vec2 a_TextureMapping;
            attribute vec4 a_Normal;
            attribute vec4 a_SkinJoint;
//            attribute vec4 a_SkinWeight;
//            attribute vec3 a_Tangent;
            
            varying vec4 v_fragPos;
            varying vec2 v_TexCoordinate;
            varying vec3 v_faceNormal;
//            varying mat3 v_TBN;
            
            void main() {
                
                //with skeleton
//                mat4 skinMat = a_SkinWeight.x * u_jointMat[int(a_SkinJoint.x)] +
//                a_SkinWeight.y * u_jointMat[int(a_SkinJoint.y)] +
//                a_SkinWeight.z * u_jointMat[int(a_SkinJoint.z)] +
//                a_SkinWeight.w * u_jointMat[int(a_SkinJoint.w)];
                
                //without skeleton
                mat4 skinMat = mat4(1.0, 0.0, 0.0, 0.0, 
                0.0, 1.0, 0.0, 0.0, 
                0.0, 0.0, 1.0, 0.0, 
                0.0, 0.0, 0.0, 1.0);


                vec4 vertexNormal = a_Normal;
//                vec4 vertexNormal = a_Normal*skinMat;
//                vec3 normalizedTangent = normalize(a_Tangent);
//                vec4 vertexTangent = vec4(normalizedTangent, 1.0)*skinMat;
                
                v_fragPos = vec4(u_modelMatrix * vec4(a_Position, 1.0));
                v_TexCoordinate = a_TextureMapping;
                v_faceNormal = normalize(vec3(u_modelMatrix * vec4(vec3(vertexNormal), 0.0)));
                

                //TBN matrix
//                vec3 T = normalize(vec3(vertexTangent));
//                vec3 N = v_faceNormal;
//                T = normalize(T - dot(T, N) * N);
//                vec3 B = normalize(cross(N, T));
//
//                v_TBN = mat3(T, B, N);
                
                
                vec4 pos = u_viewMatrix * u_modelMatrix * skinMat * vec4(a_Position, 1.0);
                gl_Position = u_projectionMatrix * pos; 
            }
            """
}