package pl.adambartkowiak.support.opengl.hud

object HudVertextShader {

    const val code = """
            precision mediump float;
            
            attribute vec3 a_Position;
                        
            void main() {
                gl_Position = vec4(a_Position, 1.0);
            }
"""
}