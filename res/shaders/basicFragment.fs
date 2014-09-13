#version 330

in vec2 texCoord0;

uniform vec3 colour;
uniform sampler2D sampler;

void main(){
	vec4 textureColour = texture2D(sampler, texCoord0.xy);
	if(textureColour == 0)
		gl_FragColor = vec4(colour, 1);
	else
		gl_FragColor = texture2D(sampler, texCoord0.xy) * vec4(colour,1);
}