#version 150
in vec2 texCoord;
out vec4 fragColor;
uniform sampler2D DiffuseSampler;
// Standard entity meshes are encoded in a narrow chroma signature before this pass.
// White texture + fullbright marker keeps skin, armor and light levels from hiding a target.
void main() {
    vec3 source = texture(DiffuseSampler, texCoord).rgb;
    float strength = max(source.r, source.b);
    float signature = abs(source.r * 137.0 - source.b * 239.0);
    float hot = step(0.025, strength) * (1.0 - step(0.95, signature))
              * (1.0 - step(0.015 * strength + 0.002, source.g));
    float luma = dot(source, vec3(0.2126, 0.7152, 0.0722));
    // Bounded lift retains walls and terrain in darkness without white exposure blowout.
    float detail = pow(clamp(luma, 0.0, 1.0), 0.70);
    vec3 cold = mix(vec3(0.015, 0.045, 0.070), vec3(0.24, 0.57, 0.70), detail);
    vec3 red = mix(vec3(0.72, 0.015, 0.005), vec3(1.0, 0.075, 0.025), clamp(strength, 0.0, 1.0));
    vec2 edge = texCoord * 2.0 - 1.0;
    float vignette = 1.0 - 0.17 * smoothstep(0.30, 1.40, dot(edge, edge));
    fragColor = vec4(mix(cold, red, hot) * vignette, 1.0);
}
