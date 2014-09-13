#version 330

const int MAX_POINT_LIGHTS = 4;
const int MAX_SPOT_LIGHTS = 4;

in vec2 texCoord0;
in vec3 normal0;
in vec3 worldPos0;

out vec4 fragColor;

struct BaseLight {
	vec3 colour;
	float intensity;
};

struct DirectionalLight {
	BaseLight base;
	vec3 direction;
};

struct Attenuation {
	float constant;
	float linear;
	float exponent;
};

struct PointLight { 
	BaseLight base;
	Attenuation atten;
	vec3 position;
	float range;
};

struct SpotLight {
	PointLight pointLight;
	vec3 direction;
	float cutoff;
};

uniform vec3 baseColour;
uniform vec3 eyePos;
uniform vec3 ambientLight;
uniform sampler2D sampler;

uniform float specularIntensity;
uniform float specularPower;

uniform DirectionalLight directionalLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

vec4 calcLight(BaseLight base, vec3 direction, vec3 normal) {
	float diffuseFactor = dot(normal, -direction);
	
	vec4 diffuseColour = vec4(0, 0, 0, 0);
	vec4 specularColour = vec4(0, 0, 0, 0);
	
	if(diffuseFactor > 0){
		diffuseColour = vec4(base.colour, 1.0) * base.intensity * diffuseFactor;
		
		vec3 directionToEye = normalize(eyePos - worldPos0);
		vec3 reflectDirection = normalize(reflect(direction, normal));
		
		float specularFactor = dot(directionToEye, reflectDirection);
		specularFactor = pow(specularFactor, specularPower);
		
		if(specularFactor > 0) {
			specularColour = vec4(base.colour, 1.0) * specularIntensity * specularFactor;
		}
	}
	
	return diffuseColour + specularColour;
}

vec4 calcDirectionaLight(DirectionalLight directionalLight, vec3 normal) {
	return calcLight(directionalLight.base, -directionalLight.direction, normal);
}

vec4 calcPointLight(PointLight pointLight, vec3 normal) {
	vec3 lightDirection = worldPos0 - pointLight.position;
	float distanceToPoint = length(lightDirection);
	
	if(distanceToPoint > pointLight.range)
		return vec4(0, 0, 0, 0);
	
	lightDirection = normalize(lightDirection);
	
	vec4 colour = calcLight(pointLight.base, lightDirection, normal);
	
	float attenuation = pointLight.atten.constant +
						pointLight.atten.linear *
						distanceToPoint + pointLight.atten.exponent *
						distanceToPoint * distanceToPoint
						+ 0.0001; //To make sure it never divides by 0 becuase GLSL is weird
						
	return colour / attenuation;
}

vec4 calcSpotLight(SpotLight spotLight, vec3 normal) { 
	vec3 lightDirection = normalize(worldPos0 - spotLight.pointLight.position);
	float spotFactor = dot(lightDirection, spotLight.direction);
	
	vec4 colour = vec4(0, 0, 0, 0);
	
	if(spotFactor > spotLight.cutoff) {
		colour = calcPointLight(spotLight.pointLight, normal) * 
			(1.0 - (1.0 - spotFactor) / (1.0 - spotLight.cutoff));
	}
	
	return colour;
}

void main() {

	vec4 totalLight = vec4(ambientLight,1);
	vec4 colour = vec4(baseColour, 1);
	vec4 textureColor = texture(sampler, texCoord0.xy);

		if(textureColor != vec4(0,0,0,0))
			colour *= textureColor;
			
			vec3 normal = normalize(normal0);
			
		totalLight += calcDirectionaLight(directionalLight, normal);
		
		for(int i = 0; i < MAX_POINT_LIGHTS ; i++)
		if(pointLights[i].base.intensity > 0) {
			totalLight += calcPointLight(pointLights[i], normal);
		}
		
		for(int i = 0; i < MAX_SPOT_LIGHTS ; i++)
		if(spotLights[i].pointLight.base.intensity > 0) {
			totalLight += calcSpotLight(spotLights[i], normal);
		}
		
		fragColor = colour * totalLight;
}