package pl.adambartkowiak.support.opengl.hud

object HudFragmentShader {

    const val code = """
            precision mediump float;
            
            void main() {
                float diff = 0.7;
                gl_FragColor = vec4(diff, diff, diff, 0.5);
            }
"""
}