varying vec4      v_color;
varying vec2      v_texCoord0;

uniform vec4      u_color;
uniform sampler2D u_texture;
uniform sampler2D u_texture1;
uniform float     u_time;
uniform float     u_x_offset;
uniform float     u_y_offset;
uniform float     u_sparkle_intensity;

void main()
{
    vec4 final_color = v_color;
    vec4 colorBlack = vec4(0,0,0,1);
    vec2 new_coord = v_texCoord0;

	// -----------------------------
	// FIRST HEIGHTMAP
	// -----------------------------
	// calculate heightmap scrolling
	// based on the stretched coords
	vec2 new_map_coord1 = new_coord;
	// scroll the map
	new_map_coord1.x += (u_time / 10.0) - u_x_offset;
	new_map_coord1.y -= u_y_offset;
	// clamp the texture to make it repeat
	// indefinitely
	new_map_coord1 = fract(new_map_coord1);
	
	// apply the heighmap displacement
	// to the original texture
	vec4 height_map_color1 = texture2D(u_texture1, new_map_coord1);
	new_coord.xy -= (height_map_color1.rg * 0.02);
	
	// -----------------------------
	// SECOND HEIGHTMAP
	// -----------------------------
	vec2 new_map_coord2 = new_coord;
	new_map_coord2.x += (u_time / 30.0) - u_x_offset;
	new_map_coord2.y -= u_y_offset;
	new_map_coord2 = fract(new_map_coord2);
	
	vec4 height_map_color2 = texture2D(u_texture1, new_map_coord2);
	new_coord.xy += (height_map_color2.rg * 0.02);
	
	// -----------------------------
	// THIRD HEIGHTMAP
	// -----------------------------
	vec2 new_map_coord3 = new_coord;
	new_map_coord3.x -= (u_time / 100.0) - u_x_offset;
	new_map_coord3.y -= (u_time / 30.0) + u_y_offset;
	new_map_coord3 = fract(new_map_coord2);
	
	vec4 height_map_color3 = texture2D(u_texture1, new_map_coord3);
	new_coord.xy -= (height_map_color3.rg * 0.02);
	
	// -----------------------------
	// FOURTH HEIGHTMAP
	// -----------------------------
	vec2 new_map_coord4 = new_coord;
	new_map_coord4.x += (u_time / 85.0) - u_x_offset;
	new_map_coord4.y -= u_y_offset;
	new_map_coord4 = fract(new_map_coord4);
	
	vec4 height_map_color4 = texture2D(u_texture1, new_map_coord4);
	new_coord.xy += (height_map_color4.rg * 0.02);
	
    // -----------------------------
    // STRETCH
    // -----------------------------
    // calculate the stretching factor
    float factor = ((.5 - new_coord.x) * (new_coord.y - u_y_offset));
    // apply the factor and stretch the image
    new_coord.x += factor;

    final_color = (final_color) * texture2D(u_texture, new_coord);
    //Le reflet est moins lumineux
    final_color = mix(final_color, colorBlack, .7 * (1 - 2*new_coord.y));

	if (( new_coord.y - v_texCoord0.y ) > 0.0095/*u_sparkle_intensity*/) {
	  final_color = vec4(1.0, 1.0, 1.0, 1.0);
	  final_color = final_color;
	  gl_FragColor = final_color;
	} else {
	  gl_FragColor = final_color;
	}
}