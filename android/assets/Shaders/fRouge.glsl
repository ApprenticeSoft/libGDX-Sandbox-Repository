varying vec4 v_color;				//varying = partagé par le vertex shader et le fragment shader
varying vec2 v_texCoord0;

uniform sampler2D u_sampler2D;		//sampler2D = prend la couleur d'un pixel d'une texture

void main(){
	//gl_FragColor = texture2D(u_sampler2D, v_texCoord0) * v_color;  //Classic passthrough shader
	//gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
	
	//TEST
	gl_FragColor = texture2D(u_sampler2D, v_texCoord0) * vec4(v_color.z, v_color.y, 0.2*v_color.x, v_color.x);
}