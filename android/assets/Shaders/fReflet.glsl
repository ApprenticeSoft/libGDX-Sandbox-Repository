varying vec4 v_color;				//varying = partagé par le vertex shader et le fragment shader
varying vec2 v_texCoord0;

uniform vec2 u_resolution;
uniform sampler2D u_sampler2D;		//sampler2D = prend la couleur d'un pixel d'une texture

void main(){
	vec4 color;
	vec4 colorBlack = vec4(0,0,0,1);
	
	vec2 position = gl_FragCoord.xy / u_resolution;	
	
	if(position.y > 0.24){
		color = texture2D(u_sampler2D, vec2(position.x, position.y));
		gl_FragColor = color;  
	}
	else{
		// Compute the mirror effect.
    	color = texture2D(u_sampler2D, vec2(position.x, (0.48 - position.y)));
    	// Diminution de la luminosité du reflet
    	color = mix(color, colorBlack, .5);
    	gl_FragColor = color;	  	
	}	
}