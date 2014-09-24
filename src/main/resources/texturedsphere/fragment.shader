#version 130

uniform sampler2D EarthTexture;

void main()
{
	vec3 lightColor = vec3(texture2D(EarthTexture, gl_TexCoord[0].st));
	gl_FragColor = vec4(lightColor, 1.0);
}
