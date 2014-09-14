#version 330

in vec2 texCoord0;

out vec4 fragColour;

uniform vec3 colour;
uniform sampler2D sampler;

void main(){
	fragColour = texture(sampler, texCoord0.xy) * vec4(colour,1);
}