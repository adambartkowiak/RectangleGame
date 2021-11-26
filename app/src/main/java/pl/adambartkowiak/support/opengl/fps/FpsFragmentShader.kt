package pl.adambartkowiak.support.opengl.fps

object FpsFragmentShader {

    const val code = """
            precision mediump float;

            uniform float u_Array[500];
            uniform float u_postAndSize[4];
            
            void main() {
                float pixelCoordX = gl_FragCoord.x - 0.5 - u_postAndSize[0];
                float pixelCoordY = gl_FragCoord.y - 0.5 - u_postAndSize[1];
                int scale = 2;
                int halfScale = 1;
                
                float color = 0.1;
                
                int pixelCoordYi = int(pixelCoordY);
                int valuei = int(u_Array[int(pixelCoordX)]) * scale;
                
                if (pixelCoordYi < valuei + halfScale && pixelCoordYi > valuei - halfScale){
                    color = 1.0;
                } else if (pixelCoordYi < valuei){
                    color = 0.3;
                }

                vec4 fragColor = vec4(0.0, color, 0.0, 0.5);
                gl_FragColor = fragColor;
            }
"""
}