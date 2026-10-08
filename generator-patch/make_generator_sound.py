#!/usr/bin/env python3
"""Synthesize an original one-second diesel-engine loop; encode with ffmpeg/libvorbis."""
from pathlib import Path
import math
import random
import struct
import subprocess
import tempfile
import wave

output = Path(__file__).resolve().parent / 'resources/assets/zomboid_survival/sounds/generator_running.ogg'
output.parent.mkdir(parents=True, exist_ok=True)
sample_rate = 22050
rng = random.Random(71425)
# Periodic turbulent/mechanical noise keeps the seam continuous.
noise = [rng.uniform(-1, 1) for _ in range(735)]
samples = []
for index in range(sample_rate):
    t = index / sample_rate
    phase = 2 * math.pi * 30 * t
    n = index * len(noise) / sample_rate
    a = int(n); blend = n - a
    turbulence = noise[a] * (1 - blend) + noise[(a + 1) % len(noise)] * blend
    pulse = (0.5 + 0.5 * math.cos(phase)) ** 6
    signal = (0.30 * math.sin(phase) + 0.18 * math.sin(2 * phase + 0.4)
              + 0.12 * math.sin(4 * phase + 0.8) + 0.07 * math.sin(7 * phase)
              + 0.13 * turbulence * (0.3 + 0.7 * pulse)
              + 0.07 * math.sin(2 * math.pi * 90 * t) * pulse)
    samples.append(int(math.tanh(signal * 1.35) * 25000))
with tempfile.TemporaryDirectory() as directory:
    wav = Path(directory) / 'engine.wav'
    with wave.open(str(wav), 'wb') as audio:
        audio.setnchannels(1); audio.setsampwidth(2); audio.setframerate(sample_rate)
        audio.writeframes(struct.pack('<' + 'h' * len(samples), *samples))
    subprocess.run(['ffmpeg', '-hide_banner', '-loglevel', 'error', '-y', '-i', str(wav),
                    '-c:a', 'libvorbis', '-q:a', '5', '-map_metadata', '-1', str(output)], check=True)
print(output)
